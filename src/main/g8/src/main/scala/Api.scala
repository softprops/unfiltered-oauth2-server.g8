import unfiltered.request._
import unfiltered.filter.Plan
import unfiltered.filter.request.ContextPath
import unfiltered.response._
import net.liftweb.json.JsonDSL._
import net.liftweb.json.JsonParser._

object Api extends Plan {
  def intent = {
    case ContextPath(_, Seg("users" :: id)) =>
      Json(("user" -> ("id" -> id) ~ ("name" -> "finnegan")))
  }
}
