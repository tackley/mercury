import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "nf-archive"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "org.jsoup" % "jsoup" % "1.6.1",
      "org.elasticsearch" % "elasticsearch" % "0.19.8",
      "org.elasticsearch" % "elasticsearch-cloud-aws" % "1.6.0",
      "com.googlecode.java-diff-utils" % "diffutils" % "1.2.1"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here      
    )

}
