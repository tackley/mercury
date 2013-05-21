package lib


case class ScannedLocation(
  name: String,
  bucketPrefix: String,
  url: String,
  countryCode: String)


object ScannedLocation {
  val ukNetworkFront = ScannedLocation("UK Network Front", "uknf", "http://www.guardian.co.uk", "uk")

  val all = List(ukNetworkFront)
}
