package net.tackley.sg.comet

import net.liftweb.actor.LiftActor
import net.liftweb.http.{CometActor, CometListener, ListenerManager}
import net.liftweb.util.Helpers._
import net.tackley.sg.model.User
import xml._

case class ReadingNowInfo(user: String, uri: String, pageName: String, trailText: String)


object ReadingNowServer extends LiftActor with ListenerManager {
  private var readingNow: List[ReadingNowInfo] = Nil

  def createUpdate = readingNow

  override def lowPriority = {
    case r: ReadingNowInfo => {
      readingNow = r :: readingNow.filter(_.user != r.user).take(9)
      updateListeners()
    }

  }
}

class ReadingNow extends CometActor with CometListener {
  private var readingNow: List[ReadingNowInfo] = Nil

  def registerWith = ReadingNowServer

  override def lowPriority = {
    case l: List[ReadingNowInfo] => readingNow = l; reRender()
  }

  def render = {
    val currentUsername = for (user <- User.current.is) yield user.name.get
    val currentUri = for (user <- User.current.is) yield user.lastVisited.get
    val byUrl = readingNow.filterNot(currentUsername === _.user).groupBy(_.uri)

    "*" #> byUrl.map { case (uri,infolist) =>
      ".link" #> <a href={uri}>{infolist.head.pageName}</a> &
      ".trail-text" #> Unparsed(infolist.head.trailText) &
      "* [class+]" #> ( if(currentUri === uri ) "currentUser" else "" ) &
        ".user" #> infolist.map(info =>
          "*" #> <a class="twittername" href={"http://twitter.com/"+info.user}>{"@"+info.user}</a>)
    }
  }
}

class ReadingNowMain extends CometActor with CometListener {
  private var readingNow: List[ReadingNowInfo] = Nil

  def registerWith = ReadingNowServer

  override def lowPriority = {
    case l: List[ReadingNowInfo] => readingNow = l; reRender()
  }

  def render = {
    val currentUsername = for (user <- User.current.is) yield user.name.get
    val currentUri = for (user <- User.current.is) yield user.lastVisited.get
    val byUrl = readingNow.filterNot(currentUsername === _.user).groupBy(_.uri)

    "*" #> byUrl.map { case (uri,infolist) =>
      ".link" #> <a href={uri}>{infolist.head.pageName}</a> &
      ".trail-text" #> Unparsed(infolist.head.trailText) &
      "* [class+]" #> ( if(currentUri === uri ) "currentUser" else "" ) &
      ".user" #> infolist.map(info =>
        "*" #> <a class="twittername" href={"http://twitter.com/"+info.user}>{"@"+info.user}</a>)
    }
  }
}