package org.philhosoft.formattedtext.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * A line is created each time the parser finds a start of line.
 * <p>
 * It contains a series of fragments, each with their own decoration, if any.<br>
 * It is a bridge between fragments (it is a fragment grouping other fragments) and blocks (it is a leaf / base unit of block hierarchy).
 */
public class Line implements Block, Fragment
{
	private List<Fragment> fragments = new ArrayList<Fragment>();

	public Line()
	{
	}
	public Line(Fragment fragment)
	{
		add(fragment);
	}
	public Line(String text)
	{
		add(text);
	}

	@Override
	public FragmentDecoration getDecoration()
	{
		return null;
	}

	@Override
	public void add(String text)
	{
		add(new TextFragment(text));
	}
	@Override
	public void add(Fragment fragment)
	{
		fragments.add(fragment);
	}

	@Override
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
		return "\nLine[" + fragments + "]";
	}
}
