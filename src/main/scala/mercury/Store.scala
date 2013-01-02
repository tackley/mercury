package mercury

import java.net.URL
import com.google.appengine.api.datastore.{Link => GaeLink, _}
import org.slf4j.LoggerFactory
import org.joda.time.DateTime
import collection.JavaConverters._
import com.google.appengine.api.datastore.Query.{SortDirection, FilterOperator, FilterPredicate}
import java.util.Date

object Store {
  private val log = LoggerFactory.getLogger(getClass)
  private val ds = DatastoreServiceFactory.getDatastoreService

  def write(scannedUrl: URL, promotedLinks: Set[Promotion]) {
    log.info("Writing {} links to store...", promotedLinks.size)

    val scanEntity = new Entity("scan")
    scanEntity.setProperty("dt", promotedLinks.head.dt.toDate)
    scanEntity.setProperty("srcUrl", new GaeLink(promotedLinks.head.srcUrl))
    val scanKey = ds.put(scanEntity)

    val promoEntities = for (link <- promotedLinks) yield {
      val promoEntity = new Entity("promo", scanKey)
      promoEntity.setProperty("dt", link.dt.toDate)
      promoEntity.setProperty("srcUrl", new GaeLink(link.srcUrl))
      promoEntity.setProperty("targetUrl", new GaeLink(link.targetUrl))
      promoEntity.setProperty("position", link.positionInWords)
      promoEntity.setProperty("component", link.component)
      promoEntity.setProperty("topPosition", link.topPosition)
      link.sublinkPosition.foreach(promoEntity.setProperty("sublinkPosition", _))
      promoEntity
    }

    ds.put(promoEntities.asJava)

    log.info("done")
  }

  private def parsePromotionEntity(e: Entity): Promotion = {
    Promotion(
      dt = new DateTime(e.getProperty("dt").asInstanceOf[Date]),
      srcUrl = e.getProperty("srcUrl").asInstanceOf[GaeLink].getValue,
      targetUrl = e.getProperty("targetUrl").asInstanceOf[GaeLink].getValue,
      component = e.getProperty("component").toString,
      topPosition = e.getProperty("topPosition").asInstanceOf[Long].toInt,
      sublinkPosition = Option(e.getProperty("sublinkPosition")).map(_.asInstanceOf[Long].toInt)
    )
  }

  def findScanDates(scannedUrl: String): List[(DateTime, Key)] = {
    val q = new Query("scan")
      .setFilter(new FilterPredicate("srcUrl", FilterOperator.EQUAL, new GaeLink(scannedUrl)))
      .addSort("dt", SortDirection.DESCENDING)

    ds.prepare(q).asIterable(FetchOptions.Builder.withLimit(50)).asScala.map { e =>
      new DateTime(e.getProperty("dt").asInstanceOf[Date]) -> e.getKey
    }.toList
  }


  def findPromotions(key: Key): List[Promotion] = {
    val q = new Query("promo").setAncestor(key)

    ds.prepare(q) .asIterable.asScala.map(parsePromotionEntity).toList
  }

  def findHistory(url: String): List[Promotion] = {
    val q = new Query("promo")
      .setFilter(new FilterPredicate("targetUrl", FilterOperator.EQUAL, new GaeLink(url)))

    ds.prepare(q).asIterable().asScala.map(parsePromotionEntity).toList
  }


}
