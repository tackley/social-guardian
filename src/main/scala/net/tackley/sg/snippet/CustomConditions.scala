package net.tackley.sg.snippet

import xml.NodeSeq
import net.liftweb.http.{S, DispatchSnippet}

/**
 * Created by IntelliJ IDEA.
 * User: mbs
 * Date: 16/12/2010
 * Time: 16:34
 * To change this template use File | Settings | File Templates.
 */

class CustomConditions  extends DispatchSnippet {
  def dispatch : DispatchIt = {
    case "isLocal" |
    "local" |
    "islocal" | "is_local" => isLocal _
  }

  def isLocal(xhtml: NodeSeq): NodeSeq =
  if (S.hostName == "localhost") xhtml else NodeSeq.Empty
}