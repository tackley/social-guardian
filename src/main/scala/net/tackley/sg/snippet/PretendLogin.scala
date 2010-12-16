package net.tackley.sg.snippet

import net.liftweb.util.Helpers._
import net.tackley.sg.model.User
import net.liftweb.common.Full
import net.liftweb.http.{S, SHtml}

class PretendLogin {
  def render = "#loginName" #> SHtml.text("", login _) 

  def login(username: String) = {
    val user:User = User.createRecord
    user.name(username)
    user.fullName(username)
    user.oauthAccessToken("")
    user.oauthAccessSecret("")
    User.current(Full(user))

    println("I will now log you in as " + username)
    S.redirectTo("/")
  }
}