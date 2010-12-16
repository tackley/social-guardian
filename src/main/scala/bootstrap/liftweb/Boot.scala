package bootstrap.liftweb

import net.liftweb.mongodb._
import net.liftweb.sitemap._
import net.tackley.sg.model.User
import net.liftweb.http._
import net.liftweb.common.{Box, Full}
import net.tackley.sg.lib.OAuthLogin

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
      case Req("signin" :: Nil, _, GetRequest) => doSignin _
//      case Req("callback" :: Nil, _, GetRequest) => () => doCallback
    }
  }

  def doSignin:Box[LiftResponse] = {
    return Full(RedirectResponse("http://api.twitter.com/oauth/authenticate?oauth_token="+OAuthLogin.get_request_token.get.value))
  }

}