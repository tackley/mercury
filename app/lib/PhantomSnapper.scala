package lib

import java.io.File
import scalax.file.Path
import play.api.Logger

object PhantomSnapper {
  private val log = Logger.logger

  def snap(url: String, tmpDir: Path): File = {
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
