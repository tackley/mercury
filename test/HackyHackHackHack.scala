import java.awt.image.BufferedImage
import java.awt.{AlphaComposite, RenderingHints}
import java.io.File
import java.net.URL
import javax.imageio.ImageIO
import scala.language.reflectiveCalls

object HackyHackHackHack extends App {
  println("hello")

  val log = new {
    def info(s: String) { println(s) }
  }

  val pngFile = new URL("https://ophan-time-machine.s3.amazonaws.com/uknf/2013/05/20/2013-05-20T14:29_full.png")

  log.info("Loading to crop...")
  val cropHeight = 800

  val img = ImageIO.read(pngFile)
  log.info(s"size is ${img.getWidth}x${img.getHeight}")

  val crop = img.getSubimage(0, 260, img.getWidth, cropHeight)
  ImageIO.write(crop, "png", new File("/tmp/crop.png"))


  val newWidth = img.getWidth / 6
  val newHeight = cropHeight / 6

  val scaledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB)
  val g = scaledImage.createGraphics()

  g.setComposite(AlphaComposite.Src)
  g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
  g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)

  g.drawImage(crop, 0, 0, newWidth, newHeight, null)
  g.dispose()

  ImageIO.write(scaledImage, "png", new File("/tmp/scaled.png"))
  log.info("written")
}
