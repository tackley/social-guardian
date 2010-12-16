package net.tackley.sg.lib

import com.gu.openplatform.contentapi.model.ItemResponse
import java.io.FileNotFoundException
import net.liftweb.http.{NotFoundResponse, ResponseShortcutException, S, RequestVar}
import com.gu.openplatform.contentapi.Api

object Current {
     object item extends RequestVar[ItemResponse] (loadItem)

  def loadItem = {
     val path = S.param("path").openOr("/")
      try {
        Api.item.itemId(path).showFields("all").showEditorsPicks().showTags("all").response
      } catch {
        case e: FileNotFoundException => throw new ResponseShortcutException(NotFoundResponse("Item not found in API"))
      }
    }
}