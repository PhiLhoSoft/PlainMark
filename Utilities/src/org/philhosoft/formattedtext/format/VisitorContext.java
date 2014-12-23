package org.philhosoft.formattedtext.format;


/**
 * Context for formatting visitors.<br>
 * This wraps an output (StringBuilder, List, OutputStream, etc.) and provides information on the context of the visit.
 */
public interface VisitorContext
{
	VisitorContext append(String out);

	String asString();

//	MarkedText getParent();
//	void setParent(MarkedText parent);

	boolean isFirst();
	boolean isLast();

	void push(String which, boolean first, boolean last);
	void setFirstLast(String which, boolean first, boolean last);
	void pop();
}
