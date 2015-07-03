/*
 * Copyright 2015, by Sean Bollin.
 *
 * A proof of concept URL shortener written in Scala using Redis and Twitter's Finagle/Finch.
 *
 * To build fat jar: sbt assembly
 *
 * Run: java -jar target/scala-2.11/shorturl-assembly-0.1.jar
 *
 * Usage:
 *
 * Make POST request with Content-Type application/json and body {"url":"http://www.urltoshorten.com/arbitraryPath?params=maybe"}
 * Receive corresponding 201 Created response with {"shorturl":"http://serviceDomainName.com/1H"} (a Base62 shortened version of URL generated off INCR Redis key)
 *
 * Make GET request to the "shorturl" provided after creating a shortened version.  This will immediately redirect with a 301 to the actual URL stored in Redis.
 *
 * Requires Redis server to be running locally on default port 6379
 */

package net.liquid_silk.shorturl

import com.github.tototoshi.base62.Base62
import com.twitter.finagle.{ Httpx, Service }
import com.twitter.finagle.httpx.{ Request, Response }
import com.twitter.finagle.redis.Client
import com.twitter.util.Await
import com.typesafe.config.ConfigFactory
import io.finch.response._
import io.finch.route._
import org.apache.commons.validator.routines.UrlValidator

trait ShortenerApp extends Shortener {
  val conf = ConfigFactory.load()
  val baseDomain = conf.getString("shorturl.baseDomain") // service's domain name, e.g. http://urlShortener.com/

  val base62 = new Base62
  val client = Client(":6379")
  val validator = new UrlValidator(List("http", "https").toArray)

  val service: Service[Request, Response] = (
    Post / "shorturl" /> shorten :+:
    Get / string /> convert
  ).toService
}

object Main extends App with ShortenerApp {
  val server = Httpx.serve(":8081", service)

  Await.ready(server) // start server
}
