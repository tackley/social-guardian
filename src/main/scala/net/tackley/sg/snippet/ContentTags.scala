package net.tackley.sg.snippet
import net.tackley.sg.lib.Current
import net.liftweb.util.Helpers._

/**
 * Created by IntelliJ IDEA.
 * User: mbs
 * Date: 17/12/2010
 * Time: 14:30
 * To change this template use File | Settings | File Templates.
 */

class ContentTags {
  def render =
    "*" #> Current.item.content.map { content =>
      ".tags *" #> content.tags.filter(_.tagType=="keyword").map { item =>
         ".link-text" #> <a href={"/"+item.id}>{item.webTitle}</a>
      }
    }

}