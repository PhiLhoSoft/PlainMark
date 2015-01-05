package org.philhosoft.formattedtext.format;

import org.philhosoft.formattedtext.ast.DecoratedFragment;
import org.philhosoft.formattedtext.ast.Line;
import org.philhosoft.formattedtext.ast.LinkFragment;
import org.philhosoft.formattedtext.ast.MarkupVisitor;
import org.philhosoft.formattedtext.ast.TextFragment;
import org.philhosoft.formattedtext.ast.TypedBlock;

/**
 * Renders Fragments and Blocks to their textual content only.
 */
public class PlainTextVisitor implements MarkupVisitor<VisitorContext>
{
	@Override
	public void visit(DecoratedFragment fragment, VisitorContext context)
	{
		VisitorHelper.visitFragments(fragment.getFragments(), this, null, context);
	}

	@Override
	public void visit(TextFragment fragment, VisitorContext context)
	{
		context.append(fragment.getText());
	}

	@Override
	public void visit(LinkFragment fragment, VisitorContext context)
	{
		VisitorHelper.visitFragments(fragment.getFragments(), this, null, context);
		context.append(" - ").append(fragment.getUrl());
	}

	@Override
	public void visit(TypedBlock block, VisitorContext context)
	{
		VisitorHelper.visitBlocks(block.getBlocks(), this, block, context);
	}

	@Override
	public void visit(Line line, VisitorContext context)
	{
		VisitorHelper.visitFragments(line.getFragments(), this, line, context);
		context.append("\n");
	}
}
