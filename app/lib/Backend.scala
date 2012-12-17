package lib

import play.api.libs.concurrent.Akka
import play.api.Play.current

import akka.util.duration._

object Backend {
  val poller = new Poller("http://www.guardian.co.uk")(Akka.system)

  def start() {

    Akka.system.scheduler.schedule(5 seconds, 30 seconds) {
      poller.poll()
    }
  }
}
