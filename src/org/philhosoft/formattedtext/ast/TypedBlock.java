package org.philhosoft.formattedtext.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * A typed block has a type (header, list, etc.) that applies to all the blocks it contains.<br>
 * These blocks can be typed too, nesting these types.
 */
public class TypedBlock implements Block
{
	private BlockType type;
	private List<Block> blocks = new ArrayList<Block>();

	public TypedBlock(BlockType type)
	{
		this.type = type;
	}

	@Override
	public void add(String text)
	{
		Line line = new Line();
		line.add(text);
		blocks.add(line);
	}
	public void add(Block block)
	{
		blocks.add(block);
	}

	public BlockType getType()
	{
		return type;
	}
	public List<Block> getBlocks()
	{
		return blocks;
	}

	@Override
	public <T> void accept(MarkupVisitor<T> visitor, T output)
	{
		visitor.visit(this, output);
	}

	@Override
	public int hashCode()
	{
		return 31 * type.hashCode() + blocks.hashCode();
	}
	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;
		if (!(obj instanceof TypedBlock))
			return false;
		TypedBlock tb = (TypedBlock) obj;
		return tb.type == this.type && tb.blocks.equals(this.blocks);
	}
	@Override
	public String toString()
	{
		return "\nTypedBlock{type=" + type + ", blocks=" + blocks + "}";
	}
}
