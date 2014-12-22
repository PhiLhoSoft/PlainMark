package org.philhosoft.formattedtext.format;

import org.philhosoft.formattedtext.ast.BlockType;

public class BlockStartVisitor implements BlockType.Visitor<StringBuilder>
{
	@Override
	public void visitDocument(StringBuilder output)
	{
		output.append("<div>");
	}
	@Override
	public void visitParagraph(StringBuilder output)
	{
		output.append("<p>");
	}
	@Override
	public void visitTitle1(StringBuilder output)
	{
		output.append("<h3>");
	}
	@Override
	public void visitTitle2(StringBuilder output)
	{
		output.append("<h4>");
	}
	@Override
	public void visitTitle3(StringBuilder output)
	{
		output.append("<h5>");
	}
	@Override
	public void visitUnorderedList(StringBuilder output)
	{
		output.append("<ul>");
	}
	@Override
	public void visitOrderedList(StringBuilder output)
	{
		output.append("<ol>");
	}
	@Override
	public void visitListItem(StringBuilder output)
	{
		output.append("<li>");
	}
	@Override
	public void visitCode(StringBuilder output)
	{
		output.append("<pre><code>");
	}
}
