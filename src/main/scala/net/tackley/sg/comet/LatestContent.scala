package net.tackley.sg.comet

import net.liftweb.actor.LiftActor
import com.gu.openplatform.contentapi.model.Content
import com.gu.openplatform.contentapi.Api
import net.liftweb.http.{CometActor, CometListener, ListenerManager}
import net.liftweb.util.Helpers._
import xml.Unparsed
import org.joda.time.{Duration, DateTime}
import java.util.concurrent.{TimeUnit, Executors}

case class PollLatestContent()

object LatestContentServer extends LiftActor with ListenerManager {
  var latest: List[Content] = Nil

  def createUpdate = latest

  override def lowPriority = {
    case _ : PollLatestContent =>
      latest = Api.search.showFields("all").showTags("all").response.results
      updateListeners
  }



  val executor = Executors.newSingleThreadScheduledExecutor
  executor.scheduleAtFixedRate(
    new Runnable { def run = LatestContentServer ! PollLatestContent() }, 1, 10, TimeUnit.SECONDS)
}


class LatestContent extends CometActor with CometListener {
  var latest: List[Content] = Nil

  def registerWith = LatestContentServer

  override def lowPriority = {
    case l: List[Content] => latest = l; reRender
  }

  def render =
    "*" #> latest.map { item =>
      ".link-text" #> <a href={"/" + item.id}>{Unparsed(item.webTitle)}</a> &
      ".trail-text *" #> item.safeFields.get("trailText").map(Unparsed(_)) &
      ".ago" #> dateDiffFromNow(item.webPublicationDate)
    }


  def dateDiffFromNow(dt: DateTime) = {
    val ageInSeconds = new Duration(dt, new DateTime()).getStandardSeconds

    if (ageInSeconds <= 0)
      "now!"
    else if (ageInSeconds < 60)
      "%d seconds ago" format ageInSeconds
    else if (ageInSeconds < 120)
      "1 minute ago"
    else
      "%d minutes ago" format ageInSeconds / 60

  }

}



