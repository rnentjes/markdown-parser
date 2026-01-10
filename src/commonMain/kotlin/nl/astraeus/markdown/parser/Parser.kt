package nl.astraeus.markdown.parser

enum class MarkdownType {
  CODE,
  PARAGRAPH,
  ORDERED_LIST,
  UNORDERED_LIST,
  CHECKBOX_LIST,
  TABLE,
  INDENTED_CODE,
}

fun markdown(text: String): List<MarkdownPart> {
  val lines = text.lines()
  val parts = mutableListOf<MarkdownPart>()
  var language = ""
  var type = MarkdownType.PARAGRAPH
  var listIndex = 1
  var checkboxLine = 0

  var index = 0
  val buffer = StringBuilder()
  val checkboxList = mutableListOf<CheckboxItem>()

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
        if (line.isBlank()) {
          parseBuffer()
          continue
        } else if (
          line.startsWith("${listIndex++}.")
          || line.startsWith("-.")
          || line.startsWith("#.")
        ) {
          buffer.append("\n")
          buffer.append(line.substring(2))
        } else {
          buffer.append(" ")
          buffer.append(line)
        }
      }

      type == MarkdownType.CHECKBOX_LIST -> {
        if (line.isBlank()) {
          if (buffer.isNotBlank()) {
            addCheckbox(checkboxList, checkboxLine, buffer)
          }
          parts.add(MarkdownPart.CheckboxList(checkboxList))
          parseBuffer()
          continue
        } else if (line.startsWith("- [ ]") || line.startsWith("- [x]")) {
          if (buffer.isNotBlank()) {
            addCheckbox(checkboxList, checkboxLine, buffer)
          }
          checkboxLine = index
          buffer.append(line)
        } else {
          buffer.append(" ")
          buffer.append(line)
        }
      }

      type == MarkdownType.UNORDERED_LIST -> {
        if (line.isBlank()) {
          parseBuffer()
          continue
        } else if (line.startsWith("- ") || line.startsWith("* ")) {
          buffer.append("\n")
          buffer.append(line.substring(2))
        } else {
          buffer.append(" ")
          buffer.append(line)
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

      type == MarkdownType.INDENTED_CODE -> {
        if (!rawLine.startsWith("  ")) {
          parseBuffer()
          continue
        } else {
          buffer.append(line)
          buffer.append("\n")
        }
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

      line.startsWith("1.") || line.startsWith("-.") || line.startsWith("#.") -> {
        parseBuffer()
        type = MarkdownType.ORDERED_LIST
        listIndex = 2
        buffer.append(line.substring(2))
      }

      line.startsWith("- [ ]") || line.startsWith("- [x]") -> {
        parseBuffer()
        type = MarkdownType.CHECKBOX_LIST
        checkboxLine = index
        buffer.append(line)
      }

      line.startsWith("- ") || line.startsWith("* ") -> {
        parseBuffer()
        type = MarkdownType.UNORDERED_LIST
        buffer.append(line.substring(2))
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

      rawLine.startsWith("  ") -> {
        parseBuffer()
        type = MarkdownType.INDENTED_CODE
        buffer.append(line)
        buffer.append("\n")
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

  if (type == MarkdownType.CHECKBOX_LIST) {
    if (buffer.isNotBlank()) {
      addCheckbox(checkboxList, checkboxLine, buffer)
    }
    parts.add(MarkdownPart.CheckboxList(checkboxList))
  } else {
    parseBuffer()
  }

  return parts
}

private fun addCheckbox(
  checkboxList: MutableList<CheckboxItem>,
  index: Int,
  buffer: StringBuilder
) {
  if (buffer.length >= 5) {
    checkboxList.add(
      CheckboxItem(
        index,
        buffer.startsWith("- [x]"),
        buffer.substring(5).trim()
      )
    )
  } else {
    println("Invalid checkbox format: $buffer")
  }
  buffer.clear()
}

private fun handleBuffer(
  type: MarkdownType,
  text: String,
  language: String = ""
): List<MarkdownPart> = when (type) {
  MarkdownType.CODE -> {
    listOf(MarkdownPart.CodeBlock(text, language))
  }

  MarkdownType.INDENTED_CODE -> {
    listOf(MarkdownPart.CodeBlock(text, "block"))
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

  MarkdownType.CHECKBOX_LIST -> {
    error("Checkbox list is handled separately")
  }

  MarkdownType.TABLE -> {
    parseTable(text)
  }
}
