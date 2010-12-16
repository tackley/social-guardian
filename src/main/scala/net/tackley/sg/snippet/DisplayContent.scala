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
     "li *" #> content.tags.filter(_.tagType=="keyword").map { item =>
       ".link-text" #> <a href={"/"+item.id}>{item.webTitle}</a>
     }
   }

  def logUserRequest = {
    for (user <- User.current.is) {
      var tagMap = user.interestingTags.get
      for (tag <- content.tags) {
        val value = tagMap.getOrElse(tag.id, 0)+1
        tagMap = tagMap.updated(tag.id, value)
      }
      user.interestingTags.set(tagMap)
      user.save
    }
  }
  lazy val content = Current.item.content.get

}