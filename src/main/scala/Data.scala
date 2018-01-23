import java.util.UUID
import sangria.macros.derive._



trait Identifiable {
  def id: String
}

case class Site(
                 id: String,
                 url: String,
                 users: Seq[User],
                 shared_with: Seq[User],
                 shared_by: User

               ) extends Identifiable

case class User(
                 @GraphQLDefault("0")
                 id: String,

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

  def addSite(url: String, users: Seq[User]): Option[Site] = {
    val sid = UUID.randomUUID.toString.substring(0,2) + UUID.randomUUID.toString.substring(0,3)
    sites = Site(sid, url, users, Nil, users(0)) :: sites
    Some(sites(0))
  }

  def addUserToCredential(siteId: String, user: User): Option[Site] = {
    println(user.id)
    var site = sites.filter(s => s.id == siteId).head
    if(user.id == "0") sites = sites.updated(sites.indexWhere(_.id == siteId),site.copy(shared_with = User(UUID.randomUUID.toString.substring(0,2) + UUID.randomUUID.toString.substring(0,3),user.name) :: site.shared_with.toList))
    else sites = sites.updated(sites.indexWhere(_.id == siteId),site.copy(shared_with = user :: site.shared_with.toList))
    Some(sites.filter(s => s.id == siteId).head)
  }
}

object SiteRepo {

  import UserRepo._

  var sites = List(
    Site(
      id = "0",
      url = "netflix.com",
      users = List(users(0), users(1)),
      shared_by = users(0),
      shared_with = List(users(1))
    ),
    Site(
      id = "1",
      url = "google.com",
      users = List(users(0)),
      shared_by = users(0),
      shared_with = Nil
    ),
    Site(
      id = "2",
      url = "reddit.com",
      users = List(users(0)),
      shared_by = users(0),
      shared_with = Nil
    ),
    Site(
      id = "3",
      url = "play.hbogo.com",
      users = List(users(0), users(2)),
      shared_by = users(0),
      shared_with = List(users(2))
    ),
    Site(
      id = "4",
      url = "youtube.com",
      users = List(users(3)),
      shared_by = users(3),
      shared_with = Nil
    )
    ,
    Site(
      id = "5",
      url = "hulu.com",
      users = List(users(4)),
      shared_by = users(3),
      shared_with = Nil
    )
  )
}

