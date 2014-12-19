package org.philhosoft.formattedtext.ast;

import java.util.ArrayList;
import java.util.List;

public class Line implements Block
{
	private List<Fragment> fragments = new ArrayList<Fragment>();

	public Line()
	{
	}
	public Line(String text)
	{
		fragments.add(new PlainTextFragment(text));
	}

	public List<Fragment> getFragments()
	{
		return fragments;
	}

	@Override
	public <T> void accept(MarkupVisitor<T> visitor, T output)
	{
		visitor.visit(this, output);
	}

	@Override
	public int hashCode()
	{
		return fragments.hashCode();
	}
	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;
		if (!(obj instanceof TypedBlock))
			return false;
		return ((Line) obj).fragments == this.fragments;
	}
	@Override
	public String toString()
	{
		return "Line" + fragments;
	}
}
