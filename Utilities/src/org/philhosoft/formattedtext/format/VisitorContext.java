package org.philhosoft.formattedtext.format;


/**
 * Context for formatting visitors.<br>
 * This wraps an output (StringBuilder, List, OutputStream, etc.) and provides information on the context of the visit.
 */
public interface VisitorContext
{
	VisitorContext append(String out);

//	MarkedText getParent();
//	void setParent(MarkedText parent);

	boolean isFirst();
	boolean isLast();

	void setFirst(boolean first);
	void setLast(boolean last);
}
