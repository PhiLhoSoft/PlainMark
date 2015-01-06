package org.philhosoft.formattedtext.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * A line is created each time the parser finds a start of line.<br>
 * It contains a series of fragments, each with their own decoration, if any.
 */
public class Line implements Block
{
	private List<Fragment> fragments = new ArrayList<Fragment>();

	public Line()
	{
	}
	public Line(Fragment fragment)
	{
		fragments.add(fragment);
	}

	@Override
	public void add(String text)
	{
		fragments.add(new TextFragment(text));
	}
	public void add(Fragment fragment)
	{
		fragments.add(fragment);
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
		if (!(obj instanceof Line))
			return false;
		return ((Line) obj).fragments.equals(this.fragments);
	}
	@Override
	public String toString()
	{
		return "Line[" + fragments + "]";
	}
}
