package mercury

import org.joda.time.{Duration, DateTime}

object RelativeDateTimeFormatter {

  def print(dt: DateTime, relativeTo: DateTime = DateTime.now): String = {
    val d = new Duration(dt, relativeTo)
    if (dt.isAfter(relativeTo.minus(Config.readingIsLatestIfWithin)))
      "now"
    else if (d.getStandardDays > 0)
      dt.toString("d MMM HH:mm")
    else
      dt.toString("HH:mm")
  }


}
