package org.philhosoft.formattedtext.ast;

/**
 * Interface for a grouping of lines of text, decorated or not.
 */
public interface Block extends MarkedText
{
	void add(String text);
}
