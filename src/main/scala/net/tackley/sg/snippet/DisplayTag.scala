package net.tackley.sg.snippet

import net.liftweb.util.Helpers._
import xml.Unparsed
import net.tackley.sg.lib.Current

class DisplayTag {

  def render =  ".headline *" #> Unparsed(tag.webTitle)
   lazy val tag = Current.item.tag.get

}