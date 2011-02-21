package net.tackley.sg.model

import net.liftweb.mongodb.record.{MongoId, MongoMetaRecord, MongoRecord}
import net.liftweb.record._
import field.{StringField, OptionalStringField, PasswordField, EmailField}
import java.util.Date
import net.liftweb.mongodb.{JsonObjectMeta, JsonObject}
import net.liftweb.mongodb.record.field.{MongoMapField, MongoListField, MongoJsonObjectListField}
import net.tackley.sg.lib.Facebook
import net.liftweb.common._
import net.liftweb.http.{Req, S, SessionVar}

class User extends MongoRecord[User] with MongoId[User] {
  def meta = User

  object name extends StringField(this, 64)
  object facebookId extends StringField(this, 64)
  object profilePage extends StringField(this, 500)
  object fullName extends StringField(this, 64)
  object email extends EmailField(this, 100)
  object password extends PasswordField(this)
  object oauthAccessToken extends StringField(this, 128)
  object oauthAccessSecret extends StringField(this, 128)

  object history extends MongoListField[User, String](this)
  object lastVisited extends StringField(this, 500)
  object interestingTags extends MongoMapField[User, Int](this)

  def incrementTagCount(tagId: String, delta: Int) = {
    val tagMap = interestingTags.get
    val currentCount = tagMap.getOrElse(tagId, 0)
    val newCount = currentCount + delta
    interestingTags.set(tagMap.updated(tagId, newCount))
  }
}

object User extends User with MongoMetaRecord[User] with Loggable  {
  object current extends SessionVar[Box[User]](Empty)

  def isLoggedIn = {
    if (S.post_?) syncWithFacebookLogin
    current.isDefined
  }

  def syncWithFacebookLogin {
    logger.info("Syncing with facebook login... (signed request is " + Facebook.signedRequest.is + ")")

    // don't do anything if we don't have a signed request
    for (signedRequest <- Facebook.signedRequest.is) {
      signedRequest.user_id match {
        case None =>
          logger.info(" -> No user_id in request, logging out")
          User.current(Empty)
        case Some(fbId) => User.current.is match {
          case Empty =>
            logger.info(" -> Logging in as user id " + fbId)
            val me = Facebook.me
            logger.info("me = " + me)
            val u = User.find("facebookId", fbId).getOrElse(User.createRecord)
            u.facebookId(fbId)
            val name = me.map(_.name) openOr "Unknown"
            u.name(name)
            u.fullName(name)
            u.profilePage(me.map(_.link) openOr "http://www.facebook.com")
            u.save(strict = true)
            User.current(Full(u))

          case Full(u) if Some(u.facebookId.get) != signedRequest.user_id =>
            logger.info(" -> Userid doesn't match should log out : " + Some(u.facebookId) + " -> " + signedRequest.user_id)
            User.current(Empty)

          case _ => // nothing to do
            logger.info(" -> No changes made")
        }
      }
    }
  }
}
