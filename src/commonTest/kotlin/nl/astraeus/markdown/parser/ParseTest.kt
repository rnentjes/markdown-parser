package nl.astraeus.markdown.parser

import nl.astraeus.wiki.parser.MarkdownPart
import nl.astraeus.wiki.parser.markdown
import kotlin.test.Test

class ParseTest {

  @Test
  fun testParagraph() {
    val input = """
      Dit is een **test**, laat ***mij*** maar eens zien!
      
      link: [NOS](www.nos.nl "Nos title") of zo.


      - link: [NU](www.nu.nl "Nu site") of zo.
      
    """.trimIndent()


    val md = markdown(input)

    printMarkdownParts(md)
  }

  @Test
  fun testImage() {
    val input = """
      [![test2](https://upload.wikimedia.org/wikipedia/commons.png)](https://upload.wikimedia.org/wikipedia/commons.png)

    """.trimIndent()

    val md = markdown(input)

    printMarkdownParts(md)
  }

  private fun printMarkdownParts(md: List<MarkdownPart>) {
    for (part in md) {
      if (part is MarkdownPart.Paragraph) {
        for (para in part.parts) {
          println("PARA: ${para::class.simpleName} - ${para.toString().take(75)}")
        }
      } else {
        println("PART: ${part::class.simpleName} - ${part.toString().take(75)}")
      }
    }
  }
}
