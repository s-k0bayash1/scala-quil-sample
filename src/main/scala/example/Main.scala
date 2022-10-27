package example

import example.custom.ItemDetail
import example.hogeschema.{Item, Person}
import io.getquill.{PostgresJdbcContext, SnakeCase}

import java.util.UUID

object Main {
  def main(args: Array[String]): Unit = {
    val ctx = new PostgresJdbcContext(SnakeCase, "db")
    new QuillRunner(ctx).start()
  }
}

class QuillRunner(ctx: PostgresJdbcContext[SnakeCase]) {
  def start() = {
    import ctx._
    implicit val encodeSeqString =
      MappedEncoding[Seq[String], java.sql.Array](x =>
        ctx.dataSource.getConnection.createArrayOf("_varchar", x.toArray)
      )
    implicit val decodeSeqString =
      MappedEncoding[java.sql.Array, Seq[String]](
        _.getArray().asInstanceOf[Array[String]].toSeq
      )
    val id1 = UUID.randomUUID()
    val result = for {
      _ <- runIO(
        quote {
          query[Person].delete
        }
      )
      _ <- runIO(
        quote {
          liftQuery(
            List(
              Person(id1, "test1", 20, None),
              Person(UUID.randomUUID(), "test2", 30, Some("スキー"))
            )
          ).foreach(p => query[Person].insertValue(p))
        }
      )
      _ <- runIO(
        quote {
          liftQuery(
            List(
              Item(1, Some(id1), "test1", "something1"),
              Item(2, Some(id1), "test2", "something2")
            )
          ).foreach(i => query[Item].insertValue(i))
        }
      )
      _ <- runIO(
        quote {
          query[Item]
            .insertValue(Item(1, lift(Option(id1)), "test1", "something2"))
            .onConflictUpdate(_.id)((before, after) =>
              before.category -> after.category
            )
        }
      )
      _ <- runIO(
        quote {
          liftQuery(
            List(
              ItemDetail("aaa", None, 1, Some(Seq("bbb")))
            )
          ).foreach(i => query[ItemDetail].insertValue(i))
        }
      )
      result <- runIO(
        quote {
          query[Person]
            .leftJoin(query[Item])
            .on { (p, i) =>
              i.personId.contains(p.id)
            }
            .leftJoin(query[ItemDetail])
            .on { case ((_, i), itemDetail) =>
              i.exists(_.id == itemDetail.itemId)
            }
        }
      )
    } yield result

    performIO(result).map { case ((person, item), itemDetail) =>
      println(person)
      println(item)
      println(itemDetail)
    }
  }
}
