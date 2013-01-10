package mercury

import unfiltered.request._
import unfiltered.response._
import org.slf4j.LoggerFactory
import java.net.URL
import com.google.appengine.api.taskqueue.QueueFactory
import com.google.appengine.api.taskqueue.TaskOptions.Builder._

class AdminController extends unfiltered.filter.Plan {
  private val log = LoggerFactory.getLogger(getClass)
  private val queue = QueueFactory.getQueue("scan-queue")

  def intent = {
    case GET(Path("/admin/cron/scan")) =>
      val scanInitiated = for (p <- Page.all) yield {
        queue.add(withUrl("/admin/task/scan").param("url", p.url.toString))
        p.name
      }

      ResponseString("Scan tasks queued: " + scanInitiated.mkString(", "))

    case POST(Path("/admin/task/scan") & Params(p)) =>
      val url = p("url").headOption.getOrElse(sys.error("url expected"))
      val page = Page.fromUrl(url)

      log.info("Scanning {}...", page)

      val promotions = PageScanner.findPromotions(page)

      Store.write(page.url, promotions)

      ResponseString(promotions.toList.map(_.pretty).sorted.mkString("\n"))

  }
}
