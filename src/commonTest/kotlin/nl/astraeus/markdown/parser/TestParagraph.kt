package nl.astraeus.markdown.parser

import nl.astraeus.wiki.parser.MarkdownPart
import nl.astraeus.wiki.parser.parseParagraph
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
}
