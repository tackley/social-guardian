package net.tackley.sg.snippet

import net.liftweb.util.Helpers._
import net.tackley.sg.lib.Current

class ChooseTemplate {
  def render = "*" #> <lift:embed what={pickTemplate}></lift:embed>


  def pickTemplate = {
    if (Current.item.content.isDefined) "content"
    else if (Current.item.section.isDefined) "section"
    else if (Current.item.tag.isDefined) "tag"
    else "front"
  }
 
}