package net.tackley.sg.snippet

import net.liftweb._
import util.Helpers._
import http._
import net.tackley.sg.model.User

class SignupForm extends StatefulSnippet {
  val newUser: User = User.createRecord

  def dispatch = { case _ => render }

  def render =
    "#email" #> SHtml.text(newUser.email.get, newUser.email.set(_)) &
    "#password" #> SHtml.password(newUser.password.get, newUser.password.set(_)) &
    "#submit" #> SHtml.submitButton(validateAndSave _)

  def validateAndSave = {
    newUser.validate match {
      case Nil => newUser.save; S.notice("Saved")
      case errors => S.error(errors)
    }
  }
}