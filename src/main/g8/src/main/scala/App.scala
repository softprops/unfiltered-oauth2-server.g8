import unfiltered.request._
import unfiltered.response._
import unfiltered.Cookie

import unfiltered.oauth2.{Client, ResourceOwner, RequestBundle}

trait AppContainer extends unfiltered.filter.Plan with unfiltered.oauth2.Container with Templates {
  import scala.collection.JavaConversions._
  import unfiltered.request.{HttpRequest => Req}

  val sessions = new java.util.HashMap[String, User]

  val ApproveKey = "Approve"
  val DenyKey = "Deny"

  def login[T](bundle: RequestBundle[T]) = loginForm(bundle)

  def requestAuthorization[T](bundle: RequestBundle[T]) = authorizationForm(bundle, ApproveKey, DenyKey)

  def invalidRedirectUri(uri: Option[String], client: Option[Client]) =
    ResponseString("missing or invalid redirect_uri")

  def resourceOwner[T](r: Req[T]): Option[User] = r match {
    case Cookies(cookies) => cookies("sid") match {
      case Some(Cookie(_, value, _, _, _, _)) => sessions.get(value) match {
        case null => None
        case u => Some(u)
      }
      case _ =>  None
    }
  }

  def accepted[T](r: Req[T]) = r match {
    case Params(p) => p("submit") match {
      case Seq(ApproveKey) => true
      case _ => false
    }
  }

  def denied[T](r: Req[T]) = r match {
    case Params(p) => p("submit") match {
      case Seq(DenyKey) => true
      case _ => false
    }
  }

  /**  would normally validate that the scopes are valid for the owner here */
  def validScopes[T](owner: ResourceOwner, scopes: Option[String], req: Req[T]) =
    true

  def intent = {
    case Path("/") & r => index("", resourceOwner(r))

    case Path("/login") & Params(p) & r =>
      resourceOwner(r) match {
        case Some(u) =>
          Redirect("/")
        case _ =>
          (p("user"), p("password")) match {
            case (Seq(username), Seq(password)) =>
              val u = User(username, password)
              val sid = java.util.UUID.randomUUID.toString
              sessions.put(sid, u)
              ResponseCookies(Cookie("sid", sid)) ~> ((p("client_id"), p("redirect_uri"), p("response_type")) match {
                case (Seq(clientId), Seq(returnUri), Seq(responseType)) =>
                  Redirect("/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=%s" format(
                    clientId, returnUri, responseType
                  ))
                case q => Redirect("/")
              })
            case _ => loginForm()
          }
      }

    case Path("/logout") => ResponseCookies(Cookie("sid","")) ~> Redirect("/")
  }
}
