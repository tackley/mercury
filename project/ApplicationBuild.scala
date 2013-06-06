import sbt._
import Keys._

import sbtassembly.Plugin._
import AssemblyKeys._

object ApplicationBuild extends Build {

    val appName         = "nf-archive"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "org.jsoup" % "jsoup" % "1.7.2",
      "com.amazonaws" % "aws-java-sdk" % "1.4.3",
      "org.scala-lang" % "scala-reflect" % "2.10.1"
    )

    val main = play.Project(appName, appVersion, appDependencies)
      .settings(assemblySettings: _*)
      .settings(
        scalaVersion := "2.10.1",
        scalacOptions := List("-feature", "-deprecation"),
        ivyXML :=
          <dependencies>
            <exclude org="commons-logging"/>
          </dependencies>,

        mainClass in assembly := Some("play.core.server.NettyServer"),
        jarName in assembly := "app.jar",

        mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) => {
            case "play/core/server/ServerWithStop.class" => MergeStrategy.first
            case x => old(x)
          }}

    )

}
