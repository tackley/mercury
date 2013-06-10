package lib

import org.joda.time.{PeriodType, Period, LocalDate, DateTime}
import controllers.routes

object DaysHelper {
  def lastWeek = for (daysAgo <- 2 to 7) yield {
    val day = DateTime.now.minusDays(daysAgo)
    day.dayOfWeek().getAsText -> routes.Application.day(day.getYear, day.getMonthOfYear, day.getDayOfMonth)
  }

  def nameForDay(d: LocalDate) = {
    val daysAgo = new Period(d, LocalDate.now, PeriodType.days).getDays

    daysAgo match {
      case 0 => "Today"
      case 1 => "Yesterday"
      case numDays if numDays <= 7 => d.dayOfWeek().getAsText
      case _ => d.toString
    }
  }
}
