package mercury

import java.net.URL

object Pages {
  case class Page(name: String, url: URL)

  val ukNetworkFront = Page("UK Network Front", new URL("http://www.guardian.co.uk/"))

  val all = ukNetworkFront :: Nil

  def findName(url: String) = all.find(_.url.toString == url).map(_.name).getOrElse(url)
}
