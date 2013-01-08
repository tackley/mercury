package mercury

import java.net.URL


case class Page(name: String, url: URL)


object Page {

  val ukNetworkFront = Page("UK Network Front", new URL("http://www.guardian.co.uk/"))

  val all = ukNetworkFront :: Nil

  def findName(url: String) = fromUrl(url).name

  def fromUrl(url: String) = all.find(_.url.toString == url).getOrElse(Page(url, new URL(url)))
}
