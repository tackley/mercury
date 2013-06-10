package controllers

import play.api.mvc._
import lib.{ScannedLocation, DataStore}
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormatterBuilder
import java.util.Date


object Application extends Controller {

  def healthcheck = Action {
    Ok("All good")
  }

  def manifest() = Action {
    val data = Map(
      "Build" -> "PREVIEW"
    )

    Ok(data map { case (k, v) => s"$k: $v"} mkString "\n")
  }

  def home = Action {
    Ok(views.html.home())
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

    val x = Screenshots(
      ScannedLocation.ukNetworkFront,
      dt,
      DataStore.findDataPointsForDay(ScannedLocation.ukNetworkFront, dt)
    )

    Ok(views.html.day(dt.toString(dayFormat), x))
  }

  def slide(year: Int, month: Int, day: Int) = Action { r =>
    val dt = new LocalDate(year, month, day)

    val screens = Screenshots(
      ScannedLocation.ukNetworkFront,
      dt,
      DataStore.findDataPointsForDay(ScannedLocation.ukNetworkFront, dt)
    )

    val selectedTime = r.getQueryString("initialTime")

    val initialIdx = selectedTime
      .map(t => screens.shots.indexWhere(_.time == t))
      .filterNot(_ == -1)
      .getOrElse(0)

    Ok(views.html.slide(screens, initialIdx, screens.shots(initialIdx)))
  }

}