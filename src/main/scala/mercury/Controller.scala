package mercury

import unfiltered.request._
import unfiltered.response._
import org.joda.time.format.ISODateTimeFormat
import unfiltered.response.ResponseString
import org.joda.time.DateTime
import com.google.appengine.api.datastore.{KeyFactory, Key}
import java.net.URL

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
    case GET(_) =>
      ResponseString(html.index.render().body) ~> HtmlContent
  }

  def renderJsonResponse(entries: List[HistoryEntry]): String = {
    import spray.json._
    import spray.json.DefaultJsonProtocol._

    case class HistoryResponse(
      from: Long,
      to: Long,

      formattedFrom: String,
      formattedTo: String,

      srcPageName: String,
      srcPageUrl: String,

      component: String,
      idx: Int,
      sublinkIdx: Option[Int]
    )
    implicit val historyResponseFormat = jsonFormat9(HistoryResponse)

    val responses = for (entry <- entries) yield {
      HistoryResponse(
        from = entry.from.getMillis,
        to = entry.from.getMillis,
        formattedFrom = RelativeDateTimeFormatter.print(entry.from),
        formattedTo = RelativeDateTimeFormatter.print(entry.to),
        srcPageName = entry.pos.src.name,
        srcPageUrl = entry.pos.src.url.toString,
        component = entry.pos.component,
        idx = entry.pos.idx,
        sublinkIdx = entry.pos.sublinkIdx
      )
    }

    responses.toJson.compactPrint
  }


}
