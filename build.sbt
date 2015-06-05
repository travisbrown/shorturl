name := "shorturl"

version := "0.1"

scalaVersion := "2.11.5"

resolvers += "rediscala" at "http://dl.bintray.com/etaty/maven"

libraryDependencies ++= Seq(
  "com.github.finagle" %% "finch-core" % "0.6.0",
  "com.twitter" %% "finagle-httpx" % "6.25.0",
  "com.twitter" %% "finagle-http" % "6.25.0",
  "com.etaty.rediscala" %% "rediscala" % "1.4.0"
)