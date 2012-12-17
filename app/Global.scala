import lib.Backend
import play.api._

object Global extends GlobalSettings {
  override def onStart(app: Application) {
    super.onStart(app)

    Logger.info("Starting...")

    Backend.start()

    Logger.info("Started")
  }
}
