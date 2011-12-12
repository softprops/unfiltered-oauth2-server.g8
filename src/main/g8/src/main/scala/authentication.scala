package $package$

import unfiltered.filter._
import unfiltered.request._
import unfiltered.response._
import unfiltered.Cookie

/** Some extra endpoints for logging in managing authorized tokens */
object Authentication
  extends Plan with Templates {
  import java.util.UUID.randomUUID
  import AppAuth._
  def intent = {

    case Path("/") & r =>
      index("", Sessions.fromRequest(r))

    case Path("/login") & Params(p) & r =>
      Sessions.fromRequest(r) match {
        case Some(u) =>
          Redirect("/")
        case _ =>
          (p("user"), p("password")) match {
            case (Seq(username), Seq(password)) =>
              val u = User(username, Some(password))
              val sid = randomUUID.toString
              Sessions.put(sid, u)
              ResponseCookies(Cookie("sid", sid)) ~>
                ((
                  p("client_id"), p("redirect_uri"), p("response_type")
                ) match {
                  case (
                    Seq(clientId), Seq(returnUri), Seq(responseType)) =>
                      Redirect(
                        "/oauth/authorize?client_id=%s&redirect_uri=%s&response_type=%s" format(
                      clientId, returnUri, responseType
                    ))
                  case q => Redirect("/")
                })
            case _ => loginForm()
          }
      }

    case Path("/logout") =>
      ResponseCookies(Cookie("sid","")) ~> Redirect("/")

    case Path("/connections") & r =>
      Sessions.fromRequest(r) match {
        case Some(u) =>
          connections(authorizedTokens(u.id) map { t =>
            client(t.clientId) match {
              case Some(c) => (t, c)
              case _ => sys.error(
                "this token was some how associated with an unregistered client: %s" format t)
            }
          })
        case _ => Redirect("/")
      }
    
    case Path(Seg("disconnect" :: id :: Nil)) =>
      deleteToken(id)
      Redirect("/connections")

    case Path("/clients") =>
      clientList(allClients)
  }
}
