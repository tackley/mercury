name := "sitemap"

organization := "com.gu"

scalaVersion := "2.9.2"

libraryDependencies ++= Seq(
    "org.joda" % "joda-convert" % "1.1" % "provided",
    "joda-time" % "joda-time" % "2.0",
    "net.databinder" %% "unfiltered-filter" % "0.6.4",
    "org.slf4j" % "slf4j-api" % "1.7.2",
    "org.slf4j" % "slf4j-jdk14" % "1.7.2",
    "com.google.appengine" % "appengine-api-1.0-sdk" % "1.7.2.1",
    "com.github.scala-incubator.io" %% "scala-io-core" % "0.4.1-seq",
    "org.jsoup" % "jsoup" % "1.7.1",
    "javax.servlet" % "servlet-api" % "2.5" % "provided",
    "org.scalatest" %% "scalatest" % "1.8" % "test"
)

resolvers += "Guardian Github Releases" at "http://guardian.github.com/maven/repo-releases"

libraryDependencies += "org.eclipse.jetty" % "jetty-webapp" % "7.6.8.v20121106" % "container"

appengineSettings


