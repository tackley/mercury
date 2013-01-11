import org.joda.time.DateTime

package object mercury {
  implicit val dtOrdering: Ordering[DateTime] = Ordering.by(_.getMillis)

}
