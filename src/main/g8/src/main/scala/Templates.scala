package $package$

trait Templates {
  import unfiltered.response._
  import unfiltered.oauth2.{ RequestBundle, ResourceOwner, Token, Client }

  def page(body: scala.xml.NodeSeq) = Html(
    <html>
      <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
        <title>oauth provider</title>
        <link href="/css/app.css" type="text/css" rel="stylesheet" />
      </head>
      <body>
        <div id="container">
          <h1><a href="/">oauth provider</a></h1>
          {body}
        </div>
      </body>
    </html>
  )

  def index(
    urlBase: String,
    currentUser: Option[ResourceOwner]) = page(
    <div>
      {
        currentUser match {
          case Some(user) => <div>
            <p>welcome {user.id}. <a href="/logout">log out</a></p>
            <p>view your <a href="/connections">connections</a></p>
          </div>
          case _ => <p><a href="/login">log in</a>.</p>
        }
      }
      <p>These are your endpoints</p>
      <ul id="oauth-endpoints">
        <li>{urlBase}oauth/authorize</li>
        <li>{urlBase}oauth/token</li>
      </ul>
      <p><a href="/clients">view clients</a></p>
    </div>
  )

  def loginForm() = page(
    <div>
     <form action="/login" method="POST">
      <input type="text" name="user" value="user" />
      <input type="password" name="password" value="password" />
      <input type="submit" value="login"/>
     </form>
    </div>
  )

  def loginForm[T](bundle: RequestBundle[T]) = page(
    <div>
     <form action="/login" method="POST">
      <p>Sign in. A 3rd party application would like access to your data</p>
      <input type="hidden" name="response_type" value={bundle.responseTypes.mkString("+")} />
      <input type="hidden" name="client_id" value={bundle.client.id} />
      <input type="hidden" name="redirect_uri" value={bundle.redirectUri} />

      <input type="text" name="user" value="user" />
      <input type="password" name="password" value="password" />
      <input type="submit" value="login"/>
     </form>
    </div>
  )

  def authorizationForm[T](
    bundle: RequestBundle[T],
    approve: String, deny: String) = page(
    <div>
      <form action="/oauth/authorize" method="POST">
        <p>
          A 3rd party application named <strong>{ bundle.client.id }</strong> has requested access to your data.
        </p>

        <input type="hidden" name="response_type" value={ bundle.responseTypes.mkString("+") } />
        <input type="hidden" name="client_id" value={ bundle.client.id } />
        <input type="hidden" name="redirect_uri" value={ bundle.redirectUri } />

        <div id="oauth-opts">
          <input type="submit" name="submit" value={ approve } />
          <input type="submit" name="submit" value={ deny } />
        </div>
      </form>
    </div>
  )

  def clientList(clients: Seq[Client]) = page(
    <div>
      <h2>Clients</h2>
      {
        if(clients.isEmpty) <p>There are not registered clients with this service</p>
        else {
          <ul>{
            clients.map { c =>
              <li>
               { c.id } | { c.secret } | <a href={ c.redirectUri }>{ c.redirectUri }</a>
              </li>
            }
          }</ul>
        }
      }
    </div>
  )

  def connections(cs: Seq[(Token, Client)]) = page(
    <div>
      <h2>Connections</h2> {
        if(cs.isEmpty) {
          <p>You have no oauth connections</p>
        } else {
          <p>You have connections with the following applications </p>
        } ++ <ul>{
          cs.map { (_: (Token, Client)) match {
            case (t, AppClient(id, _, uri)) => 
              <li>
                <strong>{ id }</strong>
                <a href={ "/disconnect/%s" format(t.value) }>break it</a>
              </li>
            case _ => <li>?</li>
          } }
        }</ul>
      }
    </div>
  )


}
