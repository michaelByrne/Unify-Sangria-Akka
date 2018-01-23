import org.scalatest.{Matchers, WordSpec}
import SchemaDefinition.UnifySchema
import sangria.ast.Document

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import sangria.macros._
import sangria.execution.Executor
import sangria.execution.deferred.DeferredResolver
import sangria.marshalling.sprayJson._
import spray.json._

class SchemaSpec extends WordSpec with Matchers {

}
