package org.philhosoft.formattedtext.format;

import java.util.ArrayDeque;
import java.util.Deque;

public abstract class BaseVisitorContext implements VisitorContext
{
	private static class FirstLast
	{
		private String which;
		private boolean first;
		private boolean last;

		public FirstLast(String which, boolean first, boolean last)
		{
			this.which = which;
			this.first = first;
			this.last = last;
		}
		public void setFirstLast(String which, boolean first, boolean last)
		{
			this.which = "(" + which + ")";
			this.first = first;
			this.last = last;
		}
		public boolean getFirst()
		{
			return first;
		}
		public boolean getLast()
		{
			return last;
		}
		@Override
		public String toString()
		{
			return "[which=" + which +  ", first=" + first + ", last=" + last + "]";
		}
	}
	Deque<FirstLast> firstLastList = new ArrayDeque<FirstLast>();

	@Override
	public void push(String which, boolean first, boolean last)
	{
		firstLastList.push(new FirstLast(which, first, last));
	}

	@Override
	public void setFirstLast(String which, boolean first, boolean last)
	{
		if (firstLastList.size() > 0)
		{
			firstLastList.peek().setFirstLast(which, first, last);;
		}
	}

	@Override
	public void pop()
	{
		if (firstLastList.size() > 0)
		{
			firstLastList.pop();
		}
	}

	@Override
	public boolean isFirst()
	{
		if (firstLastList.size() == 0)
			return true;
		return firstLastList.peek().getFirst();
	}

	@Override
	public boolean isLast()
	{
		if (firstLastList.size() == 0)
			return true;
		return firstLastList.peek().getLast();
	}
}
