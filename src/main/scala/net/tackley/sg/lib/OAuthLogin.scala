package net.tackley.sg.lib

import org.apache.commons.httpclient.methods.GetMethod
import dispatch.oauth._
import io.Source
import net.liftweb.util.Helpers.{urlDecode, appendParams}
import org.apache.commons.httpclient.{HttpClient, MultiThreadedHttpConnectionManager}
import net.liftweb.common.Loggable


object OAuthLogin extends Loggable {

  val consumer_token = Consumer("fbvfFeJMOIr776WVeCcPw", "sXVB1OojJFxJrmrzCOgeuyBU91nO8DxbwXMbWWcP3pg")


  lazy val connectionManager = new MultiThreadedHttpConnectionManager
  lazy val httpClient = new HttpClient(connectionManager)
  val split_decode: (String => Map[String, String]) = {
      case null => Map.empty
      case query => Map.empty ++ query.trim.split('&').map { nvp =>
        ( nvp split "=" map urlDecode ) match {
          case Array(name) => name -> ""
          case Array(name, value) => name -> value
        }
      }
    }
  def get_request_token():Option[Token] = {
    val url = "http://api.twitter.com/oauth/request_token"
    val parameters = OAuth.sign("GET", url, Map.empty, consumer_token, Option.empty, Option.empty, Option.empty)
    val method = new GetMethod(appendParams (url, parameters.toSeq))
    try {

      httpClient.executeMethod(method)

      val statusLine = method getStatusLine
      val responseBody = Option(method.getResponseBodyAsStream)
              .map(Source.fromInputStream(_).mkString)
              .getOrElse("")
      logger.info("Request Token: "+ statusLine +" - " +responseBody)
      Token(split_decode(responseBody))
    } finally {
      method.releaseConnection
    }
  }

  def get_access_token(request_token:Token, request_verifier:String):Option[Token] = {
    val url = "http://api.twitter.com/oauth/access_token"
    val parameters = OAuth.sign("GET", url, Map.empty, consumer_token, Some(request_token), Some(request_verifier), Option.empty)
    val method = new GetMethod(appendParams (url, parameters.toSeq))
    try {

      httpClient.executeMethod(method)

      val statusLine = method getStatusLine
      val responseBody = Option(method.getResponseBodyAsStream)
              .map(Source.fromInputStream(_).mkString)
              .getOrElse("")
      logger.info("Access Token: "+ statusLine +" - " +responseBody)
      Token(split_decode(responseBody))
    } finally {
      method.releaseConnection
    }
  }

  def get_user_details(request_token:Token):Option[String] = {
    val url = "http://api.twitter.com/1/account/verify_credentials.xml"
    val parameters = OAuth.sign("GET", url, Map.empty, consumer_token, Some(request_token), Option.empty, Option.empty)
    val method = new GetMethod(appendParams (url, parameters.toSeq))
    try {

      httpClient.executeMethod(method)

      val statusLine = method getStatusLine
      val responseBody = Option(method.getResponseBodyAsStream)
              .map(Source.fromInputStream(_).mkString)
              .getOrElse("")
      logger.info("User Details: "+ statusLine +" - " +responseBody)
      Some(responseBody)
    } finally {
      method.releaseConnection
    }
  }

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