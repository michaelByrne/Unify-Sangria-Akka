import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import sangria.execution.deferred.DeferredResolver
import sangria.parser.QueryParser
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.marshalling.sprayJson._
import spray.json._
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import akka.http.scaladsl.model.headers.{HttpOrigin, HttpOriginRange}

import ch.megard.akka.http.cors.scaladsl.CorsDirectives._


import scala.util.{Failure, Success}

object Server extends App {
  implicit val system = ActorSystem("sangria-server")
  implicit val materializer = ActorMaterializer()

  import system.dispatcher

  val corsSettings = CorsSettings.defaultSettings.copy(
    allowedOrigins = HttpOriginRange.*
  )

  val route: Route =
    cors(corsSettings) {
      (post & path("graphql")) {


        // Your CORS settings


        entity(as[JsValue]) { requestJson ⇒
          val JsObject(fields) = requestJson

          val JsString(query) = fields("query")

          val operation = fields.get("operationName") collect {
            case JsString(op) ⇒ op
          }

          //println(operation)

          val vars = fields.get("variables") match {
            case Some(obj: JsObject) ⇒ obj
            case _ ⇒ JsObject.empty
          }

          //println(vars)

          QueryParser.parse(query) match {

            // query parsed successfully, time to execute it!
            case Success(queryAst) ⇒
              complete(Executor.execute(SchemaDefinition.UnifySchema, queryAst, new SiteRepo,
                variables = vars,
                operationName = operation,
                deferredResolver = DeferredResolver.fetchers(SchemaDefinition.sites))
                .map(OK → _)
                .recover {
                  case error: QueryAnalysisError ⇒ BadRequest → error.resolveError
                  case error: ErrorWithResolver ⇒ InternalServerError → error.resolveError
                })

            // can't parse GraphQL query, return error
            case Failure(error) ⇒
              complete(BadRequest, JsObject("error" → JsString(error.getMessage)))
          }
        }
      }
    } ~
      get {
        getFromResource("graphiql.html")
      }

  Http().bindAndHandle(route, "0.0.0.0", sys.props.get("http.port").fold(8080)(_.toInt))
}
