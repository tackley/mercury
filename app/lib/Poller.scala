package lib

import akka.actor.ActorSystem
import play.api.libs.ws.WS
import org.joda.time.DateTime

import play.api.Logger
import play.api.http.HeaderNames._
import scalax.file.Path

class Poller(loc: ScannedLocation)(implicit actorSys: ActorSystem) {

  private val log = Logger.logger

  def poll() {

    val now = DateTime.now

    log.info(s"Checking if should run (${now.getMinuteOfHour})...")

    // every 5 minutes only
    if (now.getMinuteOfHour % 5 == 0) {
      try {
        doPoll(now)
      } catch {
        case e: Exception =>
          log.warn("poll failed", e)
      }
    }
  }


  private def doPoll(dt: DateTime) {
    import play.api.libs.concurrent.Execution.Implicits._

    log.info("Polling...")

    WS.url(loc.url)
      .withHeaders(
        USER_AGENT -> "Network Front Time Machine; contact graham.tackley@guardian.co.uk",
        "X-GU-GeoLocation" -> "ip:123.123.123.123;country:GB")
      .get()
      .map { r =>
        log.info("Processing...")

        val storedUrl = DataStore.write(DataStore.mkPath(dt, loc, "raw", "html"), dt, r.body, "text/html")
        val tmpDir = Path.createTempDirectory(deleteOnExit = true)

        val pngFile = PhantomSnapper.snap(storedUrl, tmpDir)

        // and now make a much smaller, lower quality image that loads faster
        val smallerMainImage = ImageMagick.compress(pngFile, tmpDir)
        DataStore.write(DataStore.mkPath(dt, loc, "full", "jpg"), smallerMainImage)

        // and create a thumbnail crop
        val thumb = ImageMagick.thumb(pngFile, tmpDir)
        DataStore.write(DataStore.mkPath(dt, loc, "thumb", "jpg"), thumb)

        tmpDir.deleteRecursively()
      }
      .onFailure {
        case e => log.warn("poll failed", e)
      }
  }

}
