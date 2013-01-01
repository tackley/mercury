package mercury

import unfiltered.request._
import unfiltered.response._
import org.slf4j.LoggerFactory
import java.net.URL

class Admin extends unfiltered.filter.Plan {
  private val log = LoggerFactory.getLogger(getClass)

  import QParams._

  def intent = {
    case GET(Path("/admin/cron/scan") & Params(p)) =>

      val expected = for {
        optUrl <- lookup("url") is required("url parameter expected")
      } yield {
        val url = new URL(optUrl.get)

        log.info("Scanning {}...", url)

        val promotions = PageScanner.findPromotions(url)

        ResponseString(promotions.toList.map(_.pretty).sorted.mkString("\n"))
      }

      expected(p) orFail { fails =>
        BadRequest ~> ResponseString(fails.map(_.error).mkString("Bad Request", "\n", "\n"))
      }

  }
}
