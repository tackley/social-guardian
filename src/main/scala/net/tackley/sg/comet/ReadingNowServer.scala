package net.tackley.sg.comet

import net.liftweb.actor.LiftActor
import net.liftweb.http.{CometActor, CometListener, ListenerManager}
import net.liftweb.util.Helpers._
import net.tackley.sg.model.User


case class ReadingNowInfo(user: String, uri: String, pageName: String)


object ReadingNowServer extends LiftActor with ListenerManager {
  private var readingNow: List[ReadingNowInfo] = Nil

  def createUpdate = readingNow

  override def lowPriority = {
    case r: ReadingNowInfo => {
      readingNow = r :: readingNow.filter(_.user != r.user)
      println("reading now list updated to - " + readingNow)
      updateListeners()
    }

  }
}

class ReadingNow extends CometActor with CometListener {
  private var readingNow: List[ReadingNowInfo] = Nil

  def registerWith = ReadingNowServer

  override def lowPriority = {
    case l: List[ReadingNowInfo] => {
      println("got a new list in reading now actor: " + readingNow)
      readingNow = l; reRender()
    }
  }

  def render = {
    println("rendering reading now! - " + readingNow)
    println("by the way I think the current user is " + User.current.is)

    val currentUsername = for (user <- User.current.is) yield user.name.get

    "li" #> readingNow.filterNot(currentUsername === _.user).map { r =>
      ".user *" #> ("@" + r.user) &
      ".link" #> <a href={r.uri}>{r.pageName}</a>
    }
  }
}