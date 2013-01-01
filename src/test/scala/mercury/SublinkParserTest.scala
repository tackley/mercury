package mercury

import PageScanner.SimpleLink
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class SublinkParserTest extends FlatSpec with ShouldMatchers {
  behavior of "Sublink parsing"

  it should "work ok with no sublinks" in {
    val links = List(
      SimpleLink("one", isSublink = false),
      SimpleLink("two", isSublink = false),
      SimpleLink("three", isSublink = false),
      SimpleLink("four", isSublink = false)
    )

    SublinkParser.positionLinks(links) should be(List(
      ("one", 1, None),
      ("two", 2, None),
      ("three", 3, None),
      ("four", 4, None)
    ))
  }

  it should "deal with simple sublinks" in {
    val links = List(
      SimpleLink("one", isSublink = false),
      SimpleLink("one-sub", isSublink = true),
      SimpleLink("two", isSublink = false)
    )

    SublinkParser.positionLinks(links) should be(List(
      ("one", 1, None),
      ("one-sub", 1, Some(1)),
      ("two", 2, None)
    ))
  }

  it should "deal with something a bit like our NF" in {
    val links = List(
      SimpleLink("one", isSublink = false),
      SimpleLink("one-sub-1", isSublink = true),
      SimpleLink("one-sub-2", isSublink = true),
      SimpleLink("one-sub-3", isSublink = true),
      SimpleLink("two", isSublink = false),
      SimpleLink("two-sub-1", isSublink = true),
      SimpleLink("two-sub-2", isSublink = true)
    )

    SublinkParser.positionLinks(links) should be(List(
      ("one", 1, None),
      ("one-sub-1", 1, Some(1)),
      ("one-sub-2", 1, Some(2)),
      ("one-sub-3", 1, Some(3)),
      ("two", 2, None),
      ("two-sub-1", 2, Some(1)),
      ("two-sub-2", 2, Some(2))
    ))
  }
}
