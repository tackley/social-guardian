package net.tackley.sg.snippet

import net.liftweb.util.Helpers._
import net.liftweb.http.S
import net.liftweb.http.js.JE._
import net.liftweb.http.js.JsCmds._
import net.tackley.sg.lib.Facebook


class FacebookInfo {
  def show = "* *" #>
    <xml:group>
      <p>signed_request = {S.param("signed_request")}</p>
      <p>json = {Facebook.signedRequest.is}</p>
    </xml:group>

  val oauthDialogUrl = appendParams("http://www.facebook.com/dialog/oauth",
    List("client_id" -> Facebook.appId, "redirect_uri" -> Facebook.canvasPage))

  def oauthDialog = "* *" #> Script(SetExp(JsVar("top.location.href"), oauthDialogUrl))
}