package mercury

import java.net.URL

object Pages {
  case class Page(name: String, url: URL)

  val ukNetworkFront = Page("UK Network Front", new URL("http://www.guardian.co.uk/"))

  val all = ukNetworkFront :: Nil
}
