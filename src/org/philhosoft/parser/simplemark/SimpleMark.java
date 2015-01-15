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
	private ParsingParameters parsingParameters = new ParsingParameters();
	private MarkupVisitor<VisitorContext> visitor;

	public SimpleMark()
	{
	}

	/**
	 * Convenience shortcut for quick conversion to HTML.
	 */
	public static String convertToHTML(String markupText)
	{
		return new SimpleMark().setVisitor(new HTMLVisitor()).convert(markupText);
	}

	/**
	 * Convenience shortcut for quick, default conversion to plain text.
	 */
	public static String convertToPlainText(String markupText)
	{
		return new SimpleMark().setVisitor(new PlainTextVisitor()).convert(markupText);
	}

	public SimpleMark setVisitor(MarkupVisitor<VisitorContext> visitor)
	{
		this.visitor = visitor;
		return this;
	}
	public SimpleMark setParsingParameters(ParsingParameters parsingParameters)
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
