package mercury

import java.net.URL
import com.google.appengine.api.datastore.{Link => GaeLink, _}
import org.slf4j.LoggerFactory
import org.joda.time.DateTime
import collection.JavaConverters._
import java.util.Date
import com.google.appengine.api.datastore
import datastore.Query.{CompositeFilterOperator, SortDirection, FilterOperator, FilterPredicate}

object Store {

  private val log = LoggerFactory.getLogger(getClass)
  private val ds = DatastoreServiceFactory.getDatastoreService

  implicit class UrlToGaeLink(url: URL) {
    def asLink: GaeLink = new GaeLink(url.toString)
  }

  implicit class StringToGaeLink(url: String) {
    def asLink: GaeLink = new GaeLink(url)
  }


  def write(scannedUrl: URL, promotedLinks: Set[Promotion]) {
    log.info("Writing {} links to store...", promotedLinks.size)

    val dt = promotedLinks.head.dt
    val lastScanWithin = dt.minusMinutes(8)

    val historyEntities = for (link <- promotedLinks) yield {
      val entity = findHistoryEntity(link, lastScanWithin) getOrElse {
        log.info("Creating new history entry for " + link.pretty)
        val e = new Entity("history")
        writePosition(link.pos, e)
        e.setProperty("from", dt.toDate)
        e.setProperty("targetUrl", link.targetUrl.asLink)
        e
      }

      entity.setProperty("to", dt.toDate)
      entity
    }

    log.info("writing updates")
    ds.put(historyEntities.asJava)
    log.info("done")
  }

  private def findHistoryEntity(promotion: Promotion, since: DateTime): Option[Entity] = {
    val filters = List(
      FilterOperator.EQUAL.of("srcUrl", promotion.pos.src.url.asLink),
      FilterOperator.EQUAL.of("component", promotion.pos.component),
      FilterOperator.EQUAL.of("topPosition", promotion.pos.idx.toLong),
      FilterOperator.EQUAL.of("targetUrl", promotion.targetUrl.asLink),
      FilterOperator.GREATER_THAN_OR_EQUAL.of("to", since.toDate)
    ) ++ promotion.pos.sublinkIdx.map { idx =>
      FilterOperator.EQUAL.of("sublinkPosition", idx.toLong)
    }

    val q = new Query("history")
      .setFilter(CompositeFilterOperator.and(filters: _*))

    Option(ds.prepare(q).asSingleEntity())
  }


  private def parsePositionFromEntity(e: Entity): Position = {
    Position(
      src = Page.fromUrl(e.getProperty("srcUrl").asInstanceOf[GaeLink].getValue),
      component = e.getProperty("component").toString,
      idx = e.getProperty("topPosition").asInstanceOf[Long].toInt,
      sublinkIdx = Option(e.getProperty("sublinkPosition")).map(_.asInstanceOf[Long].toInt)
    )
  }

  private def writePosition(p: Position, e: Entity) {
    e.setProperty("srcUrl", p.src.url.asLink)
    e.setProperty("component", p.component)
    e.setProperty("topPosition", p.idx)
    p.sublinkIdx.foreach(e.setProperty("sublinkPosition", _))
  }

  private def readHistoryEntry(e: Entity): HistoryEntry = {
    HistoryEntry(
      from = new DateTime(e.getProperty("from").asInstanceOf[Date]),
      to = new DateTime(e.getProperty("to").asInstanceOf[Date]),
      targetUrl = e.getProperty("targetUrl").asInstanceOf[GaeLink].getValue,
      pos = parsePositionFromEntity(e)
    )
  }


  def findHistory(url: String): List[HistoryEntry] = {
    val q = new Query("history")
      .setFilter(FilterOperator.EQUAL.of("targetUrl", url.asLink))

    ds.prepare(q).asIterable.asScala.map(readHistoryEntry).toList
  }


}
