package mercury

import java.net.URL


case class Page(name: String, url: URL, country: String = "uk")


object Page {

  val ukNetworkFront = Page("UK Network Front", new URL("http://www.theguardian.com/uk"), "uk")
  val usNetworkFront = Page("US Network Front", new URL("http://www.theguardian.com/us"), "us")
  val ausNetworkFront = Page("Australia Network Front", new URL("http://www.theguardian.com/au"), "au")
  val football = Page("Football Front", new URL("http://www.theguardian.com/football"), "uk")
  val sport = Page("Sport Front", new URL("http://www.theguardian.com/sport"), "uk")
  val cif = Page("UK CIF Front", new URL("http://www.theguardian.com/uk/commentisfree"), "uk")
  val lifeandstyle = Page("Life & Style Front", new URL("http://www.theguardian.com/lifeandstyle"), "uk")

  val all = List(ukNetworkFront, usNetworkFront, ausNetworkFront, football, sport, cif, lifeandstyle)

  def findName(url: String) = fromUrl(url).name

  def fromUrl(url: String) = all.find(_.url.toString == url).getOrElse(Page(url, new URL(url)))
}
