package controllers

import play.api._
import play.api.mvc._
import lib.ElasticSearch
import org.elasticsearch.search.sort.SortOrder
import collection.JavaConversions._
import org.joda.time.DateTime
import org.jsoup.Jsoup
import org.elasticsearch.search.SearchHit

class Revision(hit: SearchHit) {
  lazy val id = hit.getId
  lazy val dt = new DateTime(hit.getId.toLong).toString("d MMM HH:mm")
  lazy val changes = hit.field("linesChanged").value().toString
}

object Application extends Controller {
  
  def index = Action {
    val revisions = ElasticSearch.client
      .prepareSearch("time-machine")
      .setTypes("nf")
      .addField("linesChanged")
      .addSort("dt", SortOrder.DESC)
      .setSize(150)
      .execute()
      .get()
      .hits()
      .map { new Revision(_) }
      .toList

    Ok(views.html.index(revisions))
  }

  def showVersion(version: Long) = Action {
    val html = ElasticSearch.client
      .prepareGet("time-machine", "nf", version.toString)
      .setFields("html")
      .execute()
      .get()
      .field("html")
      .value()
      .toString

    // and strip out scripts
    val doc = Jsoup.parse(html, "http://www.guardian.co.uk")
    doc.select("script, noscript").remove()

    Ok(doc.outerHtml()).as("text/html")
  }
}