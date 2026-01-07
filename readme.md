# Markdown parser in kotlin multiplatfom common

A basic markdown parser in kotlin multiplatform common. It can be used on any platform supported by kotlin.
By default only javascript en the jvm are configured. Fork the repository and add any platform you need, no
other changes should be required.

See the [Markdown.kt](src/commonMain/kotlin/nl/astraeus/markdown/parser/Markdown.kt) class for the format of the output.

## JVM & JS versions available on maven central

    implementation("nl.astraeus:markdown-parser:1.0.4")

## Usage

```kotlin
val markdown = markdown("# Markdown\n\nMy **markdown** text.")
```

This will give you a list of MarkdownPart elements:

- MarkdownPart.Header(text="Markdown", size=1)
- MarkdownPart.Paragraph(parts=
    - Text("My ")
    - Bold("markdown")
    - Text(" text.\n")
      )

A paragraph can be parsed separately, e.g.

```koltin
val paragraph = parseParagraph("My **markdown** text.")
```

Which will produce a paragraph element with 3 parts:

- Text("My ")
- Bold("markdown")
- Text(" text.\n")

## Mark down support

### Headers

# Header 1

## Header 2

### Header 3

#### Header 4

##### Header 5

###### Header 6

### Horizontal line

---

### Formatting

Text can be formatted with **bold**, *italic*, ***bold and italic***, ~~strikethrough~~, and `code`.

### Lists

- *Bullet* point 1
- Bullet **point** 2
- Bullet point ***3***
- ~~Bullet point 4~~

### Lists with checkboxes

- [ ] Checkbox 1
- [x] Checkbox 2
- [ ] Checkbox 3

### Ordered list

1. Item 1
2. Item 2
3. Item 3

### Ordered list alternate syntax

-. Item 1
-. Item 2
-. Item 3

### Code blocks

You can create code blocks by indenting lines with two (or more) spaces or inline using the `backtick character` \`.

    Some code
    Block code

Also code blocks are supported:

```kotlin
fun main() {
  val md = markdown("Markdown text")
}
```

The language (or whatever the text is) will be added to the MarkdownPart.CodeBlock class.

### Images and links

Images and links with labels are supported.

### Tables

| Header 1      | Header 2      |
|---------------|---------------|
| Row 1, Cell 1 | Row 1, Cell 2 |
| Row 2, Cell 1 | Row 2, Cell 2 |
| Row 3         | etc           |
