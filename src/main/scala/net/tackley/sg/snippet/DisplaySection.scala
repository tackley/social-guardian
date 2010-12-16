package net.tackley.sg.snippet
import net.liftweb.util.Helpers._
import xml.Unparsed
import net.tackley.sg.lib.Current

class DisplaySection {

   def render =  ".headline *" #> Unparsed(section.webTitle)
   lazy val section = Current.item.section.get


}