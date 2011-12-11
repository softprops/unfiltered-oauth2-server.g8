object Server {
  import unfiltered.request._
  import unfiltered.response._
  import unfiltered.oauth2._
  import unfiltered.jetty._
  import unfiltered.filter._
  import java.net.URL

  object Auth extends AuthorizationProvider {
    lazy val auth = new AuthorizationServer with
      Clients with Tokens with AppServices
  }

  def main(args: Array[String]) {
    Http($port$)
      .resources(new URL(getClass.getResource("/web/robots.txt"), "."))
      .context("/oauth") {
        _.filter(OAuthorization(Auth.auth))
      }.context("/api") {
        _.filter(Api)
      }.filter(Authentication).run
  }
}
