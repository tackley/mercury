package controllers

import play.api.mvc._
import lib.{ScannedLocation, DataStore}
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormatterBuilder


object Application extends Controller {

  def index = Action {
    Ok(views.html.years(DataStore.years))
  }

  def months(year: Int) = Action {
    Ok("TODO: Show months for " + year.toString)
  }

  def days(year: Int, month: Int) = Action {
    Ok(s"TODO: Show days for month $month in year $year")
  }

  def today = {
    val t = LocalDate.now()
    day(t.getYear, t.getMonthOfYear, t.getDayOfMonth)
  }

  val dayFormat = new DateTimeFormatterBuilder()
    .appendDayOfWeekText()
    .appendLiteral(' ')
    .appendDayOfMonth(1)
    .appendLiteral(' ')
    .appendMonthOfYearText()
    .appendLiteral(' ')
    .appendYear(4, 4)
    .toFormatter

  def day(year: Int, month: Int, day: Int) = Action {
    val dt = new LocalDate(year, month, day)

    val x = Screenshots(DataStore.findDataPointsForDay(ScannedLocation.ukNetworkFront, dt))
    Ok(views.html.day(dt.toString(dayFormat), x))
  }

  /*
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
  */
}