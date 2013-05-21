package controllers

import org.joda.time.DateTime
import java.net.URL

case class Screenshot(dt: DateTime, thumbnail: URL) {
  def hour = dt.getHourOfDay
  def time = dt.toString("HH:mm")
}

case class Screenshots(shots: List[Screenshot]) {
  def byHour: List[(Int, List[Screenshot])] = shots.groupBy(_.hour).toList.sortBy(_._1)
}
