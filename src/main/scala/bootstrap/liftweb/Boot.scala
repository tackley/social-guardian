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
    MongoDB.defineDbAuth(DefaultMongoIdentifier, MongoAddress(MongoHost("flame.mongohq.com", 27063), "socialguardian"), "bruntonspall", "1234567")

    LiftRules.htmlProperties.default.set((r: Req) => new Html5Properties(r.userAgent))

    LiftRules.addToPackages("net.tackley.sg")

    LiftRules.setSiteMap(SiteMap(menus: _*))

    LiftRules.dispatch.append {
      case Req("oauth" :: "signin" :: Nil, _, GetRequest) => doSignin _
      case Req("oauth" :: "callback" :: Nil, _, GetRequest) => () => doCallback
    }
  }

  val logger = Logger(this.getClass)

  def doSignin:Box[LiftResponse] = {
    val request_token = OAuthLogin.requestRequestToken.get
    S.set("oauth_request_token", request_token.value)
    S.set("oauth_request_secret", request_token.secret)
    return Full(RedirectResponse("http://api.twitter.com/oauth/authenticate?oauth_token="+request_token.value))
  }
  def doCallback:Box[LiftResponse] = {
    val token = Token(S.param("oauth_token").get, S.get("oauth_request_secret").get)
    val verifier = S.param("oauth_verifier").get
    // Now we trade token and verifier for an actual access_token which we can store
    val access_token = OAuthLogin.requestAccessToken(token, verifier).get
    val userDetails = OAuthLogin.requestUserDetails(access_token).get
    logger.debug("Access token = "+access_token)
    logger.debug("User = "+userDetails \ "name")
    val user:User = User.createRecord
    user.name(userDetails \ "screen_name" text)
    user.fullName(userDetails \ "name" text)
    user.oauthAccessToken(access_token.value)
    user.oauthAccessSecret(access_token.secret)
    user.save(strict = true)
    User.current(Full(user))
    return Full(RedirectResponse("/"))
  }

}