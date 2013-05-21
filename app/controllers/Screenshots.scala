package controllers

import lib.DataStore.Screenshot

case class Screenshots(shots: List[Screenshot]) {
  def byHour: List[(Int, List[Screenshot])] = shots.groupBy(_.hour).toList.sortBy(_._1)
}
