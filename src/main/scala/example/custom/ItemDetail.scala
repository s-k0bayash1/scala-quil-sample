package example.custom

case class ItemDetail(
    name: String,
    description: Option[String],
    itemId: Int,
    strArray: Option[Seq[String]]
)
