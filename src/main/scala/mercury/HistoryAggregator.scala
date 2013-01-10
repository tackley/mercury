package mercury

import org.joda.time.DateTime

case class HistoryEntry(
  targetUrl: String,
  from: DateTime,
  to: DateTime,
  pos: Position)

object HistoryAggregator {
  implicit val dtOrdering: Ordering[DateTime] = Ordering.by(_.getMillis)

  def aggregate(promos: List[Promotion]): List[HistoryEntry] = {
    assume(promos.map(_.targetUrl).distinct.size == 1)  // all of the promotions are for the same url
    promos.groupBy(_.pos).values.flatMap(collapseSequentialPromos).toList.sortBy(_.from)
  }

  def collapseSequentialPromos(p: List[Promotion]): List[HistoryEntry] = {
    assume(p.map(_.pos).distinct.size == 1) // all have the same position

    p.sortBy(_.dt).foldLeft(List[HistoryEntry]()) { case (acc, promo) =>
      acc match {
        case Nil =>
          List(HistoryEntry(promo.targetUrl, promo.dt, promo.dt, promo.pos))
        case HistoryEntry(target, from, to, pos) :: tail if promo.dt.minusMinutes(8).isBefore(to) =>
          HistoryEntry(target, from, promo.dt, pos) :: tail
        case other =>
          HistoryEntry(promo.targetUrl, promo.dt, promo.dt, promo.pos) :: other
      }
    }
  }


}
