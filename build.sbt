name := "mercury"

organization := "com.gu"

scalaVersion := "2.10.0"

libraryDependencies ++= Seq(
    "org.joda" % "joda-convert" % "1.1" % "provided",
    "joda-time" % "joda-time" % "2.0",
    "net.databinder" %% "unfiltered-filter" % "0.6.5",
    "io.spray" %%  "spray-json" % "1.2.3",
    "org.slf4j" % "slf4j-api" % "1.7.2",
    "org.slf4j" % "slf4j-jdk14" % "1.7.2",
    "com.google.appengine" % "appengine-api-1.0-sdk" % "1.7.2.1",
    "org.jsoup" % "jsoup" % "1.7.1",
    "javax.servlet" % "servlet-api" % "2.5" % "provided",
    "org.scalatest" %% "scalatest" % "1.9.1" % "test"
)

libraryDependencies += "org.eclipse.jetty" % "jetty-webapp" % "7.6.8.v20121106" % "container"

scalacOptions ++= Seq("-feature", "-deprecation")

appengineSettings

Twirl.settings


