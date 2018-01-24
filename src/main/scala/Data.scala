import java.util.UUID
import sangria.macros.derive._


trait Identifiable {
  def id: String
}

case class Site(
                 id: String,
                 url: String,
                 shared_with: Seq[User],
                 shared_by: User,
                 is_owner: Boolean,
                 password_id: String

               ) extends Identifiable

case class User(
                 @GraphQLField
                 @GraphQLDefault("0")
                 id: String,

                 @GraphQLField
                 name: String) extends Identifiable


class UserRepo {

  import UserRepo._

  def getUser(id: String): Option[User] = users.find(u ⇒ u.id == id)
}

object UserRepo {
  val users = List(
    User(
      id = "0",
      name = "johndoe19"),
    User(
      id = "1",
      name = "thefriendofjohndoe"),
    User(
      id = "2",
      name = "gotfan"),
    User(
      id = "3",
      name = "macklemore"),
    User(
      id = "4",
      name = "lorenzochello"))
}

class SiteRepo {

  import SiteRepo._

  def getIndentifiable(indentifiableId: Option[String]) =
    indentifiableId flatMap (_ ⇒ getSite("0")) getOrElse None

  def getSite(id: String): Option[Site] = sites.find(s ⇒ s.id == id)

  def getSites(): Option[List[Site]] = Some(sites)

  def addSite(url: String, users: Seq[User], password_id: String): Option[Site] = {
    val sid = UUID.randomUUID.toString.substring(0, 2) + UUID.randomUUID.toString.substring(0, 3)
    sites = Site(sid, url, users, users(0), true, password_id) :: sites

    Some(sites(0))
  }

  def addUserToCredential(siteId: String, user: User): Option[Site] = {
    var site = sites.filter(s => s.id == siteId).head
    if (user.id == "0") sites = sites.updated(sites.indexWhere(_.id == siteId), site.copy(shared_with = User(UUID.randomUUID.toString.substring(0, 2) + UUID.randomUUID.toString.substring(0, 3), user.name) :: site.shared_with.toList))
    else sites = sites.updated(sites.indexWhere(_.id == siteId), site.copy(shared_with = user :: site.shared_with.toList))

    Some(sites.filter(s => s.id == siteId).head)
  }

  def revokeSharing(siteId: String, name: String): Option[Site] = {
    var site = sites.filter(s => s.id == siteId).head
    sites = sites.updated(sites.indexWhere(_.id == siteId), site.copy(shared_with = site.shared_with.filter(s => s.name != name)))

    Some(sites.filter(s => s.id == siteId).head)
  }
}

object SiteRepo {

  import UserRepo._

  var sites = List(
    Site(
      id = "0",
      url = "netflix.com",
      shared_by = users(0),
      shared_with = List(users(1)),
      is_owner = true,
      password_id = "(*#$2k$SSD"
    ),
    Site(
      id = "1",
      url = "google.com",
      shared_by = users(0),
      shared_with = Nil,
      is_owner = true,
      password_id = "#jknkjndsjk$SSD"
    ),
    Site(
      id = "2",
      url = "reddit.com",
      shared_by = users(0),
      shared_with = Nil,
      is_owner = true,
      password_id = "#)_2-==23D"
    ),
    Site(
      id = "3",
      url = "play.hbogo.com",
      shared_by = users(0),
      shared_with = List(users(2)),
      is_owner = true,
      password_id = "23789$$SSD"
    ),
    Site(
      id = "4",
      url = "youtube.com",
      shared_by = users(3),
      shared_with = Nil,
      is_owner = false,
      password_id = "(*#$2k$SSD"
    )
    ,
    Site(
      id = "5",
      url = "hulu.com",
      shared_by = users(3),
      shared_with = Nil,
      is_owner = false,
      password_id = "23789$$SSD"
    ),
    Site(
      id = "6",
      url = "wellsfargo.com",
      shared_by = users(0),
      shared_with = Nil,
      is_owner = true,
      password_id = "iuh!@@22"
    ),
    Site(
      id = "7",
      url = "facebook.com",
      shared_by = users(0),
      shared_with = Nil,
      is_owner = true,
      password_id = "nnjnnnc#D"
    ),

  )
}

