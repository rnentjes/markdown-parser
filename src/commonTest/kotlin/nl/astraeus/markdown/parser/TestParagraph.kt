package nl.astraeus.markdown.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestParagraph {

  @Test
  fun testBold() {
    val input = "Text **bold** Text"

    val result = parseParagraph(input)

    assertEquals(3, result.parts.size)

    assertTrue(result.parts[0] is MarkdownPart.ParagraphPart.Text)
    assertTrue(result.parts[1] is MarkdownPart.ParagraphPart.Bold)
    assertTrue(result.parts[2] is MarkdownPart.ParagraphPart.Text)
  }

  @Test
  fun testCode() {
    val input = "Text `code` Text"

    val result = parseParagraph(input)

    assertEquals(3, result.parts.size)

    assertTrue(result.parts[0] is MarkdownPart.ParagraphPart.Text)
    assertTrue(result.parts[1] is MarkdownPart.ParagraphPart.InlineCode)
    assertTrue(result.parts[2] is MarkdownPart.ParagraphPart.Text)
  }

  @Test
  fun testIndentedCode() {
    val input = "Text \n  code\n  line 2\n\nText"

    val result = parseParagraph(input)

    assertEquals(3, result.parts.size)

    assertTrue(result.parts[0] is MarkdownPart.ParagraphPart.Text)
    assertTrue(result.parts[1] is MarkdownPart.ParagraphPart.IndentedCode)
    assertTrue(result.parts[2] is MarkdownPart.ParagraphPart.Text)
  }

  @Test
  fun testLink() {
    val input = "[Samsung G8 G80SD](Samsung Odyssey G8 G80SD - OLED-monitor)"

    val result = parseParagraph(input)

    assertEquals(1, result.parts.size)

    assertTrue(result.parts[0] is MarkdownPart.ParagraphPart.Link)
  }

  @Test
  fun testEscaped() {
    val input = "Test \\`escaped\\` text"

    val result = parseParagraph(input)

    assertEquals(1, result.parts.size)

    assertTrue(result.parts[0] is MarkdownPart.ParagraphPart.Text)
  }


  @Test
  fun testFormatting() {
    val input = "test **bold**, *italic*, ***bold and italic***, ~~strikethrough~~, and `code`."

    val result = parseParagraph(input)

    assertEquals(1, result.parts.size)

    assertTrue(result.parts[0] is MarkdownPart.ParagraphPart.Text)
  }

}
