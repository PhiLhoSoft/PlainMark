package org.philhosoft.parser.simplemark;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.philhosoft.formattedtext.ast.DecoratedFragment;
import org.philhosoft.formattedtext.ast.FragmentDecoration;
import org.philhosoft.formattedtext.ast.Line;
import org.philhosoft.formattedtext.ast.TextFragment;
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
}
