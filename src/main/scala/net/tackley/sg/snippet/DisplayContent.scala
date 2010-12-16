package net.tackley.sg.snippet

import net.tackley.sg.lib.Current
import net.liftweb.util.Helpers._

class DisplayContent {
   def render = "*" #> Current.item.content.get.webTitle

}