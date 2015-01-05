package org.philhosoft.formattedtext.format;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.philhosoft.formattedtext.ast.Block;


public class TestPlainTextVisitor
{
	@Test
	public void testFragments() throws Exception
	{
		Block document = FormattedTextExamples.buildFragments();

		PlainTextVisitor visitor = new PlainTextVisitor();
		ContextWithStringBuilder ctx = new ContextWithStringBuilder();
		document.accept(visitor, ctx);

//		System.out.println(ctx.toString());
		assertThat(ctx.asString()).isEqualTo("Start of text with emphasis inside.\n" +
				"Strong init, followed by plain text and a nice link - http://www.example.com\n" +
				"Boring plain text and emphasized text and even deleted text fixed width text.\n");
	}

	@Test
	public void testBlocks() throws Exception
	{
		Block document = FormattedTextExamples.buildTypedBlocks(true);

		PlainTextVisitor visitor = new PlainTextVisitor();
		ContextWithStringBuilder ctx = new ContextWithStringBuilder();
		document.accept(visitor, ctx);

//		System.out.println(ctx.toString());
		assertThat(ctx.asString()).isEqualTo("This is a title\n" +
				"Line Two\n" +
				"Item 0\n" +
				"Item 1\n" +
				"Item 2\n" +
				"\n" +
				"Block of code\n" +
				"on several lines\n" +
				"Last line\n");
	}
}
