package org.philhosoft.formattedtext.ast;

/**
 * Interface for fragments of text, decorated or not.
 * <p>
 * A fragment can be plain text or a list of other fragments, with a given style, allowing nesting of styles
 * (emphasis text inside strong text, for example).<br>
 * A fragment doesn't extend over line breaks.
 */
public interface Fragment extends MarkedText
{
}
