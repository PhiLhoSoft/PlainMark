package org.philhosoft.formattedtext.ast;

/**
 * A block groups lines of text, decorated or not.
 */
public interface Block extends MarkedText
{
	@Override
	void add(String text);
}
