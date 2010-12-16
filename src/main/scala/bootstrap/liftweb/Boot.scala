package bootstrap.liftweb

import net.liftweb.mongodb._
import net.liftweb.sitemap._
import net.tackley.sg.model.User
import net.liftweb.http._

class Boot {
  import Loc._

  def boot = {
    MongoDB.defineDb(DefaultMongoIdentifier, MongoAddress(MongoHost("localhost"), "sg"))

    LiftRules.htmlProperties.default.set((r: Req) => new Html5Properties(r.userAgent))

   LiftRules.statelessRewrite.append {
       case RewriteRequest(ParsePath(path, "", _, _), GetRequest, httpreq) if path != ("index" :: Nil) =>
          println("rewriting " + path)
          RewriteResponse("index" :: Nil, Map("path" -> path.mkString("/")))
     }

    LiftRules.addToPackages("net.tackley.sg")
  }
}