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

  implicit class UrlToGaeLink(url: URL) {
    def asLink: GaeLink = new GaeLink(url.toString)
  }

  def write(scannedUrl: URL, promotedLinks: Set[Promotion]) {
    log.info("Writing {} links to store...", promotedLinks.size)

    val scanEntity = new Entity("scan")
    scanEntity.setProperty("dt", promotedLinks.head.dt.toDate)
    scanEntity.setProperty("srcUrl", scannedUrl.asLink)
    val scanKey = ds.put(scanEntity)

    val promoEntities = for (link <- promotedLinks) yield {
      val promoEntity = new Entity("promo", scanKey)
      promoEntity.setProperty("dt", link.dt.toDate)
      promoEntity.setProperty("srcUrl", link.pos.src.url.asLink)
      promoEntity.setProperty("targetUrl", new GaeLink(link.targetUrl))
      promoEntity.setProperty("component", link.pos.component)
      promoEntity.setProperty("topPosition", link.pos.idx)
      link.pos.sublinkIdx.foreach(promoEntity.setProperty("sublinkPosition", _))
      promoEntity
    }

    ds.put(promoEntities.asJava)

    log.info("done")
  }

  private def parsePromotionEntity(e: Entity): Promotion = {
    val pos = Position(
      src = Page.fromUrl(e.getProperty("srcUrl").asInstanceOf[GaeLink].getValue),
      component = e.getProperty("component").toString,
      idx = e.getProperty("topPosition").asInstanceOf[Long].toInt,
      sublinkIdx = Option(e.getProperty("sublinkPosition")).map(_.asInstanceOf[Long].toInt)
    )

    Promotion(
      dt = new DateTime(e.getProperty("dt").asInstanceOf[Date]),
      targetUrl = e.getProperty("targetUrl").asInstanceOf[GaeLink].getValue,
      pos = pos
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
