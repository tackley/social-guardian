package bootstrap.liftweb

import net.liftweb.mongodb._
import net.liftweb.sitemap._
import net.tackley.sg.model.User
import net.liftweb.http._
import com.gu.openplatform.contentapi.Api
import net.liftweb.common.{Logger, Box, Full}
import dispatch.oauth._
import net.liftweb.json.JsonDSL._
import net.liftweb.json.JsonAST._
import net.liftweb.util.Props

class Boot {
  val logger = Logger(this.getClass)

  val nonGuPathRoots = "oauth" :: "index" :: LiftRules.cometPath :: LiftRules.ajaxPath :: Nil
  val nonGuPathRootsOption = nonGuPathRoots.map(Some(_))

  def boot = {
    Props.requireOrDie("mongo_database", "mongo_username", "mongo_password", "gu_api_key", "facebook_app_id")

    MongoDB.defineDbAuth(DefaultMongoIdentifier,
      MongoAddress(MongoHost("flame.mongohq.com", 27073),
        Props.get("mongo_database").open_!),
        Props.get("mongo_username").open_!,
        Props.get("mongo_password").open_!)

    User.ensureIndex("facebookId" -> 1, ("unique" -> true) ~ ("background" -> true))

    LiftRules.htmlProperties.default.set((r: Req) => new Html5Properties(r.userAgent))

    LiftRules.statelessRewrite.append{
      case RewriteRequest(ParsePath(path, "", _, _), _, httpreq)
        if !(nonGuPathRootsOption contains path.headOption) =>
          RewriteResponse("index" :: Nil, Map("path" -> path.mkString("/")))
    }

    LiftRules.addToPackages("net.tackley.sg")

    LiftRules.loggedInTest = Full(User.isLoggedIn _)

    Api.apiKey = Props.get("gu_api_key")
  }



}