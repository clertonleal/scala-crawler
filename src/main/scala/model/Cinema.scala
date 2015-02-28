package model
import driver.PgDriver.simple._

/**
 * User: clertonleal
 * Date: 21/02/15
 * Time: 10:48
 */
class Cinema(tag: Tag) extends Table[(String, Int)](tag, "cinemas") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def addressId = column[Int]("address_id")
  def * = (name, addressId)


  def address = foreignKey("cinemas_pkey", addressId, TableQuery[Address])(_.id)
}
