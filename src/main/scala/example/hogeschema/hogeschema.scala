package example.hogeschema

case class Item(id: Int, personId: Option[java.util.UUID], name: String, category: String)

case class Person(id: java.util.UUID, name: String, age: Int, hobby: Option[String])

case class ItemDetail(name: String, description: Option[String], itemId: Int, strArray: Option[java.sql.Array])

