package net.tackley.sg.model

import net.liftweb.mongodb.record.{MongoId, MongoMetaRecord, MongoRecord}
import net.liftweb.record._
import field.{StringField, OptionalStringField, PasswordField, EmailField}
import net.liftweb.http.SessionVar
import net.liftweb.common._
import java.util.Date
import net.liftweb.mongodb.{JsonObjectMeta, JsonObject}
import net.liftweb.mongodb.record.field.{MongoMapField, MongoListField, MongoJsonObjectListField}

class User extends MongoRecord[User] with MongoId[User] {
  def meta = User

  object name extends StringField(this, 64)
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

object User extends User with MongoMetaRecord[User]  {
  object current extends SessionVar[Box[User]](Empty)

  def isLoggedIn = current.isDefined
}
