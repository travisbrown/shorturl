package net.liquid_silk.shorturl

import _root_.argonaut._, _root_.argonaut.Argonaut._
import com.twitter.finagle.Service
import com.twitter.finagle.redis.Client
import com.twitter.finagle.redis.util.StringToChannelBuffer
import com.github.tototoshi.base62.Base62
import com.twitter.util.Future
import io.finch._
import io.finch.request._
import io.finch.response._
import io.finch.route._
import io.finch.argonaut._
import java.nio.charset.StandardCharsets.UTF_8
import org.apache.commons.validator.routines.UrlValidator

// handle JSON parsing of POST request body
case class UrlToShort(url: String)

object UrlToShort {
  implicit def UrlToShortDecodeJson: DecodeJson[UrlToShort] =
    jdecode1L(UrlToShort.apply)("url")
}

trait Shortener {
  def baseDomain: String
  def base62: Base62
  def client: Client
  def validator: UrlValidator

  val shorten: Service[HttpRequest, HttpResponse] = Service.mk[HttpRequest, HttpResponse] { req =>
    for {
      u <- body.as[UrlToShort].apply(req)
      s <- processUrl(u.url)
    } yield Created(Map("shorturl" -> s).asJson)
  }

  // handle GET request for Base62 shortURL
  def convert(encoded: String): Future[HttpResponse] = fetchShort(encoded).flatMap {
    case Some(url) => Future.value(MovedPermanently.withHeaders("Location" -> url)(url))
    case None => Future.exception(new RuntimeException("Invalid URL"))
  }

  // get actual URL from Base62 version
  private[this] def fetchShort(encoded: String): Future[Option[String]] =
    client.get(StringToChannelBuffer(encoded)).map(_.map(_.toString(UTF_8)))

  // generate the shortened URL
  private[this] def processUrl(url: String): Future[String] = {
    if (validator.isValid(url)) {
      for {
        uniqId <- client.incr(StringToChannelBuffer("SHORTURLID"))
        val encoded = base62.encode(uniqId)
        _ <- client.set(StringToChannelBuffer(encoded), StringToChannelBuffer(url))
      } yield baseDomain + encoded
    } else Future.exception(new Exception("The supplied url is invalid."))
  }
}
