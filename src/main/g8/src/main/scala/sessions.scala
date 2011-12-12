package $package$

import unfiltered.oauth2.ResourceOwner
import unfiltered.request.{ Cookies, HttpRequest }
import unfiltered.Cookie

/**
 * Sessions provides a dummy means of detecting of a resource owner
 * is authenticated with the app service
 */
object Sessions {
  private val store = new java.util.HashMap[String, ResourceOwner]

  def get(key: String): Option[ResourceOwner] =
    Option(store.get(key))

  def put(key: String, owner: ResourceOwner) =
    store.put(key, owner)

  def fromRequest[T](r: HttpRequest[T]) =
    r match {
      case Cookies(cookies) => cookies("sid") match {
        case Some(Cookie(_, value, _, _, _, _)) =>
          get(value)
        case _ =>  None
      }
    }
}
