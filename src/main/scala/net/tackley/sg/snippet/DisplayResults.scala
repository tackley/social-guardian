package net.tackley.sg.snippet

import net.tackley.sg.lib.Current
import net.liftweb.util.Helpers._
import com.gu.openplatform.contentapi.model.Content
import xml._

class DisplayResults {

  def bind(content: List[Content]) = "*" #> content.map { item =>
    ".link-text" #> <a href={"/" + item.id}>{item.webTitle}</a> &
    ".trail-text *" #> item.safeFields.get("trailText").map(Unparsed(_))
  }

  def col1 = bind(cols(0))
  def col2 = bind(cols(1))
  def col3 = bind(cols(2))


  lazy val results = (Current.item.editorsPicks ++ Current.item.results).distinct
  lazy val itemsPerCol = results.size / 3
  lazy val cols = results.grouped(itemsPerCol).toList.padTo(3, Nil)
}