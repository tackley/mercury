import sbt._
import Keys._

import play.Project._

import sbtassembly.Plugin._
import AssemblyKeys._

object ApplicationBuild extends Build {

    val appName         = "nf-archive"
    val appVersion      = "1.0-SNAPSHOT"

    val playArtifactResources = TaskKey[Seq[(File, String)]]("play-artifact-resources", "Files that will be collected by the deployment-artifact task")
    val playArtifactFile = SettingKey[String]("play-artifact-file", "Filename of the artifact built by deployment-artifact")

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
      .settings(
        playArtifactResources <<= (assembly, baseDirectory) map {
          (assembly, baseDirectory) =>
            Seq(
              assembly -> "packages/ophan-time-machine/%s".format(assembly.getName),
              baseDirectory / "conf" / "deploy.json" -> "deploy.json"
            )
        },
        dist <<= buildDeployArtifact

      )


  private def buildDeployArtifact = (streams, target, playArtifactResources) map {
    (s, target, resources) =>
      val distFile = target / "artifacts.zip"
      s.log.info("Disting " + distFile)

      if (distFile.exists()) {
        distFile.delete()
      }
      IO.zip(resources, distFile)

      // Tells TeamCity to publish the artifact => leave this println in here
      println("##teamcity[publishArtifacts '%s => .']" format distFile)

      s.log.info("Done disting.")
      distFile
  }

}
