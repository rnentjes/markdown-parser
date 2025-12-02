package nl.astraeus.wiki.parser

enum class MarkdownType {
  CODE,
  PARAGRAPH,
  ORDERED_LIST,
  UNORDERED_LIST,
  TABLE,
}

fun markdown(text: String): List<MarkdownPart> {
  val lines = text.lines()
  val parts = mutableListOf<MarkdownPart>()
  var language = ""
  var type = MarkdownType.PARAGRAPH
  var listIndex = 1

  var index = 0
  val buffer = StringBuilder()

  fun parseBuffer() {
    if (buffer.isNotBlank()) {
      parts.addAll(handleBuffer(type, buffer.toString(), language))
    }
    buffer.clear()
    type = MarkdownType.PARAGRAPH
    language = ""
  }

  while (index < lines.size) {
    val rawLine = lines[index]
    val line = rawLine.trim()
    //println("BUFFER [${buffer.length}] TYPE ${type} \t LINE - ${line}")
    when {
      type == MarkdownType.ORDERED_LIST -> {
        if (!line.startsWith("${listIndex++}.")) {
          parseBuffer()
          continue
        } else {
          buffer.append(line.substring(2))
          buffer.append("\n")
        }
      }

      type == MarkdownType.UNORDERED_LIST -> {
        if (!line.startsWith("- ") &&
          !line.startsWith("* ")
        ) {
          parseBuffer()
          continue
        } else {
          buffer.append(line.substring(2))
          buffer.append("\n")
        }
      }

      type == MarkdownType.TABLE -> {
        if (!line.startsWith("|")) {
          parseBuffer()
          continue
        } else {
          buffer.append(line)
          buffer.append("\n")
        }
      }

      type == MarkdownType.PARAGRAPH && line.isBlank() -> {
        buffer.append("\n")
        parseBuffer()
      }

      line.startsWith("```") -> {
        if (type != MarkdownType.CODE) {
          parseBuffer()
          type = MarkdownType.CODE
          language = line.substring(3).trim()
        } else {
          parseBuffer()
        }
      }

      type == MarkdownType.CODE -> {
        buffer.append(rawLine)
        buffer.append("\n")
        index++
        continue
      }

      line.startsWith("1.") -> {
        parseBuffer()
        type = MarkdownType.ORDERED_LIST
        listIndex = 2
        buffer.append(line.substring(2))
        buffer.append("\n")
      }

      line.startsWith("- ") || line.startsWith("* ") -> {
        parseBuffer()
        type = MarkdownType.UNORDERED_LIST
        buffer.append(line.substring(2))
        buffer.append("\n")
      }

      line.startsWith("|") -> {
        parseBuffer()
        type = MarkdownType.TABLE
        buffer.append(line)
        buffer.append("\n")
      }

      line.startsWith("---") -> {
        parseBuffer()
        parts.add(MarkdownPart.Ruler())
      }

      line.startsWith("#") -> {
        parseBuffer()
        val headerLevel = line.takeWhile { it == '#' }.length
        val headerText = line.substring(headerLevel).trim()
        parts.add(MarkdownPart.Header(headerText, headerLevel))
      }

      line == "[break]" -> {
        parseBuffer()
        parts.add(MarkdownPart.PageBreak)
      }

      else -> {
        // Preserve trailing spaces for hard line breaks (two spaces at end of line)
        buffer.append(rawLine)
        buffer.append("\n")
      }
    }
    index++
  }

  parseBuffer()

  return parts
}

private fun handleBuffer(
  type: MarkdownType,
  text: String,
  language: String = ""
): List<MarkdownPart> = when (type) {
  MarkdownType.CODE -> {
    listOf(MarkdownPart.CodeBlock(text, language))
  }

  MarkdownType.PARAGRAPH -> {
    listOf(parseParagraph(text))
  }

  MarkdownType.ORDERED_LIST -> {
    listOf(MarkdownPart.OrderedList(text.lines()))
  }

  MarkdownType.UNORDERED_LIST -> {
    listOf(MarkdownPart.UnorderedList(text.lines()))
  }

  MarkdownType.TABLE -> {
    parseTable(text)
  }
}
