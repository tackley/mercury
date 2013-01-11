package mercury

import java.net.URL


case class Page(name: String, url: URL)


object Page {

  val ukNetworkFront = Page("UK Network Front", new URL("http://www.guardian.co.uk/"))
  val football = Page("Football Front", new URL("http://www.guardian.co.uk/football"))
  val sport = Page("Sport Front", new URL("http://www.guardian.co.uk/sport"))
  val cif = Page("CIF Front", new URL("http://www.guardian.co.uk/commentisfree"))
  val lifeandstyle = Page("Life & Style Front", new URL("http://www.guardian.co.uk/lifeandstyle"))

  val all = List(ukNetworkFront, football, sport, cif, lifeandstyle)

  def findName(url: String) = fromUrl(url).name

  def fromUrl(url: String) = all.find(_.url.toString == url).getOrElse(Page(url, new URL(url)))
}
