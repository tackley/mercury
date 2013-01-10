package mercury

object SublinkParser {

  // you'll probably want to look at the tests to understand wtf this does. sorry.
  def positionLinks(links: Seq[PageScanner.SimpleLink]): Seq[(String, Int, Option[Int])] = {
    links.distinct.foldLeft(List[(String, Int, Option[Int])]()) {
      case (acc, el) if !el.isSublink => acc :+ (el.href, acc.lastOption.map(_._2 + 1).getOrElse(1), None)
      case (acc, el) => acc :+ (el.href, acc.last._2, acc.last._3.map(1 + _).orElse(Some(1)))
    }
  }

}
