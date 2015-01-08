package org.philhosoft.formattedtext.ast;

/**
 * A fragment with only text in it.<br>
 * Leaf of a tree of decorated fragments.
 */
public class TextFragment implements Fragment
{
	private String text;

	public TextFragment(String text)
	{
		this.text = text;
	}

	public String getText()
	{
		return text;
	}

	@Override
	public <T> void accept(MarkupVisitor<T> visitor, T output)
	{
		visitor.visit(this, output);
	}

	@Override
	public int hashCode()
	{
		return text.hashCode();
	}
	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;
		if (!(obj instanceof TextFragment))
			return false;
		return ((TextFragment) obj).text.equals(this.text);
	}
	@Override
	public String toString()
	{
		return "TextFragment{" + text + "}";
	}
}
