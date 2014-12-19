package org.philhosoft.formattedtext.ast;

/**
 * Marker interface for fragments of text, decorated or not.
 * <p>
 * A fragment can be plain text or a list of other fragments, with a given style.<br>
 * A fragment doesn't extend over line breaks.
 */
public interface Fragment extends MarkedText
{
}
