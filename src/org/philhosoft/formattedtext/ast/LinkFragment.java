package org.philhosoft.formattedtext.ast;

import java.util.ArrayList;
import java.util.List;

public class LinkFragment implements Fragment
{
	private List<Fragment> fragments = new ArrayList<Fragment>(); // source anchor
	private String url = ""; // destination anchor

	public LinkFragment()
	{
	}
	public LinkFragment(String text, String url)
	{
		add(text);
		this.url = url;
	}

	public void add(String text)
	{
		add(new TextFragment(text));
	}
	public void add(Fragment newFragment)
	{
		fragments.add(newFragment);
	}

	public void addURL(String url)
	{
		this.url = url;
	}

	public List<Fragment> getFragments()
	{
		return fragments;
	}
	public String getUrl()
	{
		return url;
	}

	@Override
	public <T> void accept(MarkupVisitor<T> visitor, T output)
	{
		visitor.visit(this, output);
	}

	@Override
	public int hashCode()
	{
		return fragments.hashCode() * 31 + url.hashCode();
	}
	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;
		if (!(obj instanceof LinkFragment))
			return false;
		LinkFragment uf = (LinkFragment) obj;
		return uf.fragments.equals(this.fragments) && uf.url.equals(this.url);
	}
	@Override
	public String toString()
	{
		return "LinkFragment[" + fragments + "](" + url + ")";
	}
}
