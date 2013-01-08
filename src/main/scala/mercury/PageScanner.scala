package mercury

import org.jsoup.Jsoup
import java.net.URL
import org.jsoup.nodes.Element
import collection.JavaConverters._
import org.joda.time.DateTime


case class Promotion(
  dt: DateTime,
  srcUrl: String,
  targetUrl: String,
  component: String,
  topPosition: Int,
  sublinkPosition: Option[Int]
) {
  def pretty = positionInWords + " => " + targetUrl

  def positionInWords = "\"" + component + "\" position " + topPosition +
    sublinkPosition.map(" sublink " + _).getOrElse("")

  def isSublink = sublinkPosition.isDefined

  def src = Pages.findName(srcUrl)
}

object PageScanner {
  case class SimpleLink(href: String, componentName: Option[String] = None, isSublink: Boolean)

  def findPromotions(url: URL): Set[Promotion] = {
    // workaround our astonishingly crap geo location rules
    // need to set GU_EDITION to uk to actually get the UK edition from google's US servers
    // aarrrrrrgh!!!
    val conn = url.openConnection()
    conn.setRequestProperty("Cookie", "GU_EDITION=uk")
    conn.setRequestProperty("User-Agent", "mercury; contact graham.tackley@guardian.co.uk")

    val doc = Jsoup.parse(conn.getInputStream, "UTF-8", url.toString)

    val elems = doc.select("a[href^=http:]").asScala

    def findDataComponent(e: Element): Option[String] =
      e.parents().asScala
        .find(_.hasAttr("data-component"))
        .map(_.attr("data-component"))

    val links = for (link <- elems) yield {
      val href = link.attr("abs:href").takeWhile('?' != _).takeWhile('#' != _)
      val comp = findDataComponent(link)
      val isSublink = link.parents().asScala.exists(_.hasClass("sublinks"))

      SimpleLink(href, comp, isSublink)
    }


    val grouped: Map[Option[String], Seq[SimpleLink]] = links.groupBy { _.componentName }

    val dt = DateTime.now

    val proms = for {
      (optionalComponent, links) <- grouped
      (href, topPos, sublinkPos) <- SublinkParser.positionLinks(links)
      componentName <- optionalComponent
    } yield {
      val simpleComp = componentName.split(":").map(_.trim).filterNot(_.isEmpty).last
      Promotion(dt, url.toString, href, simpleComp, topPos, sublinkPos)
    }

    proms.toSet
  }
}
