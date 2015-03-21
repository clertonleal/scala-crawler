package model

import driver.PgDriver.simple._
import org.joda.time.LocalTime

/**
 * User: clertonleal
 * Date: 28/02/15
 * Time: 15:55
 */
class Hour(tag: Tag) extends Table[(Int, Int, LocalTime)](tag, "hours") {

  def id  = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def movieId = column[Int]("movie_id")
  def cinemaId = column[Int]("cinema_id")
  def hour = column[LocalTime]("hour")

  def * = (movieId, cinemaId, hour)

}