package nl.astraeus.markdown.parser

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

  @Test
  fun testIndentedCode() {
    val input = """
      Dit is een text
      
        Code block
        Code block
        
      Meer text
      """.trimIndent()

    val md = markdown(input)

    printMarkdownParts(md)
  }

  @Test
  fun testUnorderedList() {
    val input = """
      Dit is een text
      
      - First
        More text
      - Second
        More text
      
      Another paragraph
      """.trimIndent()

    val md = markdown(input)

    printMarkdownParts(md)
  }

  @Test
  fun testUnorderedListAlternative() {
    val input = """
      Dit is een text
      
      * First
        More text
      * Second
        More text
      
      Another paragraph
      """.trimIndent()

    val md = markdown(input)

    printMarkdownParts(md)
  }

  @Test
  fun testOrderedList() {
    val input = """
      Dit is een text
      
      -. First
        More text
      -. Second
        More text
      
      Another paragraph
      """.trimIndent()

    val md = markdown(input)

    printMarkdownParts(md)
  }

  @Test
  fun testOrderedListAlternative() {
    val input = """
      Dit is een text
      
      #.First
        More text
      #.Second
        More text
      
      Another paragraph
      """.trimIndent()

    val md = markdown(input)

    printMarkdownParts(md)
  }

  @Test
  fun testCheckboxList() {
    val input = """
      Dit is een text
      
      - [ ] Not checked,
            with some more text here
      - [x] Checked!      
      - [x] Checked,
            text it!
      
      Meer text
      """.trimIndent()

    val md = markdown(input)

    printMarkdownParts(md)
  }

  @Test
  fun testCheckboxList2() {
    val input = """
          # Todo
        
          - [ ] Handle multi line lists correctly;
                as long as there is no empty line the text is appended to last item in the list
          - [x] Autolinks, text between < >  will be parsed as a generic link. It's up
                to the using application what to do with it.""".trimIndent()

    val md = markdown(input)

    printMarkdownParts(md)
  }


  @Test
  fun testCheckboxListError() {
    val input = """
      Dit is een text
      
      - [ ] Not checked
      - [x] Checked""".trimIndent()

    val md = markdown(input)

    printMarkdownParts(md)
  }

  @Test
  fun testHeading() {
    val input = "# Markdown\n\nMy **markdown** text."

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
