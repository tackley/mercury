import org.specs2.mutable.Specification

class SublinkParserTest extends Specification {
  "Sublink parsing" should {
    "work ok with no sublinks" in {
      val links = List(
        Link("one", isSublink = false),
        Link("two", isSublink = false),
        Link("three", isSublink = false),
        Link("four", isSublink = false)
      )

      Main.positionLinks(links) must_== List(
        ("one", 1, None),
        ("two", 2, None),
        ("three", 3, None),
        ("four", 4, None)
      )
    }

    "deal with simple sublinks" in {
      val links = List(
        Link("one", isSublink = false),
        Link("one-sub", isSublink = true),
        Link("two", isSublink = false)
      )

      Main.positionLinks(links) must_== List(
        ("one", 1, None),
        ("one-sub", 1, Some(1)),
        ("two", 2, None)
      )
    }

    "deal with something a bit like our NF" in {
      val links = List(
        Link("one", isSublink = false),
        Link("one-sub-1", isSublink = true),
        Link("one-sub-2", isSublink = true),
        Link("one-sub-3", isSublink = true),
        Link("two", isSublink = false),
        Link("two-sub-1", isSublink = true),
        Link("two-sub-2", isSublink = true)
      )

      Main.positionLinks(links) must_== List(
        ("one", 1, None),
        ("one-sub-1", 1, Some(1)),
        ("one-sub-2", 1, Some(2)),
        ("one-sub-3", 1, Some(3)),
        ("two", 2, None),
        ("two-sub-1", 2, Some(1)),
        ("two-sub-2", 2, Some(2))
      )
    }

  }
}
