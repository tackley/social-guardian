package net.tackley.sg.snippet

import net.tackley.sg.lib.Current
import net.liftweb.util.Helpers._
import xml.Unparsed

class DisplayContent {
   def render = {
     ".headline *" #> Unparsed(content.webTitle) &
     ".standfirst *" #> content.safeFields.get("standfirst").map(Unparsed(_)) &
     ".byline *" #> content.safeFields.get("byline").map(Unparsed(_)) &
     ".body *" #> content.safeFields.get("body").map(Unparsed(_))
   }

  lazy val content = Current.item.content.get

}