package org.philhosoft.formattedtext.ast;

/**
 * Text with, or without decoration / type, organized in fragments of line, lines, grouped in blocks.
 */
public interface MarkedText
{
	void add(String text);

	<T> void accept(MarkupVisitor<T> visitor, T output);
}
