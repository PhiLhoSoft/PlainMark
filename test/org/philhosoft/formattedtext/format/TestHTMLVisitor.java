package org.philhosoft.formattedtext.format;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.philhosoft.formattedtext.ast.Block;
import org.philhosoft.formattedtext.ast.BlockType;
import org.philhosoft.formattedtext.ast.FragmentDecoration;


public class TestHTMLVisitor
{
	@Test
	public void testFragments() throws Exception
	{
		Block document = FormattedTextExamples.buildFragments();

		HTMLVisitor visitor = new HTMLVisitor();
		ContextWithStringBuilder ctx = new ContextWithStringBuilder();
		document.accept(visitor, ctx);

//		System.out.println(ctx.asString());
		assertThat(ctx.asString()).isEqualTo(
				"<div>\n" +
				"Start of text with <em>emphasis inside</em>.<br>\n" +
				"\n<strong>Strong init, followed by</strong> plain text and <a href='http://www.example.com'>a nice <em>link</em></a><br>\n" +
				"\nBoring plain text and <em>emphasized text <strong>and even </strong><del>deleted text</del><code> fixed width text</code>.</em>\n" +
				"</div>\n");
	}

	@Test
	public void testBlocks_noLines() throws Exception
	{
		Block document = FormattedTextExamples.buildTypedBlocks(false);

		HTMLVisitor visitor = new HTMLVisitor();
		ContextWithStringBuilder ctx = new ContextWithStringBuilder();
		document.accept(visitor, ctx);

//		System.out.println(ctx.asString());
		assertThat(ctx.asString()).isEqualTo(
				"<div>\n" +
				"<h3>This is a title</h3>\n" +
				"<ul>\n" +
				"<li>Item 0</li>\n" +
				"<li>Item 1</li>\n" +
				"<li>Item 2</li>\n" +
				"</ul>\n" +
				"<pre><code>\n" +
				"Block of code\n" +
				"on several lines" +
				"\n</code></pre>\n" +
				"\n</div>\n");
	}

	@Test
	public void testBlocks_withLines() throws Exception
	{
		Block document = FormattedTextExamples.buildTypedBlocks(true);

		HTMLVisitor visitor = new HTMLVisitor();
		ContextWithStringBuilder ctx = new ContextWithStringBuilder();
		document.accept(visitor, ctx);

//		System.out.println(ctx.asString());
		assertThat(ctx.asString()).isEqualTo(
				"<div>\n" +
				"<h3>This is a title</h3>\n" +
				"Line Two<br>\n" +
				"\n<ul>\n" +
				"<li>Item 0</li>\n" +
				"<li>Item 1</li>\n" +
				"<li>Item 2</li>\n" +
				"</ul>\n" +
				"<br>\n" +
				"\n<pre><code>\n" +
				"Block of code\n" +
				"on several lines" +
				"\n</code></pre>\n" +
				"\nLast line" +
				"\n</div>\n");
	}

	@Test
	public void testMixedBlocksFragments() throws Exception
	{
		Block document = FormattedTextExamples.buildMixedBlockFragments();

		HTMLVisitor visitor = new HTMLVisitor();
		ContextWithStringBuilder ctx = new ContextWithStringBuilder();
		document.accept(visitor, ctx);

//		System.out.println(ctx.asString());
		assertThat(ctx.asString()).isEqualTo(
				"<div>\n" +
				"<h3>This is a title</h3>\n" +
				"Start of text with <em>emphasis inside</em>.<br>\n" +
                "\n" +
				"<ul>\n" +
				"<li>Item 0 - <strong>Strong fragment, followed by</strong> plain text and <a href='http://www.example.com/0'>a nice <em>link (0)</em></a></li>\n" +
				"<li>Item 1 - <strong>Strong fragment, followed by</strong> plain text and <a href='http://www.example.com/1'>a nice <em>link (1)</em></a></li>\n" +
				"<li>Item 2 - <strong>Strong fragment, followed by</strong> plain text and <a href='http://www.example.com/2'>a nice <em>link (2)</em></a></li>\n" +
				"</ul>\n" +
				"<pre><code>\n" +
				"Block of code\n" +
				"on several lines\n" +
				"</code></pre>\n" +
                "\n" +
				"Boring plain text and <em>emphasized text <strong>and even </strong><del>deleted text</del><code> fixed width text</code>.</em>\n" +
				"</div>\n");
	}

	@Test
	public void testBlocks_custom() throws Exception
	{
		Block document = FormattedTextExamples.buildMixedBlockFragments();

		HTMLVisitor visitor = new HTMLVisitor();
		FragmentDecoration.Visitor<VisitorContext> fragmentStartVisitor = new FragmentStartVisitor()
		{
			@Override
			public void visitStrong(VisitorContext context)
			{
				context.append("<b>");
			}
			@Override
			public void visitEmphasis(VisitorContext context)
			{
				context.append("<i>");
			}
		};
		FragmentDecoration.Visitor<VisitorContext> fragmentEndVisitor = new FragmentEndVisitor()
		{
			@Override
			public void visitStrong(VisitorContext context)
			{
				context.append("</b>");
			}
			@Override
			public void visitEmphasis(VisitorContext context)
			{
				context.append("</i>");
			}
		};;
		visitor.setFragmentVisitors(fragmentStartVisitor, fragmentEndVisitor);
		BlockType.Visitor<VisitorContext> blockStartVisitor = new BlockStartVisitor()
		{
			@Override
			public void visitTitle1(VisitorContext context)
			{
				context.append("<h1>");
			}
		};
		BlockType.Visitor<VisitorContext> blockEndVisitor = new BlockEndVisitor()
		{
			@Override
			public void visitTitle1(VisitorContext context)
			{
				context.append("</h1>");
			}
		};
		visitor.setBlockVisitors(blockStartVisitor, blockEndVisitor);
		ContextWithStringBuilder ctx = new ContextWithStringBuilder();
		document.accept(visitor, ctx);

//		System.out.println(ctx.asString());
		assertThat(ctx.asString()).isEqualTo(
				"<div>\n" +
				"<h1>This is a title</h1>\n" +
				"Start of text with <i>emphasis inside</i>.<br>\n" +
                "\n" +
				"<ul>\n" +
				"<li>Item 0 - <b>Strong fragment, followed by</b> plain text and <a href='http://www.example.com/0'>a nice <i>link (0)</i></a></li>\n" +
				"<li>Item 1 - <b>Strong fragment, followed by</b> plain text and <a href='http://www.example.com/1'>a nice <i>link (1)</i></a></li>\n" +
				"<li>Item 2 - <b>Strong fragment, followed by</b> plain text and <a href='http://www.example.com/2'>a nice <i>link (2)</i></a></li>\n" +
				"</ul>\n" +
				"<pre><code>\n" +
				"Block of code\n" +
				"on several lines\n" +
				"</code></pre>\n" +
                "\n" +
				"Boring plain text and <i>emphasized text <b>and even </b><del>deleted text</del><code> fixed width text</code>.</i>\n" +
				"</div>\n");
	}
}
