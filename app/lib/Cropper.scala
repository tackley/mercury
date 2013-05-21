package lib

import java.io.File
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.awt.{RenderingHints, AlphaComposite}
import play.api.Logger
import scalax.file.Path

object Cropper {
  private lazy val log = Logger(getClass)

  def cropAndThumb(input: File, tmpDir: Path): (File, File) = {

    // and now crop!
    log.info("Loading to crop...")
    val cropHeight = 1200

    val cropFile = (tmpDir / "crop.png").fileOption.get
    val thumbFile = (tmpDir / "thumb.png").fileOption.get

    val img = ImageIO.read(input)
    log.info(s"size is ${img.getWidth}x${img.getHeight}")

    val crop = img.getSubimage(0, 260, img.getWidth, cropHeight)
    ImageIO.write(crop, "png", cropFile)

    val newWidth = img.getWidth / 6
    val newHeight = cropHeight / 6

    val scaledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB)
    val g = scaledImage.createGraphics()

    g.setComposite(AlphaComposite.Src)
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)

    g.drawImage(crop, 0, 0, newWidth, newHeight, null)
    g.dispose()

    ImageIO.write(scaledImage, "png", thumbFile)
    log.info("written")

    (cropFile, thumbFile)
  }
}
