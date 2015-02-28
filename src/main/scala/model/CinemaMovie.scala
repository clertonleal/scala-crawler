package model
import driver.PgDriver.simple._

/**
 * User: clertonleal
 * Date: 28/02/15
 * Time: 12:53
 */
class CinemaMovie(tag: Tag) extends Table[(Int, Int)](tag, "cinema_movies") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def cinemaId = column[Int]("cinema_id")
  def movieId = column[Int]("movie_id")

  def * = (cinemaId, movieId)

}
