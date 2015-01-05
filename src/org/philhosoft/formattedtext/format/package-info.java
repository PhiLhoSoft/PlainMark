/**
 * Formatters of "formatted text", defined in the ast package.<br>
 * Made with visitors walking the tree of formatted text and exporting the result in
 * a context which can be rendered as a string.
 * <p>{@link ContextWithStringBuilder} is one implementation of such context, cumulating the
 * rendering in a StringBuilder.
 * <p>There are currently two renderers / formatters / visitors:
 * <ul>
 * <li>{@link PlainTextVisitor} just outputs the textual content of the AST.
 * <li>{@link HTMLVisitor} renders the output to HTML. For added flexibility, one can define
 * BlockType.Visitor and FragmentDecoration.Visitor (or extend the default ones) to
 * render some styles to specific tags (eg. b instead of strong, etc.).
 * </ul>
 */
package org.philhosoft.formattedtext.format;

