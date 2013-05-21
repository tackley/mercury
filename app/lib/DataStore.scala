package lib

import org.joda.time.{DateTimeZone, LocalDate, DateTime}
import play.api.Logger
import com.amazonaws.regions.{Region, Regions}
import com.amazonaws.services.s3.AmazonS3Client
import org.joda.time.format.DateTimeFormatterBuilder
import com.amazonaws.util.StringInputStream
import com.amazonaws.services.s3.model.{CannedAccessControlList, PutObjectRequest, ObjectMetadata}
import java.io.File
import controllers.Screenshot
import java.net.URL

object DataStore {

  case class Path(bucket: String, key: String, ext: String) {
    def url = s"https://$bucket.s3.amazonaws.com/$key"

    def contentType = ext match {
      case "html" => "text/html"
      case "png" => "image/png"
    }
  }


  private val log = Logger(getClass)

  private val s3 = new AmazonS3Client(Config.awsProvider)
  //s3.setRegion(Region.getRegion(Regions.EU_WEST_1))

  private val bucket = "ophan-time-machine"

  lazy val pathDateFormat = new DateTimeFormatterBuilder()
    .appendYear(4, 4)
    .appendLiteral('/')
    .appendMonthOfYear(2)
    .appendLiteral('/')
    .appendDayOfMonth(2)
    .appendLiteral('/')
    .toFormatter

  lazy val fileDateFormat = new DateTimeFormatterBuilder()
      .appendYear(4, 4)
      .appendLiteral('-')
      .appendMonthOfYear(2)
      .appendLiteral('-')
      .appendDayOfMonth(2)
      .appendLiteral('T')
      .appendHourOfDay(2)
      .appendLiteral(':')
      .appendMinuteOfHour(2)
      .toFormatter

  def mkPath(dt: DateTime, pgName: String, qualifier: String, extension: String): Path =
    Path(bucket, s"$pgName/${dt.toString(pathDateFormat)}/${qualifier}_${dt.toString(fileDateFormat)}.$extension", extension)

  def write(p: Path, data: String): String = {
    log.info(s"write to $p...")

    val s = new StringInputStream(data)

    val md = new ObjectMetadata()
    md.setContentLength(s.available())
    md.setContentType(p.contentType)

    val putObjReq = new PutObjectRequest(p.bucket, p.key, s, md)
      .withCannedAcl(CannedAccessControlList.PublicRead)

    s3.putObject(putObjReq)
    log.info(s" -> done, url is ${p.url}")

    p.url
  }

  def write(p: Path, file: File): String = {
    log.info(s"write to $p...")


    val md = new ObjectMetadata()
    md.setContentType(p.contentType)

    val putObjReq = new PutObjectRequest(p.bucket, p.key, file)
      .withMetadata(md)
      .withCannedAcl(CannedAccessControlList.PublicRead)

    s3.putObject(putObjReq)
    log.info(s" -> done, url is ${p.url}")

    p.url
  }

  def years: List[Int] = List(2013)

  def findDataPointsForDay(day: LocalDate): List[Screenshot] = {
    s3.listObjects(bucket, "thumb_")

    val root = scalax.file.Path.fromString(System.getProperty("user.home")) / "png"
    val children = root * "*_thumb.png"

    children.toList.map { x =>
      val fn = x.name.split("_").head
      val dt: DateTime = fileDateFormat.parseDateTime(fn).withZoneRetainFields(DateTimeZone.UTC)
      Screenshot(dt, new URL("http://ophan-time-machine.s3.amazonaws.com/uknf/2013/05/20/" + x.name))
    }
  }

}
