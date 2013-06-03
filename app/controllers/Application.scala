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

  def yesterday = {
    val t = LocalDate.now().minusDays(1)
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

  def slide(year: Int, month: Int, day: Int) = Action { r =>
    val dt = new LocalDate(year, month, day)

    val screens = Screenshots(DataStore.findDataPointsForDay(ScannedLocation.ukNetworkFront, dt))

    val selectedTime = r.getQueryString("initialTime")

    val initialIdx = selectedTime
      .map(t => screens.shots.indexWhere(_.time == t))
      .filterNot(_ == -1)
      .getOrElse(0)

    Ok(views.html.slide(screens, initialIdx, screens.shots(initialIdx)))
  }

  def slideDemo = Action {
    Ok(views.html.slideDemo())
  }
}