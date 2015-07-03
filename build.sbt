name := "shorturl"

version := "0.1"

scalaVersion := "2.11.7"
crossScalaVersions := Seq("2.10.5", "2.11.7")

libraryDependencies ++= Seq(
  "com.github.finagle" %% "finch-core" % "0.7.0",
  "com.twitter" %% "finagle-httpx" % "6.25.0",
  "com.twitter" %% "finagle-redis" % "6.25.0",
  "com.github.finagle" %% "finch-argonaut" % "0.7.0",
  "commons-validator" % "commons-validator" % "1.4.0",
  "commons-beanutils" % "commons-beanutils" % "1.9.2",
  "io.argonaut" %% "argonaut" % "6.1",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "com.typesafe" % "config" % "1.3.0"
)
