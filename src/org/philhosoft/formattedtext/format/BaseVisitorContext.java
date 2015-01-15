package org.philhosoft.formattedtext.format;

import org.philhosoft.collection.SimpleStack;
import org.philhosoft.formattedtext.ast.Block;
import org.philhosoft.formattedtext.ast.BlockType;
import org.philhosoft.formattedtext.ast.TypedBlock;

public abstract class BaseVisitorContext implements VisitorContext
{
	private static class FirstLast
	{
		private Block parentBlock;
		private boolean first;
		private boolean last;

		public FirstLast(Block parentBlock, boolean first, boolean last)
		{
			this.parentBlock = parentBlock;
			this.first = first;
			this.last = last;
		}
		public void setFirstLast(boolean first, boolean last)
		{
			this.first = first;
			this.last = last;
		}
		public Block getParent()
		{
			return parentBlock;
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
			String type = parentBlock == null ? null : "Line";
			if (parentBlock instanceof TypedBlock)
				type = ((TypedBlock) parentBlock).getType().name();
			return "[parentBlock=" + type +  ", first=" + first + ", last=" + last + "]";
		}
	}

	protected SimpleStack<FirstLast> firstLastList = new SimpleStack<FirstLast>();

	@Override
	public void push(Block parentBlock, boolean first, boolean last)
	{
		firstLastList.push(new FirstLast(parentBlock, first, last));
	}

	@Override
	public void setFirstLast(boolean first, boolean last)
	{
		if (!firstLastList.isEmpty())
		{
			firstLastList.peek().setFirstLast(first, last);
		}
	}

	@Override
	public void pop()
	{
		if (!firstLastList.isEmpty())
		{
			firstLastList.pop();
		}
	}

	@Override
	public boolean isFirst()
	{
		if (firstLastList.isEmpty())
			return true;
		return firstLastList.peek().getFirst();
	}

	@Override
	public boolean isLast()
	{
		if (firstLastList.isEmpty())
			return true;
		return firstLastList.peek().getLast();
	}

	@Override
	public boolean isInOneOf(BlockType... blockTypes)
	{
		BlockType blockType = BlockType.DOCUMENT;
		if (firstLastList.size() > 0)
		{
			Block parent = firstLastList.peek().getParent();
			if (parent instanceof TypedBlock)
			{
				blockType = ((TypedBlock) parent).getType();
			}
		}
		for (BlockType bt : blockTypes)
		{
			if (bt == blockType)
				return true;
		}
		return false;
	}
}
