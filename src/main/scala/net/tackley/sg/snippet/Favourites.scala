package net.tackley.sg.snippet
import net.tackley.sg.model.User
import net.liftweb.util.Helpers._
import com.gu.openplatform.contentapi.Api
import com.gu.openplatform.contentapi.model.Tag
import net.liftweb.http.SHtml
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds._
import xml._


class Favourites {
  val user: User = User.current.is.get
  def tags = user.interestingTags.get.toList.sortBy {case (k,v) => v}.reverse.take(5).map(_._1)


  def increment(tagId: String) : JsCmd =  {
    user.incrementTagCount(tagId, 5)
    user.save
    SetHtml("favtags", <lift:embed what="favourite_tags" />) &
    SetHtml("recom", <lift:embed what="recommendations" />)
  }

  def decrement(tagId: String) : JsCmd =  {
    user.incrementTagCount(tagId, -5)
    user.save
    SetHtml("favtags", <lift:embed what="favourite_tags" />) &
    SetHtml("recom", <lift:embed what="recommendations" />)
  }

  def buildIncrementLink(tagId: String) = SHtml

  def list = {
    val apiTags: List[Tag]= Api.tags.ids(tags.mkString(",")).results
    "li *" #> tags.map { tag =>
      ".taglink" #> <a href={"/" + tag}>{apiTags.find(_.id == tag).map(_.webTitle).getOrElse("")}</a> &
      ".like" #> SHtml.a(() => increment(tag), Text("( + )")) &
      ".dislike" #> SHtml.a(() => decrement(tag), Text("( - )"))
    }
  }
}