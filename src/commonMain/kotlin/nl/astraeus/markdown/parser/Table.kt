package nl.astraeus.markdown.parser

fun parseTable(text: String): List<MarkdownPart> {
  val lines = text.lines().map { it.trim() }.filter { it.isNotEmpty() }

  fun parseCells(line: String): List<String> {
    val trimmed = line.trim().trim('|')
    return if (trimmed.isEmpty()) emptyList() else trimmed.split("|").map { it.trim() }
  }

  fun isSeparatorRow(cells: List<String>): Boolean {
    if (cells.isEmpty()) return false
    return cells.all { cell ->
      val dashCount = cell.count { it == '-' }
      val cleaned = cell.replace("-", "").replace("|", "").trim()
      dashCount >= 3 && cleaned.isEmpty()
    }
  }

  return if (lines.size < 2) {
    // Not enough lines to be a table, fallback to code block
    listOf(MarkdownPart.CodeBlock(text, "table"))
  } else {
    val headerCells = parseCells(lines.first())
    val sepCells = parseCells(lines[1])

    if (headerCells.isEmpty() || !isSeparatorRow(sepCells)) {
      // Invalid table format, fallback to code block
      listOf(MarkdownPart.CodeBlock(text, "table"))
    } else {
      val colCount = headerCells.size
      val rows = mutableListOf<List<String>>()
      for (i in 2 until lines.size) {
        val rowCells = parseCells(lines[i]).toMutableList()
        // Normalize column count to headers size
        if (rowCells.size < colCount) {
          while (rowCells.size < colCount) rowCells.add("")
        } else if (rowCells.size > colCount) {
          // Trim extras
          while (rowCells.size > colCount) rowCells.removeAt(rowCells.lastIndex)
        }
        rows.add(rowCells)
      }

      listOf(MarkdownPart.Table(headers = headerCells, rows = rows))
    }
  }
}