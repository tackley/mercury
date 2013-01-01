package mercury

import org.jsoup.Jsoup
import java.net.URL
import org.jsoup.nodes.Element
import collection.JavaConverters._


case class Promotion(
  url: String,
  component: String,
  topPosition: Int,
  sublinkPosition: Option[Int]
) {
  def pretty = component + " position " + topPosition +
    sublinkPosition.map(" sublink " + _).getOrElse("") +
    " => " + url
}

object PageScanner {
  case class SimpleLink(href: String, componentName: Option[String] = None, isSublink: Boolean)

  def findPromotions(url: URL): Set[Promotion] = {
    val doc = Jsoup.parse(url, 20000)

    val elems = doc.select("a[href^=http:]").asScala

    def findDataComponent(e: Element): Option[String] =
      e.parents().asScala
        .find(_.hasAttr("data-component"))
        .map(_.attr("data-component"))

    val links = for (link <- elems) yield {
      val href = link.attr("abs:href").takeWhile('?' !=).takeWhile('#' !=)
      val comp = findDataComponent(link)
      val isSublink = link.parents().asScala.exists(_.hasClass("sublinks"))

      SimpleLink(href, comp, isSublink)
    }


    val grouped: Map[Option[String], Seq[SimpleLink]] = links.groupBy { _.componentName }


    val proms = for {
      (optionalComponent, links) <- grouped
      (href, topPos, sublinkPos) <- SublinkParser.positionLinks(links)
      componentName <- optionalComponent
    } yield {
      val simpleComp = componentName.split(":").map(_.trim).filterNot(_.isEmpty).last
      Promotion(href, simpleComp, topPos, sublinkPos)
    }

    proms.toSet
  }
}
