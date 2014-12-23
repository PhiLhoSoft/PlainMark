package org.philhosoft.formattedtext.format;

public class ContextWithStringBuilder extends BaseVisitorContext
{
	private StringBuilder builder = new StringBuilder();

	@Override
	public ContextWithStringBuilder append(String out)
	{
		builder.append(out);
		return this;
	}

	@Override
	public String asString()
	{
		return builder.toString();
	}

	@Override
	public String toString()
	{
		return builder.toString() + " // " + firstLastList;
	}
}
