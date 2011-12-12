package $package$

object Server {
  import unfiltered.request._
  import unfiltered.response._
  import unfiltered.jetty._
  import unfiltered.filter._
  import java.net.URL

  def main(args: Array[String]) {

    Http($port$)
      .resources(new URL(getClass.getResource("/web/robots.txt"), "."))
      .context("/oauth") {
        _.filter(unfiltered.oauth2.OAuthorization(AppAuth))
      }.context("/api") {
        _.filter(Api)
      }.filter(Authentication).run
  }
}
