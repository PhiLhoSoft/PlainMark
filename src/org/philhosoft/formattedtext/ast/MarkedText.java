package org.philhosoft.formattedtext.ast;

/**
 * Interface for blocks and fragments of text.
 */
public interface MarkedText
{
	void add(String text);

	<T> void accept(MarkupVisitor<T> visitor, T output);
}
