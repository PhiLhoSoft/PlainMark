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
}
