package net.tackley.sg.lib

import org.apache.commons.codec.binary.Base64
import net.liftweb.json._
import dispatch._
import net.liftweb.util.Helpers._
import net.liftweb.util.Props
import java.net.URL
import java.io.InputStreamReader
import net.liftweb.common.{Empty, Box, Loggable}
import net.liftweb.http.{Req, S, RequestVar}

case class SummaryFacebookAge(
  min: Option[Int],
  max: Option[Int])

case class SummaryFacebookUser(
  country: String,
  locale: String,
  age: SummaryFacebookAge)

case class SignedRequest(
  algorithm: String,
  issued_at: Long,
  user: SummaryFacebookUser,
  user_id: Option[String],
  oauth_token: Option[String]
)


case class FacebookUser(
  id: String,
  name: String
                 )

object Facebook extends Loggable {
  val appId = Props.get("facebook_app_id").open_!
  val canvasPage = "http://apps.facebook.com/socialguardian-demo/"

  object signedRequest extends RequestVar[Box[SignedRequest]](parseSignedRequest)

  implicit val formats = DefaultFormats

  def parseSignedRequest = {
    for (p <- S.param("signed_request")) yield {
      val Array(sig, encodedJson) = p.split('.')
      val json = new String(new Base64(true).decode(encodedJson), "UTF-8")

      JsonParser.parse(json).extract[SignedRequest]
    }
  }

  def me = signedRequest.is map { sr =>
    val myUrl = appendParams("https://graph.facebook.com/me", List("access_token" -> sr.oauth_token.get))
    JsonParser.parse(new InputStreamReader(new URL(myUrl).openStream, "UTF-8")).extract[FacebookUser]
  }
}