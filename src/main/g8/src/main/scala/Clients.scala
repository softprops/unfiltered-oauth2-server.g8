package $package$

import unfiltered.oauth2.{Client, ClientStore}

case class AppClient(id: String, secret: String, redirectUri: String) extends Client

/*
 * A dummy client store with a default registered oauth client
 */
trait Clients extends ClientStore {

  private val clients: Map[String, Client] =
    Map(
      "$client_id$" -> AppClient(
        "$client_id$", "$client_secret$", "$client_redirect_uri$")
    )
  
  def client(clientId: String, secret: Option[String] = None) =
    clients.get(clientId) match {
      case None => None
      case Some(c) =>
        secret match {
          case Some(sec) =>
            if(sec.equals(c.secret)) Some(c)
            else None
          case _ => Some(c)
        }
    }

  def allClients = clients.values.toSeq
}
