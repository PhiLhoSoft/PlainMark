package org.philhosoft.formattedtext.format;

import org.philhosoft.formattedtext.ast.FragmentDecoration;

public class HTMLFragmentStartVisitor implements FragmentDecoration.Visitor<VisitorContext>
{
	@Override
	public void visitStrong(VisitorContext context)
	{
		context.append("<strong>");
	}
	@Override
	public void visitEmphasis(VisitorContext context)
	{
		context.append("<em>");
	}
	@Override
	public void visitDelete(VisitorContext context)
	{
		context.append("<del>");
	}
	@Override
	public void visitCode(VisitorContext context)
	{
		context.append("<code>");
	}
}
