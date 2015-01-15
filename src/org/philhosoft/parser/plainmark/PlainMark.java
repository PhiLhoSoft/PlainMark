package org.philhosoft.parser.plainmark;

import org.philhosoft.formattedtext.ast.Block;
import org.philhosoft.formattedtext.ast.MarkupVisitor;
import org.philhosoft.formattedtext.format.ContextWithStringBuilder;
import org.philhosoft.formattedtext.format.HTMLVisitor;
import org.philhosoft.formattedtext.format.PlainTextVisitor;
import org.philhosoft.formattedtext.format.VisitorContext;
import org.philhosoft.parser.StringWalker;


public class PlainMark
{
	private ParsingParameters parsingParameters = new ParsingParameters();
	private MarkupVisitor<VisitorContext> visitor;

	public PlainMark()
	{
	}

	/**
	 * Convenience shortcut for quick conversion to HTML.
	 */
	public static String convertToHTML(String markupText)
	{
		return new PlainMark().setVisitor(new HTMLVisitor()).convert(markupText);
	}

	/**
	 * Convenience shortcut for quick, default conversion to plain text.
	 */
	public static String convertToPlainText(String markupText)
	{
		return new PlainMark().setVisitor(new PlainTextVisitor()).convert(markupText);
	}

	public PlainMark setVisitor(MarkupVisitor<VisitorContext> visitor)
	{
		this.visitor = visitor;
		return this;
	}
	public PlainMark setParsingParameters(ParsingParameters parsingParameters)
	{
		this.parsingParameters = parsingParameters;
		return this;
	}

	public String convert(String markupText)
	{
		StringWalker walker = new StringWalker(markupText);

		Block block = BlockParser.parse(walker, parsingParameters);

		ContextWithStringBuilder context = new ContextWithStringBuilder();
		block.accept(visitor, context);

		return context.asString();
	}
}
