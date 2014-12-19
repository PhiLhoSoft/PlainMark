package org.philhosoft.formattedtext.ast;

public class PlainTextFragment implements Fragment
{
	private String text;

	public PlainTextFragment(String text)
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
		if (!(obj instanceof PlainTextFragment))
			return false;
		return ((PlainTextFragment) obj).text.equals(this.text);
	}
	@Override
	public String toString()
	{
		return "PlainTextFragment[" + text + "]";
	}
}
