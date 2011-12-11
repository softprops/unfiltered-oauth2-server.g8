import unfiltered.oauth2.{ Client, ResourceOwner, Token, TokenStore }

case class AppToken(
  val value: String, val clientId: String,
  val redirectUri: String, val owner: String)
     extends unfiltered.oauth2.Token {
       def refresh = Some("refreshToken")
       def expiresIn = Some(3600)
       def scopes = Nil
       def tokenType = None
     }

/**
  * This tokens mixing provides the oauth2 module with access to a services
  * storage for token
  */
trait Tokens extends TokenStore {
  import java.util.HashMap
  import scala.collection.JavaConversions._
  import java.util.UUID.randomUUID
  private val accessTokens = new HashMap[String, AppToken]
  private val codeTokens = new HashMap[String, AppToken]

  /** here you would normally be generating a new token to
   *  replace an existing access token */
  def refresh(other: Token) = AppToken(
    other.value, other.clientId,
    other.redirectUri, other.owner
  )

  def token(code: String) =
    codeTokens.get(code) match {
      case null => None
      case t => Some(t)
    }

  def refreshToken(refreshToken: String) =
    accessTokens.values().filter(_.refresh.get==refreshToken).headOption

  def accessToken(value: String) = accessTokens.get(value)

  // auth code flow

  def generateAuthorizationCode(
    responseTypes: Seq[String],
    owner: ResourceOwner, client: Client,
    scope: Seq[String], redirectUri: String) = {
    val ct = AppToken(
      randomUUID.toString, client.id,
      redirectUri, owner.id
    )
    codeTokens.put(ct.value, ct)
    // was eugene's suggestions we only return the token value
    ct.value 
  }

  def exchangeAuthorizationCode(codeToken: Token) = {
    codeTokens.remove(codeToken.value)
    val at = AppToken(
      randomUUID.toString, codeToken.clientId,
      codeToken.redirectUri, codeToken.owner
    )
    accessTokens.put(at.value, at)
    at
  }

  // implicit flow

  def generateImplicitAccessToken(
    responseTypes: Seq[String],
    owner: ResourceOwner, client: Client,
    scope: Seq[String], redirectURI: String) = {
    val at = AppToken(
      randomUUID.toString, client.id,
      redirectURI, owner.id
    )
    accessTokens.put(at.value, at)
    at
  }

  // client credentials flow

  def generateClientToken(client: Client, scope: Seq[String]) = {
    val at = AppToken(
      randomUUID.toString, client.id,
      client.redirectUri, client.id
    )
    accessTokens.put(at.value, at)
    at
  }

  // password flow

  def generatePasswordToken(
    owner: ResourceOwner, client: Client, scope: Seq[String]) = {
    val at = AppToken(
      randomUUID.toString, client.id,
      client.redirectUri, owner.id
    )
    accessTokens.put(at.value, at)
    at
  }

  // extras

  /** @return all tokens associated with a resource owner */
  def authorizedTokens(ownerId: String): Seq[Token] =
    accessTokens.filter(_ match {
      case (k, v) if(v.owner == ownerId) => true
      case _ => false
    }).values.toSeq

  def deleteToken(key: String) = 
    accessTokens.remove(key)
}
