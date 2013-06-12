package lib

import play.api.libs.concurrent.Akka
import play.api.Play.current

import scala.concurrent.duration._

object Backend {
  import play.api.libs.concurrent.Execution.Implicits._

  val poller = new Poller(ScannedLocation.all)(Akka.system)

  def start() {
    Akka.system.scheduler.schedule(5.seconds, 1.minutes) {
      poller.poll()
    }
  }
}
