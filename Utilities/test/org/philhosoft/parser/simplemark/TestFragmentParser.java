package org.philhosoft.parser.simplemark;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Ignore;
import org.junit.Test;

import org.philhosoft.formattedtext.ast.DecoratedFragment;
import org.philhosoft.formattedtext.ast.FragmentDecoration;
import org.philhosoft.formattedtext.ast.Line;
import org.philhosoft.formattedtext.ast.LinkFragment;
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
	public void testSingleDecoration_quote()
	{
		// Verifying proper context checking
		StringWalker walker = new StringWalker("Quoting: \"_Quoted Fragment_\"");

		Line expected = new Line();
		expected.add("Quoting: \"");
		DecoratedFragment df = new DecoratedFragment(FragmentDecoration.EMPHASIS);
		df.add("Quoted Fragment");
		expected.add(df);
		expected.add("\"");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testSingleDecoration_escaping()
	{
		StringWalker walker = new StringWalker("This is not ~*strong~*");

		Line expected = new Line();
		expected.add("This is not *strong*");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testSingleDecoration_escapingItself()
	{
		StringWalker walker = new StringWalker("This is a ~~ tilde");

		Line expected = new Line();
		expected.add("This is a ~ tilde");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testSingleDecoration_escapingAtEnd()
	{
		StringWalker walker = new StringWalker("This is *strong~* and still~");

		Line expected = new Line();
		expected.add("This is ");
		DecoratedFragment df = new DecoratedFragment(FragmentDecoration.STRONG);
		df.add("strong* and still");
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
	public void testSingleDecoration_unterminated_bug()
	{
		StringWalker walker = new StringWalker("A text `fixed width ");

		Line expected = new Line();
		expected.add("A text ");
		DecoratedFragment df = new DecoratedFragment(FragmentDecoration.CODE);
		df.add("fixed width ");
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


	//## Cases where the context of the decoration doesn't allow the decoration to be triggered

	@Test
	public void testDeactivatedDecoration_starting_noSpaces1()
	{
		StringWalker walker = new StringWalker("This is not*strong at all");

		Line expected = new Line();
		expected.add("This is not*strong at all");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testDeactivatedDecoration_starting_noSpaces2()
	{
		StringWalker walker = new StringWalker("This is in-line and var_name");

		Line expected = new Line();
		expected.add("This is in-line and var_name");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testDeactivatedDecoration_starting_spaces()
	{
		StringWalker walker = new StringWalker("Isolated * star or - dash");

		Line expected = new Line();
		expected.add("Isolated * star or - dash");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testDeactivatedDecoration_starting_expression()
	{
		StringWalker walker = new StringWalker("x*y_2-7");

		Line expected = new Line();
		expected.add("x*y_2-7");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testDeactivatedDecoration_ending_noSpaces()
	{
		StringWalker walker = new StringWalker("This is *strong not*ending at all");

		Line expected = new Line();
		expected.add("This is ");
		DecoratedFragment dfs = new DecoratedFragment(FragmentDecoration.STRONG);
		dfs.add("strong not*ending at all");
		expected.add(dfs);

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testDeactivatedDecoration_ending_spaces()
	{
		StringWalker walker = new StringWalker("This is *strong not * ending at all");

		Line expected = new Line();
		expected.add("This is ");
		DecoratedFragment dfs = new DecoratedFragment(FragmentDecoration.STRONG);
		dfs.add("strong not * ending at all");
		expected.add(dfs);

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}


	//## Implicit URL detection or explicit URL markup

	@Test
	public void testURL_implicit_notAURL()
	{
		StringWalker walker = new StringWalker("Using the http:// schema (or https://)");

		Line expected = new Line();
		expected.add("Using the http:// schema (or https://)");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testURL_implicit()
	{
		StringWalker walker = new StringWalker("http://www.example.com to become a URL");

		Line expected = new Line();
		LinkFragment lf = new LinkFragment("www.example.com", "http://www.example.com");
		expected.add(lf);
		expected.add(" to become a URL");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testURL_implicit_long()
	{
		StringWalker walker = new StringWalker("The http://www.example.com/foo/index.html#fragment becomes a URL");

		Line expected = new Line();
		expected.add("The ");
		LinkFragment lf = new LinkFragment("www.example.com/foo/index.html…", "http://www.example.com/foo/index.html#fragment");
		expected.add(lf);
		expected.add(" becomes a URL");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testURL_implicit_longer()
	{
		StringWalker walker = new StringWalker("URL in *bold http://www.example.com/foo-bar/~name/?a=sp+ace&b=%49 text*");

		Line expected = new Line();
		expected.add("URL in ");
		DecoratedFragment df = new DecoratedFragment(FragmentDecoration.STRONG);
		df.add("bold ");
		LinkFragment lf = new LinkFragment("www.example.com/foo-bar/~name/?a=sp+…", "http://www.example.com/foo-bar/~name/?a=sp+ace&b=%49");
		df.add(lf);
		df.add(" text");
		expected.add(df);

		assertThat(FragmentParser.parse(walker, 36)).isEqualTo(expected);
	}

	@Test
	public void testURL_implicit_longerShortened()
	{
		StringWalker walker = new StringWalker("URL at end: http://www.example.com/foo-bar/~name/somewhere.html");

		Line expected = new Line();
		expected.add("URL at end: ");
		LinkFragment lf = new LinkFragment("www.example.com/foo-…", "http://www.example.com/foo-bar/~name/somewhere.html");
		expected.add(lf);

		assertThat(FragmentParser.parse(walker, 20)).isEqualTo(expected);
	}

	@Test
	public void testURL_implicit_longerNoLimit()
	{
		StringWalker walker = new StringWalker("http://www.example.com/foo-bar/~name/path/somewhere.html#insideLink");

		Line expected = new Line();
		LinkFragment lf = new LinkFragment("www.example.com/foo-bar/~name/path/somewhere.html#insideLink", "http://www.example.com/foo-bar/~name/path/somewhere.html#insideLink");
		expected.add(lf);

		assertThat(FragmentParser.parse(walker, 0)).isEqualTo(expected);
	}

	@Ignore
	@Test
	public void testURL_explicit()
	{
		StringWalker walker = new StringWalker("I [link](http://www.example.com) to a URL");

		Line expected = new Line();
		LinkFragment lf = new LinkFragment("link", "http://www.example.com");
		expected.add(lf);
		expected.add(" to a URL");

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
