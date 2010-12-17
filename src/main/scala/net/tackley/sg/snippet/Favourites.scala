package net.tackley.sg.snippet
import net.tackley.sg.model.User
import net.liftweb.util.Helpers._
import com.gu.openplatform.contentapi.Api
import com.gu.openplatform.contentapi.model.Tag

/**
 * Created by IntelliJ IDEA.
 * User: mbs
 * Date: 16/12/2010
 * Time: 19:23
 * To change this template use File | Settings | File Templates.
 */

class Favourites {
  val user: User = User.current.is.get
  val tags = user.interestingTags.get.toList.sortBy {case (k,v) => v}.reverse.take(5).map(_._1)
  val apiTags: List[Tag]= Api.tags.ids(tags.mkString(",")).results

  def list = "li *" #> tags.map { tag =>
    ".taglink" #> <a href={"/" + tag}>{apiTags.find(_.id == tag).map(_.webTitle).getOrElse("")}</a>
  }
}