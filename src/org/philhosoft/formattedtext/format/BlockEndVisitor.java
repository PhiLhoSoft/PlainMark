package org.philhosoft.formattedtext.format;

import org.philhosoft.formattedtext.ast.BlockType;

public class BlockEndVisitor implements BlockType.Visitor<VisitorContext>
{
	@Override
	public void visitDocument(VisitorContext context)
	{
		context.append("</div>");
	}
	@Override
	public void visitParagraph(VisitorContext context)
	{
		context.append("</p>");
	}
	@Override
	public void visitTitle1(VisitorContext context)
	{
		context.append("</h3>");
	}
	@Override
	public void visitTitle2(VisitorContext context)
	{
		context.append("</h4>");
	}
	@Override
	public void visitTitle3(VisitorContext context)
	{
		context.append("</h5>");
	}
	@Override
	public void visitUnorderedList(VisitorContext context)
	{
		context.append("</ul>");
	}
	@Override
	public void visitOrderedList(VisitorContext context)
	{
		context.append("</ol>");
	}
	@Override
	public void visitListItem(VisitorContext context)
	{
		context.append("</li>");
	}
	@Override
	public void visitCode(VisitorContext context)
	{
		context.append("</code></pre>");
	}
}
