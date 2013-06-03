package lib

import org.joda.time.{Duration, DateTimeZone, LocalDate, DateTime}
import play.api.Logger
import com.amazonaws.regions.{Region, Regions}
import com.amazonaws.services.s3.AmazonS3Client
import org.joda.time.format.DateTimeFormatterBuilder
import com.amazonaws.util.StringInputStream
import com.amazonaws.services.s3.model.{CannedAccessControlList, PutObjectRequest, ObjectMetadata}
import java.io.File
import scala.collection.JavaConverters._
import java.net.URL
import controllers.routes
import play.api.libs.json.Json

object DataStore {

  case class Path(bucket: String, key: String, ext: String) {
    def url = s"http://$bucket.s3.amazonaws.com/$key"

    // oh dear. this doesn't belong here ;(
    def contentType = ext match {
      case "html" => "text/html"
      case "png" => "image/png"
    }
  }

  case class Screenshot(dt: DateTime, basePath: URL, commonFilename: String) {
    def hour = dt.getHourOfDay
    def time = dt.toString("HH:mm")
    def slideUrl = routes.Application.slide(dt.getYear, dt.getMonthOfYear, dt.getDayOfMonth) +
      s"?initialTime=$time"

    def thumbnail = new URL(basePath, "thumb_" + commonFilename + ".png")
    def full = new URL(basePath, "full_" + commonFilename + ".png")
    def noscriptHtml = new URL(basePath, "noscript_" + commonFilename + ".html")

    def asJson = Json.obj(
      "dt" -> dt,
      "time" -> time,
      "img" -> full.toString
    )
  }


  private val log = Logger(getClass)

  private val s3 = new AmazonS3Client(Config.awsProvider)
  s3.setRegion(Region.getRegion(Regions.EU_WEST_1))

  private val bucket = "ophan-time-machine"

  lazy val pathDateFormat = new DateTimeFormatterBuilder()
    .appendYear(4, 4)
    .appendLiteral('/')
    .appendMonthOfYear(2)
    .appendLiteral('/')
    .appendDayOfMonth(2)
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

  def mkPath(dt: DateTime, location: ScannedLocation, qualifier: String, extension: String): Path =
    Path(bucket, s"${location.bucketPrefix}/${dt.toString(pathDateFormat)}/${qualifier}_${dt.toString(fileDateFormat)}.$extension", extension)

  private def mkBasePath(ld: LocalDate, location: ScannedLocation): Path =
    Path(bucket, s"${location.bucketPrefix}/${ld.toString(pathDateFormat)}/", "")

  def write(p: Path, dt: DateTime, data: String): String = {
    log.info(s"write to $p...")

    val s = new StringInputStream(data)

    val md = new ObjectMetadata()
    md.setContentLength(s.available())
    md.setContentType(p.contentType)
    md.setLastModified(dt.toDate)
    md.setCacheControl(s"max-age: ${Duration.standardDays(30).getStandardSeconds}")

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

  def findDataPointsForDay(loc: ScannedLocation, day: LocalDate): List[Screenshot] = {
    val searchPath = mkBasePath(day, loc)
    val result = s3.listObjects(searchPath.bucket, searchPath.key + "thumb_")

    val screenshots = for (r <- result.getObjectSummaries.asScala) yield {
      val key = r.getKey
      val rawDate = key.split("_").last.stripSuffix(".png")
      val dt = fileDateFormat.parseDateTime(rawDate)
      Screenshot(dt,
        new URL(s"http://$bucket.s3.amazonaws.com/${searchPath.key}"),
        rawDate)
    }

    screenshots.toList
  }

}
