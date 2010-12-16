package net.tackley.sg.snippet

import net.liftweb.util.Helpers._
import net.tackley.sg.model.User
import xml.Text
import net.liftweb.common.{Full, Empty}
import net.liftweb.http.{S, SHtml}

class Login  {
  def details = ".username *" #> User.current.is.get.fullName &
          "a " #> SHtml.link("#", logout _, Text("logout"))

  def pretend =
    "*" #> Full(S.hostName).filter(_ == "localhost").map { ignore =>
      "#loginName" #> SHtml.text("", login _)
    }

  def login(username: String) = {
    println("I will now log you in as " + username)

    val user: User = User.find("name", username) openOr {
      val newUser = User.createRecord
      newUser.name(username)
      newUser.fullName(username)
      newUser.oauthAccessToken("(pretend)")
      newUser.oauthAccessSecret("(pretend)")
      newUser.save(true)
    }

    User.current(Full(user))

    S.redirectTo("/")
  }

  def logout = User.current.set(Empty)
}