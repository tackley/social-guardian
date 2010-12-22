package net.tackley.sg.snippet

import scala.collection.mutable.Map
import net.tackley.sg.lib.Current
import net.liftweb.util.Helpers._
import net.tackley.sg.model.{User}
import xml.Unparsed
import net.liftweb.common.Loggable
import xml._

class DisplayContent extends Loggable {
   def render = {
     logUserRequest
     ".headline *" #> Unparsed(content.webTitle) &
     ".standfirst *" #> content.safeFields.get("standfirst").map(Unparsed(_)) &
     ".byline *" #> content.safeFields.get("byline").map(Unparsed(_)) &
     ".body *" #> body &
     ".thumbnail *" #> thumbnail
   }

  def logUserRequest = {
    for (user <- User.current.is) {
      var tagMap = user.interestingTags.get
      val interestedTags = content.tags.filter(_.tagType=="keyword").map(tag => (tag.id,(tagMap.getOrElse(tag.id,0)+1))).toMap
      user.interestingTags.set(tagMap++interestedTags)
      user.save
    }
  }

  lazy val content = Current.item.content.get

  lazy val rawBody = content.safeFields.get("body")

  lazy val thumbnail = rawBody match {
    case None => Text("")
    case Some("<!-- Redistribution rights for this field are unavailable -->") => Text("")
    case Some(other) => content.safeFields.get("thumbnail").map(thumb => <img src={thumb} class="top pull-1 left" />).getOrElse(NodeSeq.Empty)
  }

  lazy val body = rawBody match {
    case None =>
      <div class="no-body">
        Sorry, at the moment The Social Guardian doesn't know how to display this kind of content.
        <a href={content.webUrl} target="_blank">
          You may if you wish abandon your social engagement and view on guardian.co.uk instead.
        </a>
      </div>

    case Some("<!-- Redistribution rights for this field are unavailable -->") =>
      <div class="no-rights">
        Sorry but the guardian isnt able to redistribute this content, so we don't have it.
        <a href={content.webUrl} target="_blank">
          You may if you wish abandon your social engagement and view on guardian.co.uk instead.
        </a>

      </div>

    case Some(other) => Unparsed(other)
  }

}