package bootstrap.liftweb

import net.liftweb.mongodb._
import net.liftweb.sitemap._
import net.tackley.sg.model.User
import net.liftweb.http._
import com.gu.openplatform.contentapi.Api
import net.tackley.sg.lib.OAuthLogin
import net.liftweb.common.{Logger, Box, Full}
import dispatch.oauth._
import net.liftweb.json.JsonDSL._
import net.liftweb.json.JsonAST._

class Boot {

  val nonGuPathRoots = "oauth" :: "index" :: LiftRules.cometPath :: LiftRules.ajaxPath :: Nil
  val nonGuPathRootsOption = nonGuPathRoots.map(Some(_))

  def boot = {
    MongoDB.defineDbAuth(DefaultMongoIdentifier, MongoAddress(MongoHost("flame.mongohq.com", 27063), "socialguardian"), "bruntonspall", "1234567")
    User.ensureIndex("name" -> 1, ("unique" -> true) ~ ("background" -> true))

    LiftRules.htmlProperties.default.set((r: Req) => new Html5Properties(r.userAgent))

    LiftRules.statelessRewrite.append{
      case RewriteRequest(ParsePath(path, "", _, _), GetRequest, httpreq)
        if !(nonGuPathRootsOption contains path.headOption) =>
        println("rewriting " + path)
        RewriteResponse("index" :: Nil, Map("path" -> path.mkString("/")))
    }

    LiftRules.addToPackages("net.tackley.sg")

    LiftRules.loggedInTest = Full(User.isLoggedIn _)

    Api.apiKey = Some("k8nd4jpt2fxmv3ewwevwahrr")

    LiftRules.dispatch.append {
      case Req("oauth" :: "signin" :: Nil, _, GetRequest) => doSignin _
      case Req("oauth" :: "callback" :: Nil, _, GetRequest) => () => doCallback
    }
  }

  val logger = Logger(this.getClass)

  def doSignin: Box[LiftResponse] = {
    for (request_token <- OAuthLogin.requestRequestToken) yield {
      S.set("oauth_request_token", request_token.value)
      S.set("oauth_request_secret", request_token.secret)
      RedirectResponse("http://api.twitter.com/oauth/authenticate?oauth_token=" + request_token.value)
    }
  }


  def doCallback: Box[LiftResponse] = {
    val token = Token(S.param("oauth_token").get, S.get("oauth_request_secret").get)
    val verifier = S.param("oauth_verifier").get
    // Now we trade token and verifier for an actual access_token which we can store
    val access_token = OAuthLogin.requestAccessToken(token, verifier).get
    val userDetails = OAuthLogin.requestUserDetails(access_token).get
    logger.debug("Access token = " + access_token)
    logger.debug("User = " + userDetails \ "name")
    val u = User.find("oauthAccessToken", access_token.value).getOrElse(User.createRecord)
    u.name(userDetails \ "screen_name" text)
    u.fullName(userDetails \ "name" text)
    u.oauthAccessToken(access_token.value)
    u.oauthAccessSecret(access_token.secret)
    u.save(strict = true)
    User.current(Full(u))
    return Full(RedirectResponse("/"))
  }

}