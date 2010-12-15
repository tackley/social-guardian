package net.tackley.sg.snippet

import net.liftweb.util.Helpers._
import net.tackley.sg.model.User
import net.liftweb.common._

class ShowUser extends Loggable {
  val aUser = User.current.is

  logger.info("User = " + aUser)


  //def render = "*" #> aUser.toForm(Full("Save")) { _.save }
}