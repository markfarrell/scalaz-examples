name := "NCBI"

version := "1.0"

scalaVersion := "2.11.5"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.1.1"

libraryDependencies += "org.scalaz.stream" %% "scalaz-stream" % "0.6a"

libraryDependencies += "org.http4s" %% "http4s-client" % "0.6.1"

libraryDependencies += "org.http4s" %% "http4s-blazeclient" % "0.6.1"

logLevel := Level.Debug

scalacOptions += "-deprecation"
