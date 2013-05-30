package controllers

import lib.DataStore.Screenshot
import play.api.libs.json.Json

case class Screenshots(shots: List[Screenshot]) {
  def byHour: List[(Int, List[Screenshot])] = shots.groupBy(_.hour).toList.sortBy(_._1)

  def asJson = Json.toJson(shots.map(_.asJson))
}
