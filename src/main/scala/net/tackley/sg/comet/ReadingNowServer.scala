package net.tackley.sg.comet

import net.liftweb.actor.LiftActor
import net.liftweb.http.{CometActor, CometListener, ListenerManager}
import net.liftweb.util.Helpers._

object ReadingNowServer extends LiftActor with ListenerManager {
  private var readingNow: Map[String, String] = Map.empty

  def createUpdate = readingNow

  override def lowPriority = {
    case (user: String, uri: String) => {
      readingNow = readingNow.updated(user, uri)
      println("reading now map updated to - " + readingNow)
      updateListeners()
    }

  }
}

class ReadingNow extends CometActor with CometListener {
  private var readingNow: Map[String, String] = Map.empty

  def registerWith = ReadingNowServer

  override def lowPriority = {
    case m: Map[String, String] => {
      println("got a new map in reading now actor: " + readingNow)
      readingNow = m; reRender()
    }
  }

  def render = {
    println("rendering reading now! - " + readingNow)
    "li" #> readingNow.map { case (user, uri) =>
      ".user *" #> user & ".uri *" #> uri
    }
  }
}