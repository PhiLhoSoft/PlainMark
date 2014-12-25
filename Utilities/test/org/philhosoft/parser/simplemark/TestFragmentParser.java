package org.philhosoft.parser.simplemark;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.philhosoft.formattedtext.ast.DecoratedFragment;
import org.philhosoft.formattedtext.ast.FragmentDecoration;
import org.philhosoft.formattedtext.ast.Line;
import org.philhosoft.formattedtext.ast.TextFragment;
import org.philhosoft.formattedtext.format.ContextWithStringBuilder;
import org.philhosoft.formattedtext.format.HTMLVisitor;
import org.philhosoft.parser.StringWalker;


public class TestFragmentParser
{
	@Test
	public void testPlain()
	{
		assertThat(FragmentParser.parse(new StringWalker(""))).isEqualTo(
				new Line());
		assertThat(FragmentParser.parse(new StringWalker("Simple plain text"))).isEqualTo(
				new Line(new TextFragment("Simple plain text")));
	}

	@Test
	public void testMultiline()
	{
		// Fragment parser stops on a line end
		StringWalker walker = new StringWalker("Two\nLines");
		assertThat(FragmentParser.parse(walker)).isEqualTo(
				new Line(new TextFragment("Two")));
		assertThat(walker.atLineStart()).isTrue();
		assertThat(walker.current()).isEqualTo('L');
	}

	@Test
	public void testSingleDecoration_startOfLine()
	{
		StringWalker walker = new StringWalker("*Strong* text");

		Line expected = new Line();
		DecoratedFragment df = new DecoratedFragment(FragmentDecoration.STRONG);
		df.add(new TextFragment("Strong"));
		expected.add(df);
		expected.add(new TextFragment(" text"));

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testSingleDecoration_middleOfLine()
	{
		StringWalker walker = new StringWalker("An _emphased_ text");

		Line expected = new Line();
		expected.add(new TextFragment("An "));
		DecoratedFragment df = new DecoratedFragment(FragmentDecoration.EMPHASIS);
		df.add(new TextFragment("emphased"));
		expected.add(df);
		expected.add(new TextFragment(" text"));

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testSingleDecoration_endOfLine()
	{
		StringWalker walker = new StringWalker("A text -deleted-");

		Line expected = new Line();
		expected.add(new TextFragment("A text "));
		DecoratedFragment df = new DecoratedFragment(FragmentDecoration.DELETE);
		df.add(new TextFragment("deleted"));
		expected.add(df);

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testSingleDecoration_unfinished1()
	{
		StringWalker walker = new StringWalker("A text `fixed width");

		Line expected = new Line();
		expected.add(new TextFragment("A text "));
		DecoratedFragment df = new DecoratedFragment(FragmentDecoration.CODE);
		df.add(new TextFragment("fixed width"));
		expected.add(df);

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testSingleDecoration_unfinished2()
	{
		StringWalker walker = new StringWalker("*Strong text");

		Line expected = new Line();
		DecoratedFragment df = new DecoratedFragment(FragmentDecoration.STRONG);
		df.add(new TextFragment("Strong text"));
		expected.add(df);

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testNestedDecorations_simple()
	{
		StringWalker walker = new StringWalker("An _emphased and even *strong* text_.");

		Line expected = new Line();
		expected.add(new TextFragment("An "));
		DecoratedFragment dfe = new DecoratedFragment(FragmentDecoration.EMPHASIS);
		dfe.add(new TextFragment("emphased and even "));
		DecoratedFragment dfs = new DecoratedFragment(FragmentDecoration.STRONG);
		dfs.add(new TextFragment("strong"));
		dfe.add(dfs);
		dfe.add(new TextFragment(" text"));
		expected.add(dfe);
		expected.add(new TextFragment("."));

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testNestedDecorations_strongEmphasis()
	{
		StringWalker walker = new StringWalker("This is *_strong emphased_* text.");

		Line expected = new Line();
		expected.add(new TextFragment("This is "));
		DecoratedFragment dfs = new DecoratedFragment(FragmentDecoration.STRONG);
		DecoratedFragment dfe = new DecoratedFragment(FragmentDecoration.EMPHASIS);
		dfe.add(new TextFragment("strong emphased"));
		dfs.add(dfe);
		expected.add(dfs);
		expected.add(new TextFragment(" text."));

//		checkExpected(expected);

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testNestedDecorations_strongEmphasisLast()
	{
		StringWalker walker = new StringWalker("This text is *_strong emphased_*");

		Line expected = new Line();
		expected.add(new TextFragment("This text is "));
		DecoratedFragment dfs = new DecoratedFragment(FragmentDecoration.STRONG);
		DecoratedFragment dfe = new DecoratedFragment(FragmentDecoration.EMPHASIS);
		dfe.add(new TextFragment("strong emphased"));
		dfs.add(dfe);
		expected.add(dfs);

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testNestedDecorations_strongEmphasis2()
	{
		StringWalker walker = new StringWalker("This is *_strong emphased_ then strong* text.");

		Line expected = new Line();
		expected.add(new TextFragment("This is "));
		DecoratedFragment dfs = new DecoratedFragment(FragmentDecoration.STRONG);
		DecoratedFragment dfe = new DecoratedFragment(FragmentDecoration.EMPHASIS);
		dfe.add(new TextFragment("strong emphased"));
		dfs.add(dfe);
		dfs.add(new TextFragment(" then strong"));
		expected.add(dfs);
		expected.add(new TextFragment(" text."));

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testNestedDecorations_strongEmphasis3()
	{
		StringWalker walker = new StringWalker("This is *strong and _strong emphased_* text.");

		Line expected = new Line();
		expected.add(new TextFragment("This is "));
		DecoratedFragment dfs = new DecoratedFragment(FragmentDecoration.STRONG);
		dfs.add(new TextFragment("strong and "));
		DecoratedFragment dfe = new DecoratedFragment(FragmentDecoration.EMPHASIS);
		dfs.add(dfe);
		dfe.add(new TextFragment("strong emphased"));
		expected.add(dfs);
		expected.add(new TextFragment(" text."));

//		checkExpected(expected);

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testNestedDecorations_strongEmphasisCode()
	{
		StringWalker walker = new StringWalker("This is *strong and _strong `(code)` emphased_ text*.");

		Line expected = new Line();
		expected.add(new TextFragment("This is "));
		DecoratedFragment dfs = new DecoratedFragment(FragmentDecoration.STRONG);
		dfs.add(new TextFragment("strong and "));
		DecoratedFragment dfe = new DecoratedFragment(FragmentDecoration.EMPHASIS);
		dfe.add(new TextFragment("strong "));
		dfs.add(dfe);
		DecoratedFragment dfc = new DecoratedFragment(FragmentDecoration.CODE);
		dfc.add(new TextFragment("(code)"));
		dfe.add(dfc);
		dfe.add(new TextFragment(" emphased"));
		dfs.add(new TextFragment(" text"));
		expected.add(dfs);
		expected.add(new TextFragment("."));

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@SuppressWarnings("unused")
	private void checkExpected(Line expected)
	{
		HTMLVisitor visitor = new HTMLVisitor();
		ContextWithStringBuilder ctx = new ContextWithStringBuilder();
		expected.accept(visitor, ctx);
		System.out.println(ctx.asString());
	}
}
