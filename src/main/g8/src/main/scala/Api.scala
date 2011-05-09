import unfiltered.request._
import unfiltered.response._
//import unfiltered.json.response._
import net.liftweb.json.JsonDSL._
import net.liftweb.json.JsonParser._

class Api extends unfiltered.filter.Plan {
  def intent = {
    case Path(Seg("users" :: id)) => Json(("user" -> ("id" -> id) ~ ("name" -> "finnegan")))
  }
}
