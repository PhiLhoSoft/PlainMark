package org.philhosoft.formattedtext.ast;

/**
 * Marker interface extended both by Block and by Fragment.
 */
public interface MarkedText
{
	<T> void accept(MarkupVisitor<T> visitor, T output);
}
