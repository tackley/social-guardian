package net.tackley.sg.snippet

import net.tackley.sg.lib.Current
import net.liftweb.util.Helpers._

class DisplayResults {

  def render =  "li *" #>  Current.item.results.map { item =>
       ".link-text" #> <a href={"/"+item.id}>{item.webTitle}</a>
     }

}