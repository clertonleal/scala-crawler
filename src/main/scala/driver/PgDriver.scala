package driver


import com.github.tminglei.slickpg.PgDateSupportJoda

import scala.slick.driver.PostgresDriver

/**
 * User: clertonleal
 * Date: 26/02/15
 * Time: 17:30
 */

object PgDriver extends MyPostgresDriver

trait MyPostgresDriver extends PostgresDriver with PgDateSupportJoda {

  override lazy val Implicit = new ImplicitsPlus {}
  override val simple = new SimpleQLPlus {}
  trait ImplicitsPlus extends Implicits with DateTimeImplicits
  trait SimpleQLPlus extends SimpleQL with ImplicitsPlus

}

