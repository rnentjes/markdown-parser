package nl.astraeus.markdown.parser

import nl.astraeus.markdown.parser.MarkdownPart.ParagraphPart.*

private enum class ParType {
  TEXT,
  LINK_LABEL,
  LINK_URL,
  LINK_TITLE,
  LINK_END,
  BOLD,
  ITALIC,
  BOLD_ITALIC,
  STRIKETHROUGH,
  INLINE_CODE,
  IMAGE_ALT,
  IMAGE_SRC,
  LINK_IMAGE_ALT,
  LINK_IMAGE_SRC,
  LINK_IMAGE_LINK,
}

private typealias ParagraphData = MutableMap<ParType, String>

private data class ParState(
  val fromType: ParType,
  val text: String,
  val toType: ParType,
  val out: (ParagraphData) -> MarkdownPart.ParagraphPart? = { _ -> null }
)

private val states = listOf(
  // Image with link
  ParState(ParType.TEXT, "[![", ParType.LINK_IMAGE_ALT) { data ->
    Text(data[ParType.TEXT]!!)
  },
  ParState(ParType.LINK_IMAGE_ALT, "](", ParType.LINK_IMAGE_SRC),
  ParState(ParType.LINK_IMAGE_SRC, ")](", ParType.LINK_IMAGE_LINK),
  ParState(ParType.LINK_IMAGE_LINK, ")", ParType.TEXT) { data ->
    Image(
      data[ParType.LINK_IMAGE_ALT]!!,
      data[ParType.LINK_IMAGE_SRC]!!,
      data[ParType.LINK_IMAGE_LINK],
    )
  },

  // Image without link
  ParState(ParType.TEXT, "![", ParType.IMAGE_ALT) { data ->
    Text(data[ParType.TEXT]!!)
  },
  ParState(ParType.IMAGE_ALT, "](", ParType.IMAGE_SRC),
  ParState(ParType.IMAGE_SRC, ")", ParType.TEXT) { data ->
    Image(
      data[ParType.IMAGE_ALT]!!,
      data[ParType.IMAGE_SRC]!!,
    )
  },

  // Links
  ParState(ParType.TEXT, "[", ParType.LINK_LABEL) { data ->
    Text(data[ParType.TEXT]!!)
  },
  ParState(ParType.LINK_LABEL, "](", ParType.LINK_URL),
  ParState(ParType.LINK_LABEL, "]", ParType.LINK_URL) { data ->
    Text(data[ParType.LINK_LABEL]!!)
  },
  ParState(ParType.LINK_URL, ")", ParType.TEXT) { data ->
    Link(data[ParType.LINK_URL]!!, data[ParType.LINK_LABEL])
  },

  ParState(ParType.LINK_URL, "\"", ParType.LINK_TITLE),
  ParState(ParType.LINK_TITLE, "\"", ParType.LINK_END),
  ParState(ParType.LINK_END, ")", ParType.TEXT) { data ->
    Link(
      data[ParType.LINK_URL]!!,
      data[ParType.LINK_LABEL],
      data[ParType.LINK_TITLE],
    )
  },

  ParState(ParType.TEXT, "***", ParType.BOLD_ITALIC) { data ->
    Text(data[ParType.TEXT]!!)
  },
  ParState(ParType.BOLD_ITALIC, "***", ParType.TEXT) { data ->
    BoldItalic(data[ParType.BOLD_ITALIC]!!)
  },

  ParState(ParType.TEXT, "~~", ParType.STRIKETHROUGH) { data ->
    Text(data[ParType.TEXT]!!)
  },
  ParState(ParType.STRIKETHROUGH, "~~", ParType.TEXT) { data ->
    StrikeThrough(data[ParType.STRIKETHROUGH]!!)
  },

  ParState(ParType.TEXT, "**", ParType.BOLD) { data ->
    Text(data[ParType.TEXT]!!)
  },
  ParState(ParType.BOLD, "**", ParType.TEXT) { data ->
    Bold(data[ParType.BOLD]!!)
  },

  ParState(ParType.TEXT, "*", ParType.ITALIC) { data ->
    Text(data[ParType.TEXT]!!)
  },
  ParState(ParType.ITALIC, "*", ParType.TEXT) { data ->
    BoldItalic(data[ParType.ITALIC]!!)
  },

  ParState(ParType.TEXT, "`", ParType.INLINE_CODE) { data ->
    Text(data[ParType.TEXT]!!)
  },
  ParState(ParType.INLINE_CODE, "`", ParType.TEXT) { data ->
    InlineCode(data[ParType.INLINE_CODE]!!)
  },
)

private fun String.test(index: Int, value: String): Boolean {
  return this.length >= index + value.length && this.substring(index, index + value.length) == value
}

fun parseParagraph(text: String): MarkdownPart.Paragraph {
  val result = mutableListOf<MarkdownPart.ParagraphPart>()
  val buffer = StringBuilder()
  var type = ParType.TEXT
  val data: ParagraphData = mutableMapOf()
  var index = 0
  var activeStates = states.filter { it.fromType == type }

  while (index < text.length) {
    var found = false
    for (state in activeStates) {
      if (state.fromType == type && text.test(index, state.text)) {
        data[state.fromType] = buffer.toString()
        buffer.clear()
        state.out(data)?.let {
          if (it !is Text || it.text.isNotBlank()) {
            result.add(it)
          }
        }
        type = state.toType
        index += state.text.length
        found = true
        activeStates = states.filter { it.fromType == type }
        break
      }
    }
    if (!found) {
      val ch = text[index]
      if (ch == '\n') {
        // Markdown hard line break: two or more spaces at end of line
        if (buffer.length >= 2 && buffer.endsWith("  ")) {
          val textBefore = buffer.substring(0, buffer.length - 2)
          if (textBefore.isNotEmpty()) {
            result.add(Text(textBefore))
          }
          result.add(LineBreak)
          buffer.clear()
        } else {
          // Keep original behavior for soft breaks (collapse later in HTML)
          buffer.append(ch)
        }
      } else {
        buffer.append(ch)
      }
      index++
    }
  }

  if (buffer.isNotEmpty()) {
    result.add(Text(buffer.toString()))
  }

  return MarkdownPart.Paragraph(result)
}
