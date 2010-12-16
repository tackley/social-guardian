package net.tackley.sg.snippet

import net.liftweb.util.Helpers._
import net.tackley.sg.model.User
import net.liftweb.http.SHtml
import net.liftweb.common.Empty
import xml.Text

class LoginDetails  {
  def render =  ".username *" #> User.current.is.get.name &
          "a " #> SHtml.link("#", logout _, Text("logout"))


  def logout = User.current.set(Empty)
}