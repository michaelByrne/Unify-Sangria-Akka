object Credential extends Enumeration {
  val YOUTUBE, HBO, NETFLIX = Value
}

trait User{
  def id: String
  def name: String
  def appearsIn: List[Credential.Value]
}

case class UnifyUser(id: String,
                name: String,
                appearsIn: List[Credential.Value]) extends User


class UserRepo {

  import UserRepo._

  def getUser(credential: Option[Credential.Value]) =
    credential flatMap (_ ⇒ getUnifyUser("1000")) getOrElse None

  def getUnifyUser(id: String): Option[UnifyUser] = users.find(c ⇒ c.id == id)
}

object UserRepo {
  val users = List(
    UnifyUser(
      id = "1000",
      name = "Michael Byrne",
      appearsIn = List(Credential.YOUTUBE, Credential.NETFLIX)),
    UnifyUser(
      id = "1001",
      name = "Bill the Cat",
      appearsIn = List(Credential.HBO)),
  )
}
