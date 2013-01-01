import java.io.File
import java.net.URL
import org.jsoup.Jsoup
import collection.JavaConverters._
import org.jsoup.nodes.Element

//case class Link(href: String, componentName: Option[String] = None, isSublink: Boolean)

object Main extends App {

  /*
  val doc = Jsoup.parse(new URL("http://www.guardian.co.uk"), 9999)

  val elems = doc.select("a[href^=http:]").asScala
  println(elems.size + " links")

  def findDataComponent(e: Element): Option[String] =
    e.parents().asScala
      .find(_.hasAttr("data-component"))
      .map(_.attr("data-component"))


  val links = for (link <- elems) yield {
    val href = link.attr("abs:href").takeWhile('?' !=).takeWhile('#' !=)
    val comp = findDataComponent(link)
    val isSublink = link.parents().asScala.exists(_.hasClass("sublinks"))

    Link(href, comp, isSublink)
  }

  //links.map { l => l.href + "" + l.componentName.getOrElse("?") }.foreach(println)
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

  val grouped: Map[Option[String], Seq[Link]] = links.groupBy { _.componentName }

  def positionLinks(links: Seq[Link]): Seq[(String, Int, Option[Int])] = {
    links.distinct.foldLeft(List[(String, Int, Option[Int])]()) {
      case (acc, el) if !el.isSublink => acc :+ (el.href, acc.lastOption.map(_._2 + 1).getOrElse(1), None)
      case (acc, el) if el.isSublink => acc :+ (el.href, acc.last._2, acc.last._3.map(1 +).orElse(Some(1)))
    }
  }

  val proms = for {
    (optionalComponent, links) <- grouped
    (href, topPos, sublinkPos) <- positionLinks(links)
    componentName <- optionalComponent
  } yield {
    val simpleComp = componentName.split(":").map(_.trim).filterNot(_.isEmpty).last
    Promotion(href, simpleComp, topPos, sublinkPos)
  }

  proms.map(_.pretty).toList.sorted.foreach(println)
   */
//  for ((comp,l) <- grouped) {
//    val simpleComp = comp flatMap { c =>
//      c.split(":").map(_.trim).filterNot(_.isEmpty).lastOption
//    }
//    println(comp + " ==> " + simpleComp)
//
//    for (link <- l.distinct) println("\t" + link.isSublink + " " + link.href)
//  }



}
