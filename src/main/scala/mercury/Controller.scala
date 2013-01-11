package mercury

import unfiltered.request._
import unfiltered.response._
import org.joda.time.format.ISODateTimeFormat
import unfiltered.response.ResponseString
import org.joda.time.DateTime
import com.google.appengine.api.datastore.{KeyFactory, Key}

class Controller extends unfiltered.filter.Plan {
  val dateFormat = ISODateTimeFormat.dateTimeNoMillis()

  case class AvailableScan(url: String, dt: DateTime, key: Key) {
    lazy val snapshotUrl = "/snapshot?url=" + url + "&key=" + KeyFactory.keyToString(key)
    lazy val niceDate = dt.toString("EEE d MMM h:mm aa")
  }

  case class Component(name: String, promos: List[Promotion]) {
    def topLevelLinks = promos.filterNot(_.isSublink)
    def sublinksForPosition(pos: Position) = promos.filter(_.pos.idx == pos.idx).filter(_.isSublink)
  }

  def intent = {
    case GET(Path("/")) =>
      ResponseString(html.index.render().body) ~> HtmlContent

    case GET(Path("/history") & Params(p)) =>
      val url = p("url").headOption getOrElse sys.error("missing url")

      val history: List[HistoryEntry] = Store.findHistory(url)

      ResponseString(html.history.render(url, history).body) ~> HtmlContent
  }

}
