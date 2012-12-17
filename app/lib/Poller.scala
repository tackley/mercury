package lib

import akka.actor.ActorSystem
import akka.event.Logging
import play.api.libs.ws.WS
import play.api.http.HeaderNames
import org.jsoup.Jsoup
import collection.JavaConversions._
import scalax.io.Resource
import org.jsoup.nodes.Node
import org.apache.commons.lang.StringUtils
import org.joda.time.DateTime

import org.elasticsearch.search.sort._
import difflib.DiffUtils

class Poller(url: String)(implicit actorSys: ActorSystem) {
  private val log = Logging(actorSys, this.getClass)
  var lastVersion = latestHtml

  private def latestHtml = {
    try {
      ElasticSearch.client
        .prepareSearch("time-machine")
        .addSort("dt", SortOrder.DESC)
        .addField("diffHtml")
        .setTypes("nf")
        .execute()
        .get()
        .hits()
        .headOption
        .map(h => stringToLines(h.field("diffHtml").value[String]()))
        .getOrElse(Nil)
    } catch {
      case e: Exception =>
        log.error(e, "failed to get old data from elasticsearch")
        Nil
    }
  }

  log.info("last version = " + lastVersion)

  private def removeComments(node: Node) {
    for (child <- node.childNodes().toList) {
      if (child.nodeName() == "#comment")
        child.remove()
      else
        removeComments(child)
    }
  }

  private def stringToLines(s: String): List[String] = s.split("\n").toList.map(_.trim)

  def poll() {
    import HeaderNames._

    log.info("Polling...")

    WS.url(url)
      .withHeaders(USER_AGENT -> "Network Front Time Machine; contact graham.tackley@guardian.co.uk")
      .get()
      .map { r =>
        log.info("Parsing...")
        val doc = Jsoup.parse(r.body, url)

        // create a copy of the doc that we'll do a diff on
        val diffDoc = doc.clone()

        // eliminate automated stuff
        diffDoc.select(".most-viewed, .m-zeitgeist").remove()

        // elimitae "last updated 6 mins ago"
        diffDoc.select(".accolade, .last-updated").remove()

        // scripts contain random stuff like the server that generated the request
        diffDoc.select("script").remove()

        // soulmates update regularly
        diffDoc.select(".soulmate").remove()

        // not interested in ticker changes
        diffDoc.select("#ticker").remove()

        // nor the auto rotating ventures slot machine
        diffDoc.select(".ventures-slot-machine").remove()

        // the omniture url in the noscript tag changes
        diffDoc.select("noscript").remove()

        // the id of the video tag changes
        diffDoc.select(".video-player").removeAttr("id")

        // not really interested when the weather forecast changes
        diffDoc.select("#weather-header").remove()

        // not interested in comment changes
        removeComments(diffDoc)

        val docString = stringToLines(diffDoc.outerHtml())

        println("calculating diff...")

        val diffs = DiffUtils.diff(lastVersion, docString).getDeltas
        println("delta size is " + diffs.size)

        for (diff <- diffs) {
          println("original:")
          println(diff.getOriginal)
          println("revised:")
          println(diff.getRevised)
        }

        if (diffs.size > 0) {

          lastVersion = docString

          val dt = DateTime.now()
          val id = dt.getMillis.toString

          ElasticSearch.client.prepareIndex("time-machine", "nf", id)
            .setSource(Map[String, AnyRef](
              "html" -> doc.outerHtml(),
              "diffHtml" -> docString.mkString("\n"),
              "linesChanged" -> (diffs.size: java.lang.Integer),
              "dt" -> dt
            ))
            .execute()
            .get()

          log.info("Written")

        } else {
          log.info("Same!")
        }

      }

  }

}
