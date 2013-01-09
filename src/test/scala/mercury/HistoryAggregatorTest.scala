package mercury

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec
import org.joda.time.DateTime

class HistoryAggregatorTest extends FlatSpec with ShouldMatchers {

  behavior of "HistoryAggregator"

  val url = "http://www.example.com"

  it should "flatten multiple entries in the same position" in {
    val startDate = new DateTime(2012, 7, 20, 11, 0)
    val pos = Position(Page.ukNetworkFront, "comp", 1, None)

    val promos = List(
      Promotion(startDate, url, pos),
      Promotion(startDate.plusMinutes(5), url, pos),
      Promotion(startDate.plusMinutes(10), url, pos),
      Promotion(startDate.plusMinutes(15), url, pos)
    )

    val history = HistoryAggregator.aggregate(promos)
    history should be (List(HistoryEntry(startDate, startDate.plusMinutes(15), pos)))
  }

  it should "keep different entries separate" in {
    val startDate = new DateTime(2012, 7, 20, 11, 0)
    val pos1 = Position(Page.ukNetworkFront, "comp", 1, None)
    val pos2 = Position(Page.ukNetworkFront, "comp", 2, None)

    val promos = List(
      Promotion(startDate, url, pos2),
      Promotion(startDate.plusMinutes(5), url, pos2),
      Promotion(startDate.plusMinutes(10), url, pos1),
      Promotion(startDate.plusMinutes(15), url, pos1)
    )

    HistoryAggregator.aggregate(promos) should be (List(
      HistoryEntry(startDate, startDate.plusMinutes(5), pos2),
      HistoryEntry(startDate.plusMinutes(10), startDate.plusMinutes(15), pos1)
    ))

  }

}
