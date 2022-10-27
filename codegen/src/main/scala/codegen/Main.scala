package codegen

import io.getquill.codegen.jdbc.model.{DefaultJdbcTyper, JdbcTypeInfo}
import io.getquill.codegen.jdbc.{
  ComposeableTraitsJdbcCodegen,
  SimpleJdbcCodegen
}
import io.getquill.codegen.model.{
  AssumeString,
  CustomNames,
  NameParser,
  PackagingStrategy,
  SnakeCaseNames,
  ThrowTypingError,
  UnrecognizedTypeStrategy
}
import io.getquill.util.LoadConfig

import java.util.UUID
import scala.reflect.{ClassTag, classTag}

object Main {
  def main(args: Array[String]): Unit = {
    val snakecaseConfig = LoadConfig("db")
//    val gen = new SimpleJdbcCodegen(snakecaseConfig, "example") {
//      override def nameParser = SnakeCaseNames
//    }
    val gen = new ComposeableTraitsJdbcCodegen(
      snakecaseConfig,
      packagePrefix = "example",
      nestedTrait = true
    ) {
      override def nameParser = SnakeCaseNames
      override def unrecognizedTypeStrategy: UnrecognizedTypeStrategy = {
        ThrowTypingError
      }

      override def typer =
        new DefaultJdbcTyper(unrecognizedTypeStrategy, numericPreference) {
          override def apply(jdbcTypeInfo: JdbcTypeInfo) = {
            jdbcTypeInfo.typeName match {
              case Some("uuid") => Some(classTag[UUID])
              case _            => super.apply(jdbcTypeInfo)
            }
          }
        }
      override def packagingStrategy: PackagingStrategy =
        PackagingStrategy.ByPackageHeader.TablePerSchema(packagePrefix)
    }
    gen.writeFiles("src/main/scala/example")
  }
}
