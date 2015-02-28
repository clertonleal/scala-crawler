package model
import driver.PgDriver.simple._

/**
  * User: clertonleal
 * Date: 21/02/15
 * Time: 11:56
 */

class Address(tag: Tag) extends Table[(String, String, String, String)](tag, "addresses") {

  def id  = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def street = column[String]("street")
  def city = column[String]("city")
  def state = column[String]("state")
  def cep = column[String]("cep")

  def * = (street, city, state, cep)

}
