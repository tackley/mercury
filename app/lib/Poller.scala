package lib

import akka.actor.ActorSystem
import akka.event.Logging
import play.api.libs.ws.WS
import play.api.http.HeaderNames
import org.jsoup.Jsoup
import collection.JavaConversions._
import scalax.io.Resource
import org.jsoup.nodes.Node
import org.joda.time.{DateTimeZone, DateTime}

import javax.swing.SortOrder
import java.util.TimeZone
import play.api.Logger
import play.api.http.HeaderNames._
import javax.imageio.ImageIO
import java.io.File
import java.awt.image.BufferedImage
import java.awt.{RenderingHints, AlphaComposite}
import scalax.file.Path

class Poller(loc: ScannedLocation)(implicit actorSys: ActorSystem) {

  private val log = Logger(getClass)

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
      .withHeaders(USER_AGENT -> "Network Front Time Machine; contact graham.tackley@guardian.co.uk")
      .get()
      .map { r =>
        log.info("Parsing...")

        val doc = Jsoup.parse(r.body, loc.url)


        val storedUrl = DataStore.write(DataStore.mkPath(dt, loc, "raw", "html"), dt, doc.toString)

        // removing all script is probably too much - perhaps we should
        // just remove the ad code?
        doc.select("script, noscript").remove()
        val noScriptUrl = DataStore.write(DataStore.mkPath(dt, loc, "noscript", "html"), dt, doc.toString)

        val tmpDir = Path.createTempDirectory(deleteOnExit = true)


        val pngFile = PhantomSnapper.snap(storedUrl, tmpDir)
        DataStore.write(DataStore.mkPath(dt, loc, "full", "png"), pngFile)

        // and now crop!
        val (crop, thumb) = Cropper.cropAndThumb(pngFile, tmpDir)

        DataStore.write(DataStore.mkPath(dt, loc, "crop", "png"), crop)
        DataStore.write(DataStore.mkPath(dt, loc, "thumb", "png"), thumb)

        tmpDir.deleteRecursively()
      }
      .onFailure {
        case e => log.warn("poll failed", e)
      }
  }

}
