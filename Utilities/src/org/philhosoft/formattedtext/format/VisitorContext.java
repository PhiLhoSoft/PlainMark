package org.philhosoft.formattedtext.format;

import org.philhosoft.formattedtext.ast.Block;
import org.philhosoft.formattedtext.ast.BlockType;


/**
 * Context for formatting visitors.<br>
 * This wraps an output (StringBuilder, List, OutputStream, etc.) and provides information on the context of the visit.
 */
public interface VisitorContext
{
	VisitorContext append(String out);

	String asString();

	boolean isFirst();
	boolean isLast();
	boolean isInOneOf(BlockType... blockTypes);

	void push(Block parentBlock, boolean first, boolean last);
	void setFirstLast(boolean first, boolean last);
	void pop();
}
