package controllers

import lib.DataStore.Screenshot
import play.api.libs.json.Json
import lib.ScannedLocation
import org.joda.time.LocalDate

case class Screenshots(loc: ScannedLocation, day: LocalDate, shots: List[Screenshot]) {
  def byHour: List[(Int, List[Screenshot])] = shots.groupBy(_.hour).toList.sortBy(_._1)

  // returns linktext -> url
  def hourlyQuickLinks: List[(String, String)] =
    for {
      (hour, shots) <- byHour
      onTheHour <- shots.headOption
    } yield {
      onTheHour.time -> onTheHour.thumbnailsViewUrl
    }

  def asJson = Json.toJson(shots.map(_.asJson))
}
