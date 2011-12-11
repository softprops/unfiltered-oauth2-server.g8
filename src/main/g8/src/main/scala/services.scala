import unfiltered.request._
import unfiltered.response._
import unfiltered.Cookie
import unfiltered.filter._

import unfiltered.oauth2.{ Client, ResourceOwner, RequestBundle, Service }

/**
 * App services provides the oauth2 module
 * with the hooks it needs to complete the oauth protocol
 */
trait AppServices extends Service with Templates {

  val ApproveKey = "Approve"
  val DenyKey = "Deny"

  // if you wish to support docs for errors
  def errorUri(errKey: String) = None

  // show a login form
  def login[T](bundle: RequestBundle[T]) = loginForm(bundle)

  // prompt the authenticated user for authorization
  def requestAuthorization[T](bundle: RequestBundle[T]) =
    authorizationForm(bundle, ApproveKey, DenyKey)

  // the client's uri could not be trusted to redirect, let the user know
  def invalidRedirectUri(uri: Option[String], client: Option[Client]) =
    ResponseString("missing or invalid redirect_uri")

  // given a request, extract the currently logged in user
  def resourceOwner[T](r: HttpRequest[T]) = Sessions.fromRequest(r)

  // for password-based requests authenticate using just 
  // the users credentials, here we are just returning who ever
  // is logged in, in a real application. actually do this
  def resourceOwner(username: String, password: String) = None

  // given the request of a form submission, return
  // true if there is indication of the user's approval
  def accepted[T](r: HttpRequest[T]) = r match {
    case Params(p) => p("submit") match {
      case Seq(ApproveKey) => true
      case _ => false
    }
  }

  // given the request of a form submission, return
  // true if there is indication of the user's denial
  def denied[T](r: HttpRequest[T]) = r match {
    case Params(p) => p("submit") match {
      case Seq(DenyKey) => true
      case _ => false
    }
  }

  // validate that these are all valid scopes
  // supported by the service
  def validScopes(scopes: Seq[String]) = true

  //would normally validate that the scopes are valid for the owner here
  def validScopes[T](
    owner: ResourceOwner, scopes: Seq[String],
    req: HttpRequest[T]) = true

  def invalidClient = ResponseString("invalid client")
}
