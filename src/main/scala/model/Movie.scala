package model
import driver.PgDriver.simple._

/**
 * User: clertonleal
 * Date: 21/02/15
 * Time: 13:35
 */
class Movie(tag: Tag) extends Table[(String, String, Int, String, String, String)](tag, "movies")  {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def title = column[String]("title")
  def synopsis = column[String]("synopsis")
  def durationInMinutes = column[Int]("durationInMinutes")
  def director = column[String]("director")
  def gender = column[String]("gender")
  def coverUrl = column[String]("cover_url")

  def * = (title, synopsis, durationInMinutes, director, gender, coverUrl)

}
