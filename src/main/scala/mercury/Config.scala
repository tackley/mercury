package mercury

import org.joda.time.Duration


object Config {

  // This must match the scan interval configured in the cron task
  // file (cron.yaml)
  val scanInterval = Duration.standardMinutes(5)

  // Sometimes we look at the the "to" date of a history entry, and
  // want to know whether it is the most recent reading we have taken.
  // The hack for this is to pad the scan interval a bit:
  val readingIsLatestIfWithin = Duration.standardMinutes(8)
}
