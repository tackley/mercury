package mercury

import org.joda.time.DateTime

case class HistoryEntry(
  from: DateTime,
  to: DateTime,
  position: String)

object HistoryAggregator {

}
