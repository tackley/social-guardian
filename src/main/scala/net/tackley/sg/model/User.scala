package net.tackley.sg.model

import net.liftweb.mongodb.record.{MongoId, MongoMetaRecord, MongoRecord}
import net.liftweb.record._
import field.{OptionalStringField, PasswordField, EmailField}
import net.liftweb.http.SessionVar
import net.liftweb.common._

class User extends MongoRecord[User] with MongoId[User] {
  def meta = User

  object email extends EmailField(this, 100)
  object password extends PasswordField(this)
  object oauthAccessToken extends OptionalStringField(this, 128)
  object oauthAccessSecret extends OptionalStringField(this, 128)
}

object User extends User with MongoMetaRecord[User]  {
  object current extends SessionVar[Box[User]](Empty)

  def isLoggedIn = current.isDefined
}