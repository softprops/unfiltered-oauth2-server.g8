import unfiltered.request._
import unfiltered.response._
import unfiltered.oauth2._

object Server {
  val resources = new java.net.URL(getClass.getResource("/web/robots.txt"), ".")
  val port = 8080

  object Auth extends AuthorizationProvider with Tokens {
    lazy val auth = new AuthorizationServer with
      Clients with Tokens with AppContainer
  }

  def main(args: Array[String]) {
    unfiltered.jetty.Http(port)
      .resources(Server.resources)
      .context("/oauth") {
        _.filter(OAuthorization(Auth.auth))
      }
      .filter(Auth.auth)
      /*.context("/api") {
        _.filter(Protection())
         .filter(new Api)
      }*/.run
  }
}
