package net.tackley.sg.snippet

import com.gu.openplatform.contentapi.Api
import com.gu.openplatform.contentapi.model.Content
import net.tackley.sg.lib.Current
import net.tackley.sg.model.User
import net.liftweb.util.Helpers._
import xml.{Unparsed}

class Recommendations  {

  lazy val latestContent: List[Content] =  Api.search.pageSize(50).showTags("keyword").response.results
  def myTags: Map[String, Int] = User.current.is.map(_.interestingTags.get) getOrElse Map()
  lazy val myUris: List[String] = User.current.is.map(_.history.get) getOrElse Nil


  def scoredContent = latestContent
    .filterNot(c => myUris.contains("/"+c.id))
    .map(content => content -> scoreContent(content))
    .sortBy { case (content, score) => score }.reverse.take(5)

  def scoreContent (content : Content) = {
    if (content.tags.isEmpty) 0
    else if(content.tags.length == 1) 0
    else {
      val tagScores = for (tag <- content.tags) yield myTags.getOrElse(tag.id, 0)
      tagScores.sum / content.tags.size
    }
  }

  def render = "li *" #> scoredContent.map { case (content, score) =>
    ".link-text" #> <a title={"score: "+score} href={"/" + content.id}>{Unparsed(content.webTitle)}</a>
  }



}