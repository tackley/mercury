package lib

import akka.actor.ActorSystem
import play.api.libs.ws.WS
import org.joda.time.{DateTimeZone, DateTime}

import play.api.Logger
import play.api.http.HeaderNames._
import scalax.file.Path

class Poller(locs: List[ScannedLocation])(implicit actorSys: ActorSystem) {

  private val log = Logger.logger

  def poll() {

    // time zone here is important: we store everything in S3 in
    // local london time
    val now = DateTime.now.withZone(DateTimeZone.forID("Europe/London"))

    log.info(s"Checking if should run (${now.getMinuteOfHour})...")

    // every 5 minutes only
    if (now.getMinuteOfHour % 5 == 0) {
      for (loc <- locs) {
        try {
          doPoll(loc, now)
        } catch {
          case e: Exception =>
            log.warn(s"poll of $loc failed", e)
        }
      }
    }
  }


  private def doPoll(loc: ScannedLocation, dt: DateTime) {
    import play.api.libs.concurrent.Execution.Implicits._

    log.info(s"Polling ${loc.name}...")

    WS.url(loc.url)
      .withHeaders(
        USER_AGENT -> "Network Front Time Machine; contact graham.tackley@guardian.co.uk",
        "X-GU-GeoLocation" -> s"ip:123.123.123.123,country:${loc.countryCode}")
      .get()
      .map { r =>
        log.info(s"Processing ${loc.name}...")

        val storedUrl = DataStore.write(DataStore.mkPath(dt, loc, "raw", "html"), dt, r.body, "text/html")
        val tmpDir = Path.createTempDirectory(deleteOnExit = true)

        val pngFile = PhantomSnapper.snap(storedUrl, tmpDir)

        // and now make a much smaller, lower quality image that loads faster
        val smallerMainImage = ImageMagick.compress(pngFile, tmpDir)
        DataStore.write(DataStore.mkPath(dt, loc, "full", "jpg"), smallerMainImage)

        // and create a thumbnail crop
        val thumb = ImageMagick.thumb(pngFile, tmpDir)
        DataStore.write(DataStore.mkPath(dt, loc, "thumb", "jpg"), thumb)

        log.info(s"...done ${loc.name}")

        tmpDir.deleteRecursively()
      }
      .onFailure {
        case e => log.warn(s"poll failed for ${loc.name}", e)
      }
  }

}
