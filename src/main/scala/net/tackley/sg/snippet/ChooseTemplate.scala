package net.tackley.sg.snippet

import net.liftweb.util.Helpers._
import net.tackley.sg.lib.Current
import net.liftweb.json.JsonDSL._
import net.tackley.sg.model.{User}
import net.liftweb.http.S
import java.util.Date

class ChooseTemplate {
  implicit val formats = net.liftweb.json.DefaultFormats
  
  def render = {
    logUserRequest
    "*" #> <lift:embed what={pickTemplate}></lift:embed>
  }


  def pickTemplate = {
    if (Current.item.content.isDefined) "content"
    else if (Current.item.section.isDefined) "section"
    else if (Current.item.tag.isDefined) "tag"
    else "front"
  }

  def logUserRequest = {
    for (user <- User.current.is) {
      user.history.set(user.history.get ::: S.uri :: Nil)
      user.lastVisited.set(S.uri)
      user.save
    }
  }
 
}