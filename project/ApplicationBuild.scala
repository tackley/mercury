import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "nf-archive"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "org.jsoup" % "jsoup" % "1.7.2",
      "com.amazonaws" % "aws-java-sdk" % "1.4.3"
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
      // Add your own project settings here      
    )

}
