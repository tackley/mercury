package lib

import java.io.File
import scalax.file.Path
import play.api.Logger

object PhantomSnapper {
  private val log = Logger.logger

  // the synchronized here is to stop us kicking up > 1 phantomjs process at once -
  // we're only running on a micro after all!
  def snap(url: String, tmpDir: Path): File = synchronized {
    import scala.sys.process._

    val pngOutput = tmpDir / "output.png"

    val cmd =
      s"""
      |var page = require('webpage').create();
      |page.open('$url', function () {
      |    page.render('${pngOutput.path}');
      |    phantom.exit();
      |});
    """.stripMargin

    val cmdFile = tmpDir / "cmd.js"
    cmdFile.write(cmd)

    log.info("Snapping " + url)

    Seq("phantomjs", cmdFile.path).!!

    log.info("Snapped!")

    pngOutput.fileOption.get
  }

}
