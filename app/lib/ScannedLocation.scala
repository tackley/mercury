package lib

import play.api.mvc.PathBindable


case class ScannedLocation(
  name: String,
  bucketPrefix: String,
  url: String,
  countryCode: String)


object ScannedLocation {
  val ukNetworkFront = ScannedLocation("UK Network Front", "uknf", "http://www.guardian.co.uk", "GB")
  val usNetworkFront = ScannedLocation("US Network Front", "usnf", "http://www.guardiannews.com", "US")
  val ausNetworkFront = ScannedLocation("Australia Network Front", "ausnf", "http://www.guardian.co.uk/australia", "AU")

  val all = List(ukNetworkFront, usNetworkFront, ausNetworkFront)


  implicit def pathBinder = new PathBindable[ScannedLocation] {
    override def bind(key: String, value: String): Either[String, ScannedLocation] = {
         for {
           prefix <- Right(value).right
           loc <- all.find(_.bucketPrefix == prefix).toRight(left = "Unknown location").right
         } yield loc
       }

       override def unbind(key: String, loc: ScannedLocation): String = loc.bucketPrefix
  }
}
