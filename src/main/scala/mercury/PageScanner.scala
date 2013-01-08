package mercury

import org.jsoup.Jsoup
import java.net.URL
import org.jsoup.nodes.Element
import collection.JavaConverters._
import org.joda.time.DateTime


case class Promotion(
  dt: DateTime,
  targetUrl: String,
  pos: Position
) {
  def pretty = positionInWords + " => " + targetUrl

  def positionInWords = pos.positionInWords

  def isSublink = pos.isSublink

  def src = pos.src
}

object PageScanner {
  case class SimpleLink(href: String, componentName: Option[String] = None, isSublink: Boolean)

  def findPromotions(page: Page): Set[Promotion] = {
    // workaround our astonishingly crap geo location rules
    // need to set GU_EDITION to uk to actually get the UK edition from google's US servers
    // aarrrrrrgh!!!
    val conn = page.url.openConnection()
    conn.setRequestProperty("Cookie", "GU_EDITION=uk")
    conn.setRequestProperty("User-Agent", "mercury; contact graham.tackley@guardian.co.uk")

    val doc = Jsoup.parse(conn.getInputStream, "UTF-8", page.url.toString)

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
      val position = Position(page, simpleComp, topPos, sublinkPos)
      Promotion(dt, href, position)
    }

    proms.toSet
  }
}
