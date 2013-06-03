package lib

import org.joda.time.DateTime
import controllers.routes

object DaysHelper {
  def lastWeek = for (daysAgo <- 2 to 7) yield {
    val day = DateTime.now.minusDays(daysAgo)
    day.dayOfWeek().getAsText -> routes.Application.day(day.getYear, day.getMonthOfYear, day.getDayOfMonth)
  }
}
