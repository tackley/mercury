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

class Poller(loc: ScannedLocation)(implicit actorSys: ActorSystem) {

  private val log = Logger(getClass)

  def poll() {

    val now = DateTime.now(DateTimeZone.UTC)

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

        DataStore.write(DataStore.mkPath(dt, loc.bucketPrefix, "raw", "html"), doc.toString)
        doc.select("script, noscript").remove()

        DataStore.write(DataStore.mkPath(dt, loc.bucketPrefix, "noscript", "html"), doc.toString)
      }
      .onFailure {
        case e => log.warn("poll failed", e)
      }
  }

}
