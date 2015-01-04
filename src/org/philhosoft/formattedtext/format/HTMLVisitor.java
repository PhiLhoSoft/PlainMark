package org.philhosoft.formattedtext.format;

import org.philhosoft.formattedtext.ast.BlockType;
import org.philhosoft.formattedtext.ast.DecoratedFragment;
import org.philhosoft.formattedtext.ast.FragmentDecoration;
import org.philhosoft.formattedtext.ast.Line;
import org.philhosoft.formattedtext.ast.LinkFragment;
import org.philhosoft.formattedtext.ast.MarkupVisitor;
import org.philhosoft.formattedtext.ast.TextFragment;
import org.philhosoft.formattedtext.ast.TypedBlock;

/**
 * Visitor that contexts HTML from some markup.
 */
public class HTMLVisitor implements MarkupVisitor<VisitorContext>
{
	private FragmentDecoration.Visitor<VisitorContext> fragmentStartVisitor = new FragmentStartVisitor();
	private FragmentDecoration.Visitor<VisitorContext> fragmentEndVisitor = new FragmentEndVisitor();
	private BlockType.Visitor<VisitorContext> blockStartVisitor = new BlockStartVisitor();
	private BlockType.Visitor<VisitorContext> blockEndVisitor = new BlockEndVisitor();

	/**
	 * Allows overriding the default fragment visitors.
	 */
	public void setFragmentVisitors(
			FragmentDecoration.Visitor<VisitorContext> fragmentStartVisitor,
			FragmentDecoration.Visitor<VisitorContext> fragmentEndVisitor)
	{
		this.fragmentStartVisitor = fragmentStartVisitor;
		this.fragmentEndVisitor = fragmentEndVisitor;
	}
	/**
	 * Allows overriding the default block visitors.
	 */
	public void setBlockVisitors(
			BlockType.Visitor<VisitorContext> blockStartVisitor,
			BlockType.Visitor<VisitorContext> blockEndVisitor)
	{
		this.blockStartVisitor = blockStartVisitor;
		this.blockEndVisitor = blockEndVisitor;
	}

	@Override
	public void visit(DecoratedFragment fragment, VisitorContext context)
	{
		fragment.getDecoration().accept(fragmentStartVisitor, context);
		VisitorHelper.visitFragments(fragment.getFragments(), this, null, context);
		fragment.getDecoration().accept(fragmentEndVisitor, context);
	}

	@Override
	public void visit(TextFragment fragment, VisitorContext context)
	{
		context.append(fragment.getText());
	}

	@Override
	public void visit(LinkFragment fragment, VisitorContext context)
	{
		context.append("<a href='").append(fragment.getUrl()).append("'>");
		VisitorHelper.visitFragments(fragment.getFragments(), this, null, context);
		context.append("</a>");
	}

	@Override
	public void visit(TypedBlock block, VisitorContext context)
	{
		if (!context.isFirst())
		{
			context.append("\n");
		}
		boolean tagOnItsOwnLine = block.getType() == BlockType.DOCUMENT || block.getType() == BlockType.CODE;

		block.getType().accept(blockStartVisitor, context);
		if (block.getType() == BlockType.DOCUMENT)
		{
			context.append("\n");
		}
		VisitorHelper.visitBlocks(block.getBlocks(), this, block, context);
		if (tagOnItsOwnLine)
		{
			context.append("\n");
		}
		block.getType().accept(blockEndVisitor, context);
		if (tagOnItsOwnLine || context.isLast())
		{
			context.append("\n");
		}
	}

	@Override
	public void visit(Line line, VisitorContext context)
	{
		if (context.isInOneOf(BlockType.DOCUMENT) && !context.isFirst())
		{
			context.append("\n");
		}

		VisitorHelper.visitFragments(line.getFragments(), this, line, context);

		if (!context.isLast())
		{
			if (!context.isInOneOf(BlockType.CODE))
			{
				context.append("<br>");
			}
			context.append("\n");
		}
	}
}
