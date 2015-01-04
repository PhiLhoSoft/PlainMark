package org.philhosoft.formattedtext.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * A decorated fragment
 */
public class DecoratedFragment implements Fragment
{
	private FragmentDecoration decoration;
	private List<Fragment> fragments = new ArrayList<Fragment>();

	public DecoratedFragment(FragmentDecoration decoration)
	{
		this.decoration = decoration;
	}

	public DecoratedFragment(FragmentDecoration decoration, String firstText)
	{
		this(decoration);
		fragments.add(new TextFragment(firstText));
	}

	public void add(String text)
	{
		fragments.add(new TextFragment(text));
	}
	public void add(Fragment fragment)
	{
		fragments.add(fragment);
	}

	public FragmentDecoration getDecoration()
	{
		return decoration;
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
		return 31 * decoration.hashCode() + fragments.hashCode();
	}
	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;
		if (!(obj instanceof DecoratedFragment))
			return false;
		DecoratedFragment tb = (DecoratedFragment) obj;
		return tb.decoration == this.decoration && tb.fragments.equals(this.fragments);
	}
	@Override
	public String toString()
	{
		return "DecoratedFragment{decoration=" + decoration + ", fragments=" + fragments + "}";
	}
}
