package net.tackley.sg.snippet

import scala.collection.mutable.Map
import net.tackley.sg.lib.Current
import net.liftweb.util.Helpers._
import net.tackley.sg.model.{User}
import xml.Unparsed
import net.liftweb.common.Loggable

class DisplayContent extends Loggable {
   def render = {
     logUserRequest
     ".headline *" #> Unparsed(content.webTitle) &
     ".standfirst *" #> content.safeFields.get("standfirst").map(Unparsed(_)) &
     ".byline *" #> content.safeFields.get("byline").map(Unparsed(_)) &
     ".body *" #> content.safeFields.get("body").map(Unparsed(_)) &
     ".tags *" #> content.tags.filter(_.tagType=="keyword").map { item =>
       ".link-text" #> <a href={"/"+item.id}>{item.webTitle}</a>
     }
   }

  def logUserRequest = {
    for (user <- User.current.is) {
      var tagMap = user.interestingTags.get
      val interestedTags = content.tags.filter(_.tagType!="type").map(tag => (tag.id,(tagMap.getOrElse(tag.id,0)+1))).toMap
      user.interestingTags.set(tagMap++interestedTags)
      user.save
    }
  }
  lazy val content = Current.item.content.get

}