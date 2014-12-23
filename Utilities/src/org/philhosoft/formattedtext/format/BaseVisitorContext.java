package org.philhosoft.formattedtext.format;

public abstract class BaseVisitorContext implements VisitorContext
{
	boolean first, last;

	@Override
	public boolean isFirst()
	{
		return first;
	}

	@Override
	public boolean isLast()
	{
		return last;
	}

	@Override
	public void setFirst(boolean first)
	{
		this.first = first;
	}

	@Override
	public void setLast(boolean last)
	{
		this.last = last;
	}
}
