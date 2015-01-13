package org.philhosoft.formattedtext.format;

import org.philhosoft.formattedtext.ast.BlockType;

public class HTMLBlockStartVisitor implements BlockType.Visitor<VisitorContext>
{
	@Override
	public void visitDocument(VisitorContext context)
	{
		context.append("<div>");
	}
	@Override
	public void visitParagraph(VisitorContext context)
	{
		context.append("<p>");
	}
	@Override
	public void visitTitle1(VisitorContext context)
	{
		context.append("<h4>");
	}
	@Override
	public void visitTitle2(VisitorContext context)
	{
		context.append("<h5>");
	}
	@Override
	public void visitTitle3(VisitorContext context)
	{
		context.append("<h6>");
	}
	@Override
	public void visitUnorderedList(VisitorContext context)
	{
		context.append("<ul>\n");
	}
	@Override
	public void visitOrderedList(VisitorContext context)
	{
		context.append("<ol>\n");
	}
	@Override
	public void visitListItemBullet(VisitorContext context)
	{
		context.append("<li>");
	}
	@Override
	public void visitListItemNumber(VisitorContext context)
	{
		context.append("<li>");
	}
	@Override
	public void visitCode(VisitorContext context)
	{
		context.append("<pre><code>");
	}
}
