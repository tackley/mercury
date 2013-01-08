package mercury

// Represnts a position on a promotion page
case class Position(
  src: Page,
  component: String,
  idx: Int,
  sublinkIdx: Option[Int]
) {
  def positionInWords = "\"" + component + "\" position " + idx +
    sublinkIdx.map(" sublink " + _).getOrElse("")

  def isSublink = sublinkIdx.isDefined
}

object Position {
  implicit val defaultOrdering: Ordering[Position] = Ordering.by(p => (p.idx, p.sublinkIdx))
}
