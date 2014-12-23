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
//		context.setParent(fragment);

		fragment.getDecoration().accept(fragmentStartVisitor, context);
		VisitorHelper.visitFragments(fragment.getFragments(), this, context);
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
		VisitorHelper.visitFragments(fragment.getFragments(), this, context);
		context.append("</a>");
	}

	@Override
	public void visit(TypedBlock block, VisitorContext context)
	{
//		context.setParent(block);

		context.append("\n");
		block.getType().accept(blockStartVisitor, context);
		VisitorHelper.visitBlocks(block.getBlocks(), this, context);
		block.getType().accept(blockEndVisitor, context);
		if (context.isLast())
		{
			context.append("[Last Block]\n");
		}
	}

	@Override
	public void visit(Line line, VisitorContext context)
	{
		VisitorHelper.visitFragments(line.getFragments(), this, context);
		if (!context.isLast())
		{
			context.append("<br>\n");
		}
	}
}
