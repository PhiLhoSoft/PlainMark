SimpleMark: a Simple Humane Markup
==================================

Yet another Markdown-style parser, written in Java (might do implementations in Dart and Ceylon, as exercises).

See "SimpleMark - Simple Humane Markup.sm" explaining in detail the ideas behind this project. Of course, it uses the SimpleMark markup as a kind of real-world self-test...

SimpleMark is a text markup inspired by Markdown, but simplified to ease parsing, learning and usage.

It is closer to the simplified version used in [Google+](http://webapps.stackexchange.com/questions/23078/what-are-all-the-formatting-options-for-a-google-post) (newlines mark line breaks, simplified bold / italic marks), [GitHub](https://help.github.com/articles/markdown-basics/) ([aka. GitHub Flavored Markdown](https://help.github.com/articles/github-flavored-markdown/), blocks of code) or [Stackoverflow](http://stackoverflow.com/editing-help) than to the original spec.<br>
It aims more at writing short comments than writing big, complex documents. For the latter, something like [CommonMark](http://commonmark.org/) is better suited.

One of the main departure from the original spec is that the newline character does a line break. No mandatory empty line nonsense, that confuse so much users and often result in mangled messages. Text area in browsers and in modern editors automatically wrap long lines, so auto-join of consecutive lines is an outdated feature.<br>
The "double space at end of line" convention to do a simple line-break (not a paragraph) is also nonsensical: I dislike relying on non-visible characters, and I set up my editors to remove trailing spaces!

As a consequence, this convention simplifies greatly the spec (and the parsing!) and the usage of the markup.

Likewise, like Google+, I favor one character signs for styling fragments, with distinct usages. The fact that * and _ have the same rendering in Markdown, and that we must double (** and __) and triple (*** or ___) them to have a different rendering looks quite confusing...

The markup uses Ascii characters in a given context to apply a special style (eg. HTML markup + CSS) to whole lines of text (blocks, div-like in HTML) or to fragments of text (span-like in HTML).<br>
These special characters can loose their meaning in some context, and can always be escaped with the tilde `~` sign preceding them.<br>
If tilde precedes a non-markup character, it is kept literal. It can also be doubled to figure a literal tilde.

Block markup characters are defined at the start of a line, regardless of initial spaces. These initial spaces are just skipped. In the future (an extension?), they might be used for nesting blocks.
Fragment markup characters loose their meaning if surrounded by spaces or by letters or digits.<br>
A fragment of text stops at the end of the line: authors rarely want to have several paragraphs of bold text, and thus a missing ending char doesn't spread over the whole remainder of the text.

### Limitations

There is no support for blockquotes (I prefer to use double quotes surrounding an italicized citation), tables or images. No HTML markup can be used, `&` ,`<` and `>` signs are escaped (kept literal) in an HTML rendering. HTML entities are not supported (might be, later).
