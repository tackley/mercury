package lib

import java.io.File
import play.api.Configuration
import com.typesafe.config.ConfigFactory
import com.amazonaws.auth._
import com.amazonaws.internal.StaticCredentialsProvider

object Config {
  private lazy val localPropsFile = System.getProperty("user.home") + "/.ophan-ec2"

//  private lazy val machineSpecificAwsConfig = fileConfig(
//    if (stage contains "DEV") localPropsFile else "/etc/aws.conf"
//  )

  private lazy val machineSpecificAwsConfig = fileConfig(localPropsFile)

  lazy val machineConfigAwsProvider = new AWSCredentialsProvider {

    def getCredentials = new AWSCredentials {
      def getAWSAccessKeyId = machineSpecificAwsConfig.getString("accessKey").orNull
      def getAWSSecretKey = machineSpecificAwsConfig.getString("secretKey").orNull
    }

    def refresh() { }
  }

  // aws will use the first one of these that doesn't throw and returns non-null
  //  typically we'll use the instance profile in prod, and the machine config locally
  lazy val awsProvider = new AWSCredentialsProviderChain(new InstanceProfileCredentialsProvider, machineConfigAwsProvider)

  def fileConfig(filePath: String) = {
    val file = new File(filePath)
    if (!file.exists) throw new Error("Could not find %s" format (filePath))
    Configuration(ConfigFactory.parseFile(file))
  }


}
