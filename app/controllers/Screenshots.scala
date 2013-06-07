package controllers

import lib.DataStore.Screenshot
import play.api.libs.json.Json
import lib.ScannedLocation

case class Screenshots(loc: ScannedLocation, shots: List[Screenshot]) {
  def byHour: List[(Int, List[Screenshot])] = shots.groupBy(_.hour).toList.sortBy(_._1)

  def asJson = Json.toJson(shots.map(_.asJson))
}
