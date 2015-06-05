import shapeless._
import record._, syntax.singleton._, ops.record._

object Main extends App {

  // The fields that might change (Some) or not (None)
  case class Delta(
        name: Option[String],
     twitter: Option[String]
  )

  val deltaGen = LabelledGeneric[Delta]
  val keys = Keys[deltaGen.Repr].apply

  // Update record `id` with just the fields that need changing
  // You could include `id` in `Delta` as long it's not `Option` -- it'll be ignored by the `collect`
  def update(id: Long, delta: Delta) = {
    val record = deltaGen.to(delta)
    val pairs = record.values zip keys

    // Emit SQL clauses....
    pairs.toList.collect { case (Some(value), column) =>
      // Don't do this literally: Maybe used StaticQuery.u with +? etc
      s"SET $column = $value"
    }
  }

  println(
    update(42, Delta(None, Some("@bob")) )
  )
}