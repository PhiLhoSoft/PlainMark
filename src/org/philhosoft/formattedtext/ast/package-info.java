/**
 * Generic data structure (abstract syntax tree) for defining formatted text.
 * <p>MarkedText is the general interface for these structures.
 * <p>Fragment-based classes define styled (or raw) text within a line.<br>
 * They correspond, for example, to the span tag in HTML,
 * or other flow-based tags like em or strong.
 * <p>Block-based classes define lines of styled text, with block-level style.<br>
 * They correspond, for example, to the div tag in HTML,
 * or other block-based tags like h3 or ul.
 */
package org.philhosoft.formattedtext.ast;

