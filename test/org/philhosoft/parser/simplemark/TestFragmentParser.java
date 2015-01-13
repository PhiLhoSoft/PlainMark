package org.philhosoft.parser.simplemark;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Ignore;
import org.junit.Test;

import org.philhosoft.formattedtext.ast.DecoratedFragment;
import org.philhosoft.formattedtext.ast.FragmentDecoration;
import org.philhosoft.formattedtext.ast.Line;
import org.philhosoft.formattedtext.ast.LinkFragment;
import org.philhosoft.formattedtext.format.ContextWithStringBuilder;
import org.philhosoft.formattedtext.format.HTMLVisitor;
import org.philhosoft.parser.StringWalker;


public class TestFragmentParser
{
	@Test
	public void testPlain()
	{
		assertThat(FragmentParser.parse(new StringWalker(""))).isEqualTo(new Line());
		assertThat(FragmentParser.parse(new StringWalker("Simple plain text"))).isEqualTo(new Line("Simple plain text"));
	}

	@Test
	public void testMultiline()
	{
		// Fragment parser stops on a line end
		StringWalker walker = new StringWalker("Two\nLines");
		assertThat(FragmentParser.parse(walker)).isEqualTo(new Line("Two"));
		assertThat(walker.atLineStart()).isTrue();
		assertThat(walker.current()).isEqualTo('L');
	}


	//## Regular cases of proper markup

	@Test
	public void testSingleDecoration_startOfLine()
	{
		StringWalker walker = new StringWalker("*Strong* text");

		Line expected = new Line();
		DecoratedFragment df = new DecoratedFragment(FragmentDecoration.STRONG, "Strong");
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
		DecoratedFragment df = new DecoratedFragment(FragmentDecoration.EMPHASIS, "emphasized");
		expected.add(df);
		expected.add(" text");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testSingleDecoration_endOfLine()
	{
		StringWalker walker = new StringWalker("A text -deleted-");

		Line expected = new Line("A text ");
		DecoratedFragment df = new DecoratedFragment(FragmentDecoration.DELETE, "deleted");
		expected.add(df);

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testSingleDecoration_code()
	{
		StringWalker walker = new StringWalker("A text with `code` in it, even `*` markup signs");

		Line expected = new Line("A text with ");
		DecoratedFragment dfc1 = new DecoratedFragment(FragmentDecoration.CODE, "code");
		expected.add(dfc1);
		expected.add(" in it, even ");
		DecoratedFragment dfc2 = new DecoratedFragment(FragmentDecoration.CODE, "*");
		expected.add(dfc2);
		expected.add(" markup signs");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testSingleDecoration_quote()
	{
		// Verifying proper context checking
		StringWalker walker = new StringWalker("Quoting: \"_Quoted Fragment_\"");

		Line expected = new Line("Quoting: \"");
		DecoratedFragment df = new DecoratedFragment(FragmentDecoration.EMPHASIS, "Quoted Fragment");
		expected.add(df);
		expected.add("\"");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Ignore // TODO
	@Test
	public void testSingleDecoration_doubleCode()
	{
		StringWalker walker = new StringWalker("Empty code `` is kept literal");

		Line expected = new Line("Empty code `` is kept literal");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Ignore // TODO
	@Test
	public void testSingleDecoration_doubleStrong()
	{
		StringWalker walker = new StringWalker("Empty strong ** is kept literal");

		Line expected = new Line("Empty strong ** is kept literal");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testSingleDecoration_escaping()
	{
		StringWalker walker = new StringWalker("This is not ~*strong*");

		Line expected = new Line("This is not *strong*");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testSingleDecoration_tilde()
	{
		StringWalker walker = new StringWalker("This is ~ a ~tilde");

		Line expected = new Line("This is ~ a ~tilde");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testSingleDecoration_escapingItself()
	{
		StringWalker walker = new StringWalker("This is a ~~ tilde");

		Line expected = new Line("This is a ~ tilde");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testSingleDecoration_escapingAtEnd()
	{
		StringWalker walker = new StringWalker("This is *not strong~* and still~");

		Line expected = new Line("This is ");
		expected.add("*not strong* and still~");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testNestedDecorations_simple()
	{
		StringWalker walker = new StringWalker("An _emphasized and even *strong* text_.");

		Line expected = new Line("An ");
		DecoratedFragment dfe = new DecoratedFragment(FragmentDecoration.EMPHASIS, "emphasized and even ");
		DecoratedFragment dfs = new DecoratedFragment(FragmentDecoration.STRONG, "strong");
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

		Line expected = new Line("This is ");
		DecoratedFragment dfs = new DecoratedFragment(FragmentDecoration.STRONG);
		DecoratedFragment dfe = new DecoratedFragment(FragmentDecoration.EMPHASIS, "strong emphasized");
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

		Line expected = new Line("This text is ");
		DecoratedFragment dfs = new DecoratedFragment(FragmentDecoration.STRONG);
		DecoratedFragment dfe = new DecoratedFragment(FragmentDecoration.EMPHASIS, "strong emphasized");
		dfs.add(dfe);
		expected.add(dfs);

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testNestedDecorations_strongEmphasis2()
	{
		StringWalker walker = new StringWalker("This is *_strong emphasized_ then strong* text.");

		Line expected = new Line("This is ");
		DecoratedFragment dfs = new DecoratedFragment(FragmentDecoration.STRONG);
		DecoratedFragment dfe = new DecoratedFragment(FragmentDecoration.EMPHASIS, "strong emphasized");
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

		Line expected = new Line("This is ");
		DecoratedFragment dfs = new DecoratedFragment(FragmentDecoration.STRONG, "strong and ");
		DecoratedFragment dfe = new DecoratedFragment(FragmentDecoration.EMPHASIS, "strong emphasized");
		dfs.add(dfe);
		expected.add(dfs);
		expected.add(" text.");

//		checkExpected(expected);

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testNestedDecorations_strongEmphasisCode()
	{
		StringWalker walker = new StringWalker("This is *strong and _strong `(code)` emphasized_ text*.");

		Line expected = new Line("This is ");
		DecoratedFragment dfs = new DecoratedFragment(FragmentDecoration.STRONG, "strong and ");
		DecoratedFragment dfe = new DecoratedFragment(FragmentDecoration.EMPHASIS, "strong ");
		dfs.add(dfe);
		DecoratedFragment dfc = new DecoratedFragment(FragmentDecoration.CODE, "(code)");
		dfe.add(dfc);
		dfe.add(" emphasized");
		dfs.add(" text");
		expected.add(dfs);
		expected.add(".");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}


	//## Cases where users forgot to close their markup. We disable the single sign.

	@Test
	public void testSingleDecoration_unterminated()
	{
		StringWalker walker = new StringWalker("A text without `fixed width");

		Line expected = new Line("A text without ");
		expected.add("`fixed width");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testSingleDecoration_unterminated_bug()
	{
		StringWalker walker = new StringWalker("A text without `fixed width ");

		Line expected = new Line("A text without ");
		expected.add("`fixed width ");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testSingleDecoration_unterminatedFromStart()
	{
		StringWalker walker = new StringWalker("*Almost strong text");

		Line expected = new Line("*Almost strong text");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testNestedDecorations_emphasisDeleted_unterminated()
	{
		StringWalker walker = new StringWalker("This could be _emphasis style and -deleted");

		Line expected = new Line("This could be ");
		expected.add("_emphasis style and ");
		expected.add("-deleted");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testNestedDecorations_code_unterminatedAndDeleted()
	{
		StringWalker walker = new StringWalker("This could be `code style and -deleted- text");

		Line expected = new Line("This could be ");
		expected.add("`code style and -deleted- text");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testNestedDecorations_codeStrongEmphasis_unterminatedAndDeletedWithURL()
	{
		StringWalker walker = new StringWalker("This is not -deleted *strong style _and -deleted http://foo.bar URL- text");

		Line expected = new Line("This is not ");
		expected.add("-deleted ");
		expected.add("*strong style ");
		expected.add("_and ");
		DecoratedFragment dfd = new DecoratedFragment(FragmentDecoration.DELETE, "deleted ");
		LinkFragment lf = new LinkFragment("foo.bar", "http://foo.bar");
		dfd.add(lf);
		dfd.add(" URL");
		expected.add(dfd);
		expected.add(" text");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testNestedDecorations_deletedStrong_unterminatedAndDeletedEmphasisWithURL()
	{
		StringWalker walker = new StringWalker("This is not -deleted *and strong style but this is -deleted http://foo.bar _URL_- text");

		Line expected = new Line("This is not ");
		expected.add("-deleted ");
		expected.add("*and strong style but this is ");
		DecoratedFragment dfd = new DecoratedFragment(FragmentDecoration.DELETE, "deleted ");
		LinkFragment lf = new LinkFragment("foo.bar", "http://foo.bar");
		dfd.add(lf);
		dfd.add(" ");
		dfd.add(new DecoratedFragment(FragmentDecoration.EMPHASIS, "URL"));
		expected.add(dfd);
		expected.add(" text");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testNestedDecorations_codeStrong_unterminatedAndDeletedStrongWithURL()
	{
		StringWalker walker = new StringWalker("This is not _emphasis *style and -deleted http://foo.bar *URL*- text");

		Line expected = new Line("This is not ");
		expected.add("_emphasis ");
		expected.add("*style and ");
		DecoratedFragment dfd = new DecoratedFragment(FragmentDecoration.DELETE, "deleted ");
		LinkFragment lf = new LinkFragment("foo.bar", "http://foo.bar");
		dfd.add(lf);
		dfd.add(" ");
		dfd.add(new DecoratedFragment(FragmentDecoration.STRONG, "URL"));
		expected.add(dfd);
		expected.add(" text");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testNestedDecorations_deleted_unterminatedWithURL()
	{
		StringWalker walker = new StringWalker("This could be -deleted http://foo.bar URL");

		Line expected = new Line("This could be ");
		expected.add("-deleted ");
		LinkFragment lf = new LinkFragment("foo.bar", "http://foo.bar");
		expected.add(lf);
		expected.add(" URL");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}


	//## Improper nesting

	@Test
	public void testNestedDecorations_badNestingTwo()
	{
		StringWalker walker = new StringWalker("This could have been *_strong emphasized*_ text.");

		Line expected = new Line("This could have been ");
		expected.add("*");
		DecoratedFragment dfe = new DecoratedFragment(FragmentDecoration.EMPHASIS, "strong emphasized*"); // Strong inside strong (at higher level) is ignored
		expected.add(dfe);
		expected.add(" text.");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testNestedDecorations_badNestingThree()
	{
		StringWalker walker = new StringWalker("This could have been *_strong `emphasized*_ text`.");

		Line expected = new Line("This could have been ");
		expected.add("*");
		expected.add("_strong "); // Strong inside strong is ignored
		DecoratedFragment dfc = new DecoratedFragment(FragmentDecoration.CODE, "emphasized*_ text"); // Decoration inside same decoration (at higher level) is ignored
		expected.add(dfc);
		expected.add(".");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}


	//## Cases where the context of the decoration doesn't allow the decoration to be triggered

	@Test
	public void testDeactivatedDecoration_starting_noSpaces1()
	{
		StringWalker walker = new StringWalker("This is not*strong at all");

		Line expected = new Line("This is not*strong at all");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testDeactivatedDecoration_starting_noSpaces2()
	{
		StringWalker walker = new StringWalker("This is in-line and var_name");

		Line expected = new Line("This is in-line and var_name");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testDeactivatedDecoration_starting_spaces()
	{
		StringWalker walker = new StringWalker("Isolated * star or - dash");

		Line expected = new Line("Isolated * star or - dash");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testDeactivatedDecoration_starting_expression()
	{
		StringWalker walker = new StringWalker("x*y_2-7");

		Line expected = new Line("x*y_2-7");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testDeactivatedDecoration_ending_noSpaces()
	{
		StringWalker walker = new StringWalker("This is *strong not*ending at all");

		Line expected = new Line("This is ");
		expected.add("*strong not*ending at all");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testDeactivatedDecoration_ending_spaces()
	{
		StringWalker walker = new StringWalker("This is *strong not * ending at all");

		Line expected = new Line("This is ");
		expected.add("*strong not * ending at all");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testDeactivatedDecoration_inCodeFragment()
	{
		StringWalker walker = new StringWalker("This `is *not strong* at` all");

		Line expected = new Line("This ");
		DecoratedFragment df = new DecoratedFragment(FragmentDecoration.CODE, "is *not strong* at");
		expected.add(df);
		expected.add(" all");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}


	//## Implicit URL detection or explicit URL markup

	@Test
	public void testURL_implicit_notAURL()
	{
		StringWalker walker = new StringWalker("Using the http:// schema (or https://)");

		Line expected = new Line("Using the http:// schema (or https://)");

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

		Line expected = new Line("The ");
		LinkFragment lf = new LinkFragment("www.example.com/foo/index.html…", "http://www.example.com/foo/index.html#fragment");
		expected.add(lf);
		expected.add(" becomes a URL");

		assertThat(FragmentParser.parse(walker)).isEqualTo(expected);
	}

	@Test
	public void testURL_implicit_longer()
	{
		StringWalker walker = new StringWalker("URL in *bold http://www.example.com/foo-bar/~name/?a=sp+ace&b=%49 text*");

		Line expected = new Line("URL in ");
		DecoratedFragment df = new DecoratedFragment(FragmentDecoration.STRONG, "bold ");
		LinkFragment lf = new LinkFragment("www.example.com/foo-bar/~name/?a=sp+…", "http://www.example.com/foo-bar/~name/?a=sp+ace&b=%49");
		df.add(lf);
		df.add(" text");
		expected.add(df);

		ParsingParameters parsingParameters = new ParsingParameters();
		parsingParameters.setMaxLinkLength(36);

		assertThat(FragmentParser.parse(walker, parsingParameters)).isEqualTo(expected);
	}

	@Test
	public void testURL_implicit_longerShortened()
	{
		StringWalker walker = new StringWalker("URL at end: http://www.example.com/foo-bar/~name/somewhere.html");

		Line expected = new Line("URL at end: ");
		LinkFragment lf = new LinkFragment("www.example.com/foo-…", "http://www.example.com/foo-bar/~name/somewhere.html");
		expected.add(lf);

		ParsingParameters parsingParameters = new ParsingParameters();
		parsingParameters.setMaxLinkLength(20);

		assertThat(FragmentParser.parse(walker, parsingParameters)).isEqualTo(expected);
	}

	@Test
	public void testURL_implicit_longerNoLimit()
	{
		StringWalker walker = new StringWalker("http://www.example.com/foo-bar/~name/path/somewhere.html#insideLink");

		Line expected = new Line();
		LinkFragment lf = new LinkFragment("www.example.com/foo-bar/~name/path/somewhere.html#insideLink", "http://www.example.com/foo-bar/~name/path/somewhere.html#insideLink");
		expected.add(lf);

		ParsingParameters parsingParameters = new ParsingParameters();
		parsingParameters.setMaxLinkLength(0);

		assertThat(FragmentParser.parse(walker, parsingParameters)).isEqualTo(expected);
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
