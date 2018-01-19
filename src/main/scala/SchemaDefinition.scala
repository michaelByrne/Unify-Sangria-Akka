import sangria.execution.deferred.{Fetcher, HasId}
import sangria.schema._

import scala.concurrent.Future


object SchemaDefinition {

  val users = Fetcher.caching(
    (ctx: UserRepo, ids: Seq[String]) ⇒
      Future.successful(ids.flatMap(id ⇒ ctx.getUnifyUser(id) orElse None)))(HasId(_.id))

  val CredentialEnum = EnumType(
    "Credential",
    Some("A credential managed by UnifyID"),
    List(
      EnumValue("YOUTUBE",
        value = Credential.YOUTUBE,
        description = Some("Personal YouTube account")),
      EnumValue("HBO",
        value = Credential.HBO,
        description = Some("HBO Go account")),
      EnumValue("NETFLIX",
        value = Credential.NETFLIX,
        description = Some("Netflix streaming account"))))

  val User: InterfaceType[UserRepo, User] =
    InterfaceType(
      "User",
      "A UnifyID user",
      () ⇒ fields[UserRepo, User](
        Field("id", StringType,
          Some("The id of the user."),
          resolve = _.value.id),
        Field("name", StringType,
          Some("The name of the user."),
          resolve = _.value.name),
        Field("appearsIn", OptionType(ListType(OptionType(CredentialEnum))),
          Some("Which credentials a user is named in"),
          resolve = _.value.appearsIn map (e ⇒ Some(e)))
      ))

  val UnifyUser =
    ObjectType(
      "UnifyUser",
      "A Unify user.",
      interfaces[UserRepo, UnifyUser](User),
      fields[UserRepo, UnifyUser](
        Field("id", StringType,
          Some("The id of the user."),
          resolve = _.value.id),
        Field("name", StringType,
          Some("The name of the user."),
          resolve = _.value.name),
        Field("appearsIn", OptionType(ListType(OptionType(CredentialEnum))),
          Some("Which credentials they appear in."),
          resolve = _.value.appearsIn map (e ⇒ Some(e))),
      ))

  val ID = Argument("id", StringType, description = "id of the user")

  val CredentialArg = Argument("credential", StringType,
    description = "Nothing so far")

  val Query = ObjectType(
    "Query", fields[UserRepo, Unit](
      Field("unify_user", OptionType(UnifyUser),
        arguments = ID :: Nil,
        resolve = ctx ⇒ ctx.ctx.getUnifyUser(ctx arg ID)),
    ))

  val UnifySchema = Schema(Query)
}
