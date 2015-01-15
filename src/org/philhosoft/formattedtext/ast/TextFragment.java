package org.philhosoft.formattedtext.ast;

import java.util.Collections;
import java.util.List;

/**
 * A fragment with only plain text in it.
 * <p>
 * Leaf of a tree of decorated fragments.
 */
public class TextFragment implements Fragment
{
	private String text;

	public TextFragment(String text)
	{
		this.text = text;
	}

	@Override
	public FragmentDecoration getDecoration()
	{
		return null;
	}

	@Override
	public List<Fragment> getFragments()
	{
		return Collections.emptyList();
	}

	@Override
	public void add(String text)
	{
		if (this.text == null)
		{
			this.text = text;
		}
		else
		{
			this.text += text;
		}
	}
	@Override
	public void add(Fragment fragment)
	{
		throw new UnsupportedOperationException("Text fragment doesn't accept other fragments");
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
