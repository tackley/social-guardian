package bootstrap.liftweb

import net.liftweb.mongodb._
import net.liftweb.sitemap._
import net.tackley.sg.model.User
import net.liftweb.http.{GetRequest, Html5Properties, Req, LiftRules}
import dispatch.oauth.Consumer
import dispatch.twitter.Auth

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

//    LiftRules.dispatch.append {
//      case Req("signin" :: Nil, _, GetRequest) => () => doSignin
//      case Req("callback" :: Nil, _, GetRequest) => () => doCallback
//    }
  }

//  def doSignin = {
//    val access_token = get_access_token(CONSUMER_KEY, CONSUMER_SECRET)
//    redirect("http://api.twitter.com/oauth/authorize?oauth_token="+generate_auth_token(access_token))
//  }
//  def doCallback = {
//    val token = q.get("oauth_token")
//    val secret = q.get("oauth.token")
//    user.set_credentials(token, secret)
//    credentials = get_twitter_credentials(token, secret)
//    user.name = credentials.realname
//    user.username = credentials.username
//  }

  /**How does OAuth actually work for User login?
   *
   * Since OAuth is percieved as complex, and lift is not my first webapp, here are my thoughts on
   * what the user flow is, and what we need to implement
   *
   * My investigation so far is that dispatch-twitter fundementally assumes that you are using the
   * dispatch framework (or at least I think that's what the Handler's are for.
   * Therefore I think we'll just use the low level functions of the dispatch-oauth which only
   * assumes that you are using apache-http-client for the communication as far as I can tell.
   *
   * So how does OAuth work?
   *
   * A user hits our site on the url /login
   * We need to send them on to twitter.  In order to do that we need 2 things.
   *  * A consumer key pair - this identifies us as website
   *  * An access keypair - this is uniquely generated, with a time stamp for this connection.
   *
   * We have to use our consumer key pair to request a brand new request keypair from twitter
   * Request goes to http://api.twitter.com/oauth/request_token
   * Response contains an access_token and access_secret
   * We then use that access keypair to create a signed url at twitter which we redirect the user to.
   * The user goes to http://api.twitter.com/oauth/authenticate?oauth_token=blah
   * If they hit accept they are sent back to our callback url, with the following parameters:
   *  * oauth_token - A storable, non-expiring token we can use to talk to twitter
   *  * oauth_secret - the secret to go with the token
   *
   * Finally we can store these credential in a database.  At any time we can call out to
   * twitter at http://api.twitter.com/account/verify_credentials.json with our access tokens
   * and we get back information about the user, including real name, username, picture and id.
   *
   */
}