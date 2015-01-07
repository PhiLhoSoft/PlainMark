package org.philhosoft.parser.simplemark;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.philhosoft.formattedtext.ast.Block;
import org.philhosoft.formattedtext.ast.BlockType;
import org.philhosoft.formattedtext.ast.Line;
import org.philhosoft.formattedtext.ast.TextFragment;
import org.philhosoft.formattedtext.ast.TypedBlock;
import org.philhosoft.parser.StringWalker;


public class TestBlockParser
{
	@Test
	public void testPlain()
	{
		assertThat(BlockParser.parse(new StringWalker(""))).isEqualTo(new TypedBlock(BlockType.DOCUMENT));

		StringWalker walker1 = new StringWalker("Simple plain text");
		TypedBlock expected1 = new TypedBlock(BlockType.DOCUMENT);
		expected1.add(new Line(new TextFragment("Simple plain text")));
		assertThat(BlockParser.parse(walker1)).isEqualTo(expected1);

		StringWalker walker2 = new StringWalker("   Indented plain text");
		TypedBlock expected2 = new TypedBlock(BlockType.DOCUMENT);
		expected2.add(new Line(new TextFragment("Indented plain text")));
		assertThat(BlockParser.parse(walker2)).isEqualTo(expected2);
	}

	@Test
	public void testPlainMultiline_1()
	{
		StringWalker walker = new StringWalker("Two\nLines");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		expected.add(new Line(new TextFragment("Two")));
		expected.add(new Line(new TextFragment("Lines")));
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testPlainMultiline_2()
	{
		StringWalker walker = new StringWalker("Various\nMore or less long\nLines of text");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		expected.add(new Line(new TextFragment("Various")));
		expected.add(new Line(new TextFragment("More or less long")));
		expected.add(new Line(new TextFragment("Lines of text")));
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testPlainMultiline_3()
	{
		StringWalker walker = new StringWalker("With trailing\nNewline at end\n");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		expected.add(new Line(new TextFragment("With trailing")));
		expected.add(new Line(new TextFragment("Newline at end")));
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testPlainMultiline_4()
	{
		StringWalker walker = new StringWalker("With trailing\nNewlines at end\n\n");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		expected.add(new Line(new TextFragment("With trailing")));
		expected.add(new Line(new TextFragment("Newlines at end")));
		expected.add(new Line());
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testTitle1_notTitle1()
	{
		StringWalker walker = new StringWalker("#Almost a title line");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		expected.add("#Almost a title line");
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testTitle1_notTitle2()
	{
		StringWalker walker = new StringWalker("  #Almost a title line");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		expected.add("#Almost a title line");
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testTitle1_notTitle3()
	{
		StringWalker walker = new StringWalker("   : # Almost a title line");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		expected.add(": # Almost a title line");
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testTitle1_notTitle_escaped1()
	{
		StringWalker walker = new StringWalker("~# Almost a title line");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		expected.add("# Almost a title line");
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testTitle1_notTitle_escaped2()
	{
		StringWalker walker = new StringWalker("~~# Almost a title line");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		expected.add("~# Almost a title line");
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testTitle1_notTitle_escaped3()
	{
		StringWalker walker = new StringWalker("~#Almost a title line");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		expected.add("~#Almost a title line");
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testTitle1_single_simple()
	{
		StringWalker walker = new StringWalker("# A title line");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		TypedBlock title = new TypedBlock(BlockType.TITLE1);
		title.add("A title line");
		expected.add(title);
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testTitle1_single_indented1()
	{
		StringWalker walker = new StringWalker("   # A title line");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		TypedBlock title = new TypedBlock(BlockType.TITLE1);
		title.add("A title line");
		expected.add(title);
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testTitle1_single_indented2()
	{
		StringWalker walker = new StringWalker("   #    A title line");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		TypedBlock title = new TypedBlock(BlockType.TITLE1);
		title.add("A title line");
		expected.add(title);
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testTitle2_single()
	{
		StringWalker walker = new StringWalker("## A title line of second level");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		TypedBlock title = new TypedBlock(BlockType.TITLE2);
		title.add("A title line of second level");
		expected.add(title);
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testTitle3_single()
	{
		StringWalker walker = new StringWalker("### A title line of third level");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		TypedBlock title = new TypedBlock(BlockType.TITLE3);
		title.add("A title line of third level");
		expected.add(title);
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testTitlesAndLines()
	{
		StringWalker walker = new StringWalker("# Title 1\n" +
				"Simple line\n" +
				"## Title 2\n" +
				"Plain line\n" +
				"### Title 3\n" +
				"Boring line\n");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		TypedBlock title1 = new TypedBlock(BlockType.TITLE1);
		title1.add("Title 1");
		Line line1 = new Line(new TextFragment("Simple line"));
		TypedBlock title2 = new TypedBlock(BlockType.TITLE2);
		title2.add("Title 2");
		Line line2 = new Line(new TextFragment("Plain line"));
		TypedBlock title3 = new TypedBlock(BlockType.TITLE3);
		title3.add("Title 3");
		Line line3 = new Line(new TextFragment("Boring line"));

		expected.add(title1);
		expected.add(line1);
		expected.add(title2);
		expected.add(line2);
		expected.add(title3);
		expected.add(line3);
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testNotTitle4()
	{
		StringWalker walker = new StringWalker("#### A title line of fourth level (not implemented!)");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		expected.add("#### A title line of fourth level (not implemented!)");
		assertThat(result).isEqualTo(expected);
	}
}
