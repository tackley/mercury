package lib

import play.api.Logger
import java.io.File
import scalax.file.Path

import scala.sys.process._
import scalax.file.defaultfs.DefaultPath

object ImageMagick {

  private val log = Logger.logger

  // imagemagick equivalents of this:
  // crop:
  //   convert src.png -crop x1200+0+260 crop.png
  // thumb where text is readable:
  //   convert crop.png -resize 35% thumb.png
  // and together that is:
  //   convert src.png -crop x1200+0+260 -resize 35% newthumb.jpg
  // making the full image 6 times smaller:
  //   convert src.png -quality 30 conv.jpg


  def compress(file: File, tmpDir: Path): File = {
    log.info("Compressing...")
    val output = tmpDir / "compressed.jpg"

    Seq("convert", file.getAbsolutePath, "-quality", "30", output.path).!!

    output.fileOption.get
  }

  def thumb(file: File, tmpDir: Path): File = {
    log.info("Creating thumbnail...")

    val output = tmpDir / "thumb.jpg"

    Seq("convert", file.getAbsolutePath, "-crop", "x1200+0+260", "-resize", "35%", output.path).!!

    output.fileOption.get
  }

}
