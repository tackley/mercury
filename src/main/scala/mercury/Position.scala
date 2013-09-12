package mercury

// Represnts a position on a promotion page
case class Position(
  src: Page,
  component: String,
  idx: Int,
  sublinkIdx: Option[Int]
) {
  def inWords =
    s"""${src.name} "${component}" position $idx""" +
      sublinkIdx.map(" sublink " + _).getOrElse("")

  def inWordsWithoutPageName =
    s""""${component}" position $idx""" +
      sublinkIdx.map(" sublink " + _).getOrElse("")

  def isSublink = sublinkIdx.isDefined
}

object Position {
  implicit val defaultOrdering: Ordering[Position] = Ordering.by(p => (p.component, p.idx, p.sublinkIdx))
}
