package nl.astraeus.wiki.parser

sealed class MarkdownPart {

  data object NewLine : MarkdownPart()

  data object PageBreak : MarkdownPart()

  sealed class ParagraphPart() {
    data class Text(
      val text: String
    ) : ParagraphPart()

    data object LineBreak : ParagraphPart()

    data class Link(
      val url: String,
      val label: String? = null,
      val title: String? = null,
    ) : ParagraphPart()

    data class Image(
      val alt: String,
      val src: String,
      val url: String? = null,
    ) : ParagraphPart()

    data class Bold(
      val text: String
    ) : ParagraphPart()

    data class Italic(
      val text: String
    ) : ParagraphPart()

    class BoldItalic(
      val text: String
    ) : ParagraphPart()

    class StrikeThrough(
      val text: String
    ) : ParagraphPart()

    class InlineCode(
      val text: String
    ) : ParagraphPart()
  }

  data class Paragraph(
    val parts: List<ParagraphPart>
  ) : MarkdownPart()

  data class Header(
    val text: String,
    val size: Int
  ) : MarkdownPart()

  data class UnorderedList(
    val lines: List<String>,
  ) : MarkdownPart()

  data class OrderedList(
    val lines: List<String>,
  ) : MarkdownPart()

  data class CodeBlock(
    val text: String,
    val language: String
  ) : MarkdownPart()

  data class Table(
    val headers: List<String>,
    val rows: List<List<String>>,
  ) : MarkdownPart()

  class Ruler() : MarkdownPart()
}
