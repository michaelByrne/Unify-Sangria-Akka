import sangria.execution.deferred.{Fetcher, HasId}
import sangria.schema._

import scala.concurrent.Future

import sangria.macros.derive._

import sangria.marshalling.playJson._
import play.api.libs.json._



object SchemaDefinition {

  val sites = Fetcher.caching(
    (ctx: SiteRepo, ids: Seq[String]) ⇒
      Future.successful(ids.flatMap(id ⇒ ctx.getSite(id) orElse None)))(HasId(_.id))


  val IdentifiableType = InterfaceType(
    "Identifiable",
    "Entity that can be identified",
    fields[Unit, Identifiable](
      Field("id", StringType, resolve = _.value.id)))

  val UserType =
    ObjectType(
      "User",
      "A Unify user.",
      fields[UserRepo, User](
        Field("id", StringType,
          Some("The id of the user."),
          resolve = _.value.id),
        Field("name", StringType,
          Some("The name of the user."),
          resolve = _.value.name),
      ))


  val SiteType =
    ObjectType(
      "Site",
      "A website credential.",
      fields[SiteRepo, Site](
        Field("id", StringType,
          Some("The id of the site."),
          resolve = _.value.id),
        Field("website", StringType,
          Some("The url of the website."),
          resolve = _.value.url),
        Field("users", ListType(UserType),
          Some("Current users of credential"),
          resolve = _.value.users),
        Field("shared_with", ListType(UserType),
          Some("Non-owner users"),
          resolve = _.value.shared_with),
        Field("shared_by", UserType,
          Some("Who loaned the credential if applicable"),
          resolve = _.value.shared_by),
      ))

  implicit val userFormat = Json.format[User]

  val UserInputType = deriveInputObjectType[User](
    InputObjectTypeName("user"),
    InputObjectTypeDescription("A unify user"),
  )

  val ID = Argument("id", StringType, description = "id of the site")
  val URL = Argument("url", StringType, description = "url of credential site")
  val USERS = Argument("users", ListInputType(UserInputType), description = "the users of the credential")
  val USER = Argument("user", UserInputType, description = "a unify user")



  val Query = ObjectType(
    "Query", fields[SiteRepo, Unit](
      Field("site", OptionType(SiteType),
        arguments = ID :: Nil,
        resolve = c ⇒ c.ctx.getSite(c arg ID)),
      Field("sites", OptionType(ListType(SiteType)),
        arguments = Nil,
        resolve = c ⇒ c.ctx.getSites(),
    )))

  val Mutation = ObjectType(
    "Mutation", fields[SiteRepo, Unit](
      Field("addSite", OptionType(SiteType),
        arguments = URL :: USERS :: Nil,
        resolve = c => c.ctx.addSite(c.arg(URL), c.arg(USERS))
      ),
      Field("addUserToCredential", OptionType(SiteType),
        arguments = ID :: USER :: Nil,
        resolve = c => c.ctx.addUserToCredential(c.arg(ID), c.arg(USER))
      ),

    )
  )

  val UnifySchema = Schema(Query, Some(Mutation))
}
