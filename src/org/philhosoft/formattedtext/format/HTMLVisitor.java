package org.philhosoft.formattedtext.format;

import java.util.Arrays;

import org.philhosoft.formattedtext.ast.BlockType;
import org.philhosoft.formattedtext.ast.DecoratedFragment;
import org.philhosoft.formattedtext.ast.FragmentDecoration;
import org.philhosoft.formattedtext.ast.Line;
import org.philhosoft.formattedtext.ast.LinkFragment;
import org.philhosoft.formattedtext.ast.MarkupVisitor;
import org.philhosoft.formattedtext.ast.TextFragment;
import org.philhosoft.formattedtext.ast.TypedBlock;

/**
 * Renders Fragments and Blocks to an HTML representation.
 * <p>
 * Uses default visitors for start and end of fragments and blocks.
 * These can be overridden to customize the tags to use.
 */
public class HTMLVisitor implements MarkupVisitor<VisitorContext>
{
	private FragmentDecoration.Visitor<VisitorContext> fragmentStartVisitor = new HTMLFragmentStartVisitor();
	private FragmentDecoration.Visitor<VisitorContext> fragmentEndVisitor = new HTMLFragmentEndVisitor();
	private BlockType.Visitor<VisitorContext> blockStartVisitor = new HTMLBlockStartVisitor();
	private BlockType.Visitor<VisitorContext> blockEndVisitor = new HTMLBlockEndVisitor();
	private int tabSize;

	public HTMLVisitor()
	{
		this(4);
	}
	public HTMLVisitor(int tabSize)
	{
		this.tabSize = tabSize;
	}

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
		context.append(normalize(fragment.getText()));
	}

	@Override
	public void visit(LinkFragment fragment, VisitorContext context)
	{
		fragment.getDecoration().accept(fragmentStartVisitor, context);
		context.append("href='").append(normalize(fragment.getUrl())).append("'>");
		VisitorHelper.visitFragments(fragment.getFragments(), this, null, context);
		fragment.getDecoration().accept(fragmentEndVisitor, context);
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

	private String normalize(String text)
	{
		char[] filler = new char[tabSize];
		Arrays.fill(filler, ' ');
		String tab = new String(filler);
		return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\t", tab);
	}
}
