import scala.util.Random

trait Converter[-S] {
  def convert(value: S): String
}

trait Slide[+R] {
  def read: (Option[R], Option[Slide[R]])
}

// OOP15-UE: slide projector
class Projector[R](converter: Converter[R]) {
  def project(screen: Slide[R]): String = screen.read match {
    case (None, None) => ""
    case (tokenOpt, nextOpt) => tokenOpt.fold("")(converter.convert) + nextOpt.fold("")(project)
  }
}

class WordLine(val word: String)
class RedactedWordLine(val redactionFactor: Double, word: String) extends WordLine(word)

object LineConverter extends Converter[WordLine] {
  override def convert(value: WordLine): String = value.word + "\n"
}

object RedactedWordLineConverter extends Converter[RedactedWordLine] {
  override def convert(value: RedactedWordLine): String = {
    if (Random.nextDouble() < value.redactionFactor) "█" * value.word.length + "\n"
    else value.word + "\n"
  }
}

class HelloSlide[R <: WordLine](lines: Seq[R]) extends Slide[R] {
  override def read: (Option[R], Option[Slide[R]]) = {
    (lines.headOption, lines.drop(1).length match {
      case 0 => None
      case _ => Option(new HelloSlide[R](lines.drop(1)))
    })
  }
}

object solutionFour extends App {

  val slideRedacted: HelloSlide[RedactedWordLine] = new HelloSlide[RedactedWordLine](
    Seq(
      new RedactedWordLine(0.5, "Hello, World!"),
      new RedactedWordLine(0.25, "Scala is cool!"),
      new RedactedWordLine(1, "I'm from Russia!")
    )
  )

  val slideSimple: HelloSlide[WordLine] = new HelloSlide[WordLine](
    Seq(
      new WordLine("Hello, World!"),
      new WordLine("Scala is cool!")
    )
  )

  /*
    3. В проекторе для RedactedWordLine можно проецировать Slide[RedactedWordLine], но нельзя Slide[WordLine]
    RWL - RedactedWordLine
    WL - WordLine
   */
   val projectorRWL = new Projector[RedactedWordLine](RedactedWordLineConverter)
  println(projectorRWL.project(slideRedacted))
  /*  don't compile: projectorRWL.project(slideSimple)  */


  /*
    4. В проекторе для WordLine можно проецировать Slide[WordLine] и Slide[RedactedWordLine]
   */
   val projectorWL = new Projector[WordLine](LineConverter)
//   println(projectorWL.project(slideSimple))
//  println(projectorWL.project(slideRedacted))

  /*
    5. В проекторе для RedactedWordLine можно использовать Converter[RedactedWordLine] и Converter[WordLine]
   */
  val projectorRWLfromRWL = new Projector[RedactedWordLine](RedactedWordLineConverter)
  val projectorRWLfromWL = new Projector[RedactedWordLine](LineConverter)

  /*
    6. В проекторе для WordLine можно использовать Converter[WordLine], но нельзя Converter[RedactedWordLine]
   */
   val projectorWLfromWL = new Projector[WordLine](LineConverter)
   /* don't compile: val projectorWLfromRWL = new Projector[WordLine](RedactedWordLineConverter)  */
}
