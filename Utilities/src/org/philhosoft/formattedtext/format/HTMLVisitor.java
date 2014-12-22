package org.philhosoft.formattedtext.format;

import org.philhosoft.formattedtext.ast.Block;
import org.philhosoft.formattedtext.ast.BlockType;
import org.philhosoft.formattedtext.ast.DecoratedFragment;
import org.philhosoft.formattedtext.ast.Fragment;
import org.philhosoft.formattedtext.ast.FragmentDecoration;
import org.philhosoft.formattedtext.ast.Line;
import org.philhosoft.formattedtext.ast.LinkFragment;
import org.philhosoft.formattedtext.ast.MarkupVisitor;
import org.philhosoft.formattedtext.ast.TextFragment;
import org.philhosoft.formattedtext.ast.TypedBlock;

public class HTMLVisitor implements MarkupVisitor<StringBuilder>
{
	private FragmentDecoration.Visitor<StringBuilder> fragmentStartVisitor = new FragmentStartVisitor();
	private FragmentDecoration.Visitor<StringBuilder> fragmentEndVisitor = new FragmentEndVisitor();
	private BlockType.Visitor<StringBuilder> blockStartVisitor = new BlockStartVisitor();
	private BlockType.Visitor<StringBuilder> blockEndVisitor = new BlockEndVisitor();

	/**
	 * Allows overriding the default fragment visitors.
	 */
	public void setFragmentVisitors(
			FragmentDecoration.Visitor<StringBuilder> fragmentStartVisitor,
			FragmentDecoration.Visitor<StringBuilder> fragmentEndVisitor)
	{
		this.fragmentStartVisitor = fragmentStartVisitor;
		this.fragmentEndVisitor = fragmentEndVisitor;
	}
	/**
	 * Allows overriding the default block visitors.
	 */
	public void setBlockVisitors(
			BlockType.Visitor<StringBuilder> blockStartVisitor,
			BlockType.Visitor<StringBuilder> blockEndVisitor)
	{
		this.blockStartVisitor = blockStartVisitor;
		this.blockEndVisitor = blockEndVisitor;
	}

	@Override
	public void visit(DecoratedFragment fragment, StringBuilder output)
	{
		fragment.getDecoration().accept(fragmentStartVisitor, output);
		for (Fragment f : fragment.getFragments())
		{
			f.accept(this, output);
		}
		fragment.getDecoration().accept(fragmentEndVisitor, output);
	}

	@Override
	public void visit(TextFragment fragment, StringBuilder output)
	{
		output.append(fragment.getText());
	}

	@Override
	public void visit(LinkFragment fragment, StringBuilder output)
	{
		output.append("<a href='").append(fragment.getUrl()).append("'>");
		for (Fragment f : fragment.getFragments())
		{
			f.accept(this, output);
		}
		output.append("</a>");
	}

	@Override
	public void visit(TypedBlock block, StringBuilder output)
	{
		output.append("\n");
		block.getType().accept(blockStartVisitor, output);
		for (Block b : block.getBlocks())
		{
			b.accept(this, output);
		}
		block.getType().accept(blockEndVisitor, output);
		output.append("\n");
	}

	@Override
	public void visit(Line line, StringBuilder output)
	{
		for (Fragment f : line.getFragments())
		{
			f.accept(this, output);
		}
//		output.append("<br>\n");
	}
}
