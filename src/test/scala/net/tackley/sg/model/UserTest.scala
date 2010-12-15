package net.tackley.sg.model

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec
import net.liftweb.mongodb._
import net.liftweb.record.Field

class UserTest extends FlatSpec with ShouldMatchers {
  MongoDB.defineDb(DefaultMongoIdentifier, MongoAddress(MongoHost("localhost"), "smash"))


  "User" should "be saved to mongo" in {
    val u: User = User.createRecord
    u.firstName("Test").password("pants").email("a@example.com").save

    u.fields.foreach { f: Field[_, _] => println(f.toXHtml) }

    println("user to xhtml: '" + u.toXHtml + "'")
  }
}