package org.philhosoft.parser.simplemark;

import org.philhosoft.formattedtext.ast.Block;
import org.philhosoft.formattedtext.ast.MarkupVisitor;
import org.philhosoft.formattedtext.format.ContextWithStringBuilder;
import org.philhosoft.formattedtext.format.HTMLVisitor;
import org.philhosoft.formattedtext.format.PlainTextVisitor;
import org.philhosoft.formattedtext.format.VisitorContext;
import org.philhosoft.parser.StringWalker;


public class SimpleMark
{
	private SimpleMark()
	{
	}

	public static String convertToHTML(String markupText)
	{
		HTMLVisitor visitor = new HTMLVisitor();
		return convertWithVisitor(markupText, visitor);
	}

	public static String convertToPlainText(String markupText)
	{
		PlainTextVisitor visitor = new PlainTextVisitor();
		return convertWithVisitor(markupText, visitor);
	}

	private static String convertWithVisitor(String markupText, MarkupVisitor<VisitorContext> visitor)
	{
		StringWalker walker = new StringWalker(markupText);

		Block block = BlockParser.parse(walker);

		ContextWithStringBuilder context = new ContextWithStringBuilder();
		block.accept(visitor, context);

		return context.asString();
	}
}
