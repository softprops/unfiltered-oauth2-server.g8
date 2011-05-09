import unfiltered.oauth2.{Client, ResourceOwner, Token, TokenStore}

case class AppToken(val value: String, val clientId: String,
                    val redirectUri: String, val owner: String)
     extends Token {
       def refresh = Some("refreshToken")
       def expiresIn = Some(3600)
       def scopes = None
       def tokenType = "tokenType"
     }


trait Tokens extends TokenStore {
  import scala.collection.JavaConversions._
  import java.util.UUID.randomUUID
  private val accessTokens = new java.util.HashMap[String, AppToken]
  private val codeTokens = new java.util.HashMap[String, AppToken]

  /** here you would normally be generating a new token to
   *  replace an existing access token */
  def refresh(other: Token) = AppToken(
    other.value, other.clientId, other.redirectUri, other.owner
  )

  /** may want to rename to this authorizationCode */
  def token(code: String) = codeTokens.get(code) match {
    case null => None
    case t => Some(t)
  }

  def refreshToken(refreshToken: String) =
    accessTokens.values().filter(_.refresh.get==refreshToken).headOption

  def accessToken(value: String) = accessTokens.get(value)

  def generateAccessToken(other: Token) = {
    codeTokens.remove(other.value)
    val at = AppToken(randomUUID.toString, other.clientId, other.redirectUri, other.owner)
    accessTokens.put(at.value, at)
    at
  }

  def generateCodeToken(owner: ResourceOwner, client: Client,
                        scope: Option[String], redirectURI: String) = {
    val ct = AppToken(randomUUID.toString, client.id, redirectURI, owner.id)
    codeTokens.put(ct.value, ct)
    ct.value
  }

  /** these tokens are not associated with a resource owner */
  def generateClientToken(client: Client, scope: Option[String]) = {
    val at = AppToken(randomUUID.toString, client.id, client.redirectUri, client.id)
    accessTokens.put(at.value, at)
    at
  }

  def generateImplicitAccessToken(owner: ResourceOwner, client: Client,
                                  scope: Option[String], redirectURI: String) = {
    val at = AppToken(randomUUID.toString, client.id, redirectURI, owner.id)
    accessTokens.put(at.value, at)
    at
  }
}
