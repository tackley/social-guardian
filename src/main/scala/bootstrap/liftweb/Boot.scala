package bootstrap.liftweb

import net.liftweb.mongodb._
import net.liftweb.sitemap._
import net.tackley.sg.model.User
import net.liftweb.http._
import net.tackley.sg.lib.OAuthLogin
import net.liftweb.common.{Logger, Box, Full}
import dispatch.oauth._


class Boot {
  import Loc._

  lazy val isLoggedIn = () => User.isLoggedIn

  lazy val notLoggedIn = Unless(isLoggedIn, "You must not be logged in")
  lazy val loggedIn = If(isLoggedIn, "You must be logged in")

  def menus =
      Menu("Home") / "index" ::
      (Menu("Login") / "login" >> notLoggedIn) ::
      (Menu("Signup") / "signup" >> notLoggedIn) ::
      (Menu("Test Page") / "test" >> loggedIn) ::
      Nil


  def boot = {
    MongoDB.defineDb(DefaultMongoIdentifier, MongoAddress(MongoHost("localhost"), "sg"))

    LiftRules.htmlProperties.default.set((r: Req) => new Html5Properties(r.userAgent))

    LiftRules.addToPackages("net.tackley.sg")

    LiftRules.setSiteMap(SiteMap(menus: _*))

    LiftRules.dispatch.append {
      case Req("oauth" :: "signin" :: Nil, _, GetRequest) => doSignin _
      case Req("oauth" :: "callback" :: Nil, _, GetRequest) => () => doCallback
    }
  }

  def doSignin:Box[LiftResponse] = {
    val request_token = OAuthLogin.get_request_token.get
    S.set("oauth_request_token", request_token.value)
    S.set("oauth_request_secret", request_token.secret)
    return Full(RedirectResponse("http://api.twitter.com/oauth/authenticate?oauth_token="+request_token.value))
  }
  def doCallback:Box[LiftResponse] = {
    val logger = Logger(this.getClass)
    // http://social-guardian.bruntonspall.staxapps.net/oauth/callback?
    // oauth_token=VbSHfSQdqZ4EOCAF7LCl7BJoYtdMt2ERWKHDd1bY&
    // oauth_verifier=iUPNHlIy3ZkmrR2ukCv936wHY1amICMqI57AXZQkF14
    val token = Token(S.param("oauth_token").get, S.get("oauth_request_secret").get)
    val verifier = S.param("oauth_verifier").get
    // Now we trade token and verifier for an actual access_token which we can store
    val access_token = OAuthLogin.get_access_token(token, verifier).get
    val user_details = OAuthLogin.get_user_details(access_token)
    logger.debug("Access token = "+access_token)
//    val aUser = User.current.is.get
//    aUser.oauth_access_token = access_token.get.value
//    aUser.oauth_access_secret = access_token.get.secret
//    aUser.save
    return Full(RedirectResponse("/"))
  }

}