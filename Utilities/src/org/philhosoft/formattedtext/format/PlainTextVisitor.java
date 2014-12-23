package org.philhosoft.formattedtext.format;

import org.philhosoft.formattedtext.ast.DecoratedFragment;
import org.philhosoft.formattedtext.ast.Line;
import org.philhosoft.formattedtext.ast.LinkFragment;
import org.philhosoft.formattedtext.ast.MarkupVisitor;
import org.philhosoft.formattedtext.ast.TextFragment;
import org.philhosoft.formattedtext.ast.TypedBlock;

public class PlainTextVisitor implements MarkupVisitor<VisitorContext>
{
	@Override
	public void visit(DecoratedFragment fragment, VisitorContext context)
	{
		VisitorHelper.visitFragments(fragment.getFragments(), this, fragment.getDecoration().name(), context);
	}

	@Override
	public void visit(TextFragment fragment, VisitorContext context)
	{
		context.append(fragment.getText());
	}

	@Override
	public void visit(LinkFragment fragment, VisitorContext context)
	{
		VisitorHelper.visitFragments(fragment.getFragments(), this, "Link", context);
		context.append(" - ").append(fragment.getUrl());
	}

	@Override
	public void visit(TypedBlock block, VisitorContext context)
	{
		VisitorHelper.visitBlocks(block.getBlocks(), this, block.getType().name(), context);
	}

	@Override
	public void visit(Line line, VisitorContext context)
	{
		VisitorHelper.visitFragments(line.getFragments(), this, "Line", context);
		context.append("\n");
	}
}
