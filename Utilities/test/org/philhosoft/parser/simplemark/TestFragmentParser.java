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

	//## Regular cases of proper markup

	@Test
	public void testSingleDecoration_startOfLine()
	{
		StringWalker walker = new StringWalker("*Strong* text");

		Line expected = new Line();
		DecoratedFragment df = new DecoratedFragment(FragmentDecoration.STRONG);
		df.add("Strong");
		expected.add(df);
		expected.add(" text");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testSingleDecoration_middleOfLine()
	{
		StringWalker walker = new StringWalker("An _emphasized_ text");

		Line expected = new Line();
		expected.add("An ");
		DecoratedFragment df = new DecoratedFragment(FragmentDecoration.EMPHASIS);
		df.add("emphasized");
		expected.add(df);
		expected.add(" text");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testSingleDecoration_endOfLine()
	{
		StringWalker walker = new StringWalker("A text -deleted-");

		Line expected = new Line();
		expected.add("A text ");
		DecoratedFragment df = new DecoratedFragment(FragmentDecoration.DELETE);
		df.add("deleted");
		expected.add(df);

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testNestedDecorations_simple()
	{
		StringWalker walker = new StringWalker("An _emphasized and even *strong* text_.");

		Line expected = new Line();
		expected.add("An ");
		DecoratedFragment dfe = new DecoratedFragment(FragmentDecoration.EMPHASIS);
		dfe.add("emphasized and even ");
		DecoratedFragment dfs = new DecoratedFragment(FragmentDecoration.STRONG);
		dfs.add("strong");
		dfe.add(dfs);
		dfe.add(" text");
		expected.add(dfe);
		expected.add(".");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testNestedDecorations_strongEmphasis()
	{
		StringWalker walker = new StringWalker("This is *_strong emphasized_* text.");

		Line expected = new Line();
		expected.add("This is ");
		DecoratedFragment dfs = new DecoratedFragment(FragmentDecoration.STRONG);
		DecoratedFragment dfe = new DecoratedFragment(FragmentDecoration.EMPHASIS);
		dfe.add("strong emphasized");
		dfs.add(dfe);
		expected.add(dfs);
		expected.add(" text.");

//		checkExpected(expected);

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testNestedDecorations_strongEmphasisLast()
	{
		StringWalker walker = new StringWalker("This text is *_strong emphasized_*");

		Line expected = new Line();
		expected.add("This text is ");
		DecoratedFragment dfs = new DecoratedFragment(FragmentDecoration.STRONG);
		DecoratedFragment dfe = new DecoratedFragment(FragmentDecoration.EMPHASIS);
		dfe.add("strong emphasized");
		dfs.add(dfe);
		expected.add(dfs);

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testNestedDecorations_strongEmphasis2()
	{
		StringWalker walker = new StringWalker("This is *_strong emphasized_ then strong* text.");

		Line expected = new Line();
		expected.add("This is ");
		DecoratedFragment dfs = new DecoratedFragment(FragmentDecoration.STRONG);
		DecoratedFragment dfe = new DecoratedFragment(FragmentDecoration.EMPHASIS);
		dfe.add("strong emphasized");
		dfs.add(dfe);
		dfs.add(" then strong");
		expected.add(dfs);
		expected.add(" text.");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testNestedDecorations_strongEmphasis3()
	{
		StringWalker walker = new StringWalker("This is *strong and _strong emphasized_* text.");

		Line expected = new Line();
		expected.add("This is ");
		DecoratedFragment dfs = new DecoratedFragment(FragmentDecoration.STRONG);
		dfs.add("strong and ");
		DecoratedFragment dfe = new DecoratedFragment(FragmentDecoration.EMPHASIS);
		dfs.add(dfe);
		dfe.add("strong emphasized");
		expected.add(dfs);
		expected.add(" text.");

//		checkExpected(expected);

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testNestedDecorations_strongEmphasisCode()
	{
		StringWalker walker = new StringWalker("This is *strong and _strong `(code)` emphasized_ text*.");

		Line expected = new Line();
		expected.add("This is ");
		DecoratedFragment dfs = new DecoratedFragment(FragmentDecoration.STRONG);
		dfs.add("strong and ");
		DecoratedFragment dfe = new DecoratedFragment(FragmentDecoration.EMPHASIS);
		dfe.add("strong ");
		dfs.add(dfe);
		DecoratedFragment dfc = new DecoratedFragment(FragmentDecoration.CODE);
		dfc.add("(code)");
		dfe.add(dfc);
		dfe.add(" emphasized");
		dfs.add(" text");
		expected.add(dfs);
		expected.add(".");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	//## Cases where users forgot to close their markup. We stop at line end.

	@Test
	public void testSingleDecoration_unterminated()
	{
		StringWalker walker = new StringWalker("A text `fixed width");

		Line expected = new Line();
		expected.add("A text ");
		DecoratedFragment df = new DecoratedFragment(FragmentDecoration.CODE);
		df.add("fixed width");
		expected.add(df);

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testSingleDecoration_unterminatedFromStart()
	{
		StringWalker walker = new StringWalker("*Strong text");

		Line expected = new Line();
		DecoratedFragment df = new DecoratedFragment(FragmentDecoration.STRONG);
		df.add("Strong text");
		expected.add(df);

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testNestedDecorations_codeDeleted_unterminated()
	{
		StringWalker walker = new StringWalker("This is `code style and -deleted");

		Line expected = new Line();
		expected.add("This is ");
		DecoratedFragment dfs = new DecoratedFragment(FragmentDecoration.CODE);
		dfs.add("code style and ");
		DecoratedFragment dfe = new DecoratedFragment(FragmentDecoration.DELETE);
		dfs.add(dfe);
		dfe.add("deleted");
		expected.add(dfs);

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	//## Improper nesting

	@Test
	public void testNestedDecorations_badNestingTwo()
	{
		StringWalker walker = new StringWalker("This is *_strong emphasized*_ text.");

		Line expected = new Line();
		expected.add("This is ");
		DecoratedFragment dfs = new DecoratedFragment(FragmentDecoration.STRONG);
		DecoratedFragment dfe = new DecoratedFragment(FragmentDecoration.EMPHASIS);
		dfe.add("strong emphasized*"); // Strong inside strong (at higher level) is ignored
		dfs.add(dfe);
		dfs.add(" text.");
		expected.add(dfs);

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testNestedDecorations_badNestingThree()
	{
		StringWalker walker = new StringWalker("This is *_strong `emphasized*_ text`.");

		Line expected = new Line();
		expected.add("This is ");
		DecoratedFragment dfs = new DecoratedFragment(FragmentDecoration.STRONG);
		DecoratedFragment dfe = new DecoratedFragment(FragmentDecoration.EMPHASIS);
		dfe.add("strong "); // Strong inside strong is ignored
		dfs.add(dfe);
		DecoratedFragment dfc = new DecoratedFragment(FragmentDecoration.CODE);
		dfc.add("emphasized*_ text"); // Decoration inside same decoration (at higher level) is ignored
		dfe.add(dfc);
		dfe.add(".");
		expected.add(dfs);

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
