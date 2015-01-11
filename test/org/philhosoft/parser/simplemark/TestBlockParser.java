package org.philhosoft.parser.simplemark;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.philhosoft.formattedtext.ast.Block;
import org.philhosoft.formattedtext.ast.BlockType;
import org.philhosoft.formattedtext.ast.DecoratedFragment;
import org.philhosoft.formattedtext.ast.FragmentDecoration;
import org.philhosoft.formattedtext.ast.Line;
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
		expected1.add(createParagraph("Simple plain text"));
		assertThat(BlockParser.parse(walker1)).isEqualTo(expected1);

		StringWalker walker2 = new StringWalker("   Indented plain text");
		TypedBlock expected2 = new TypedBlock(BlockType.DOCUMENT);
		expected2.add(createParagraph("Indented plain text"));
		assertThat(BlockParser.parse(walker2)).isEqualTo(expected2);
	}

	@Test
	public void testPlainMultiline_1()
	{
		StringWalker walker = new StringWalker("Two\nLines");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		expected.add(createParagraph("Two", "Lines"));
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testPlainMultiline_2()
	{
		StringWalker walker = new StringWalker("Various\nMore or less long\nLines of text");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		expected.add(createParagraph("Various", "More or less long", "Lines of text"));
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testPlainMultiline_3()
	{
		StringWalker walker = new StringWalker("With trailing\nNewline at end\n");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		expected.add(createParagraph("With trailing", "Newline at end"));
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testPlainMultiline_4()
	{
		StringWalker walker = new StringWalker("With trailing\nNewlines\n\n");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		expected.add(createParagraph("With trailing", "Newlines"));
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testPlainMultiline_5()
	{
		StringWalker walker = new StringWalker("\n\n\n\nWith leading and trailing\n\n\nNewlines\n\n\n");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		expected.add(createParagraph("With leading and trailing"));
		expected.add(createParagraph("Newlines"));
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testPlainMultiParagraph()
	{
		StringWalker walker = new StringWalker("First paragraph\nwith line break.\n\nAnd another paragraph");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		expected.add(createParagraph("First paragraph", "with line break."));
		expected.add(createParagraph("And another paragraph"));
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testTitle1_notTitle1()
	{
		StringWalker walker = new StringWalker("#Almost a title line");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		expected.add(createParagraph("#Almost a title line"));
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testTitle1_notTitle2()
	{
		StringWalker walker = new StringWalker("  #Almost a title line");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		expected.add(createParagraph("#Almost a title line"));
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testTitle1_notTitle3()
	{
		StringWalker walker = new StringWalker("   : # Almost a title line");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		expected.add(createParagraph(": # Almost a title line"));
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testTitle1_notTitle_escaped1()
	{
		StringWalker walker = new StringWalker("~# Almost a title line");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		expected.add(createParagraph("# Almost a title line"));
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testTitle1_notTitle_escaped2()
	{
		StringWalker walker = new StringWalker("~~# Almost a title line");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		expected.add(createParagraph("~# Almost a title line"));
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testTitle1_notTitle_escaped3()
	{
		StringWalker walker = new StringWalker("~#Almost a title line");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		expected.add(createParagraph("~#Almost a title line"));
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
	public void testTitle1_twoLines()
	{
		StringWalker walker = new StringWalker("# A title line\n# On two lines");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		TypedBlock title = new TypedBlock(BlockType.TITLE1);
		title.add("A title line");
		title.add("On two lines");
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
	public void testTitles()
	{
		StringWalker walker = new StringWalker("# Title 1\n" +
				"## Title 2\n" +
				"### Title 3\n" +
				"Boring line\n");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		TypedBlock title1 = new TypedBlock(BlockType.TITLE1);
		title1.add("Title 1");
		TypedBlock title2 = new TypedBlock(BlockType.TITLE2);
		title2.add("Title 2");
		TypedBlock title3 = new TypedBlock(BlockType.TITLE3);
		title3.add("Title 3");
		Block paragraph = createParagraph("Boring line");

		expected.add(title1);
		expected.add(title2);
		expected.add(title3);
		expected.add(paragraph);
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testTitlesAndLines1()
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
		Block paragraph1 = createParagraph("Simple line");
		TypedBlock title2 = new TypedBlock(BlockType.TITLE2);
		title2.add("Title 2");
		Block paragraph2 = createParagraph("Plain line");
		TypedBlock title3 = new TypedBlock(BlockType.TITLE3);
		title3.add("Title 3");
		Block paragraph3 = createParagraph("Boring line");

		expected.add(title1);
		expected.add(paragraph1);
		expected.add(title2);
		expected.add(paragraph2);
		expected.add(title3);
		expected.add(paragraph3);
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testTitlesAndLines2()
	{
		StringWalker walker = new StringWalker("# Title 1\n\n" +
				"Simple line\n\n" +
				"## Title 2\n\n" +
				"Plain line\n\n" +
				"Other paragraph\nwith line break.\n\n" +
				"### Title 3\n\n" +
				"Boring line\n\n");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		TypedBlock title1 = new TypedBlock(BlockType.TITLE1);
		title1.add("Title 1");
		Block paragraph1 = createParagraph("Simple line");
		TypedBlock title2 = new TypedBlock(BlockType.TITLE2);
		title2.add("Title 2");
		Block paragraph2 = createParagraph("Plain line");
		Block paragraph3 = createParagraph("Other paragraph", "with line break.");
		TypedBlock title3 = new TypedBlock(BlockType.TITLE3);
		title3.add("Title 3");
		Block paragraph4 = createParagraph("Boring line");

		expected.add(title1);
		expected.add(paragraph1);
		expected.add(title2);
		expected.add(paragraph2);
		expected.add(paragraph3);
		expected.add(title3);
		expected.add(paragraph4);
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testNotTitle4()
	{
		StringWalker walker = new StringWalker("#### A title line of fourth level (not implemented!)");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		expected.add(createParagraph("#### A title line of fourth level (not implemented!)"));
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testCodeBlock_empty()
	{
		StringWalker walker = new StringWalker("```\n```\n");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		TypedBlock code = new TypedBlock(BlockType.CODE);
		expected.add(code);
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testCodeBlock_regular()
	{
		StringWalker walker = new StringWalker(
				"Plain text before\n" +
				"```\n" +
				"# include <stdio>\n" +
				"\n" +
				"int main()\n" +
				"{\n" +
				"  return 0;  \n" +
				"}\n\n" +
				"// Last *line*\n\n" +
				"```\n" +
				"And plain text after");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		expected.add(createParagraph("Plain text before"));
		TypedBlock code = new TypedBlock(BlockType.CODE);
		code.add("# include <stdio>");
		code.add("");
		code.add("int main()");
		code.add("{");
		code.add("  return 0;  ");
		code.add("}");
		code.add("");
		code.add("// Last *line*");
		code.add("");
		expected.add(code);
		expected.add(createParagraph("And plain text after"));
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testCodeBlock_escaped()
	{
		StringWalker walker = new StringWalker(
				"Plain text before\n" +
				"~```\n" +
				"// Comment *line*\n\n" +
				"~```\n" +
				"And plain text after");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		TypedBlock p1 = new TypedBlock(BlockType.PARAGRAPH);
		Line l1 = new Line("Plain text before");
		p1.add(l1);
		Line l2 = new Line("`");
		DecoratedFragment df1 = new DecoratedFragment(FragmentDecoration.CODE);
		l2.add(df1);
		p1.add(l2);
		Line l3 = new Line("// Comment ");
		DecoratedFragment df2 = new DecoratedFragment(FragmentDecoration.STRONG, "line");
		l3.add(df2);
		p1.add(l3);
		TypedBlock p2 = new TypedBlock(BlockType.PARAGRAPH);
		p2.add(l2);
		Line l4 = new Line("And plain text after");
		p2.add(l4);
		expected.add(p1);
		expected.add(p2);
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testListStar_single()
	{
		StringWalker walker = new StringWalker("* A mono-entry line");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		TypedBlock list = new TypedBlock(BlockType.UNORDERED_LIST);
		TypedBlock listItem1 = new TypedBlock(BlockType.LIST_ITEM_BULLET);
		listItem1.add("A mono-entry line");
		list.add(listItem1);
		expected.add(list);
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testListStar_multiple()
	{
		StringWalker walker = new StringWalker("* A mono-entry line\n* And another\n* Last");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		TypedBlock list = new TypedBlock(BlockType.UNORDERED_LIST);
		TypedBlock listItem1 = new TypedBlock(BlockType.LIST_ITEM_BULLET);
		listItem1.add("A mono-entry line");
		list.add(listItem1);
		TypedBlock listItem2 = new TypedBlock(BlockType.LIST_ITEM_BULLET);
		listItem2.add("And another");
		list.add(listItem2);
		TypedBlock listItem3 = new TypedBlock(BlockType.LIST_ITEM_BULLET);
		listItem3.add("Last");
		list.add(listItem3);
		expected.add(list);
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testListStar_multipleAndParagraph()
	{
		StringWalker walker = new StringWalker("* A mono-entry line\n* And another\n\nLast line");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		TypedBlock list = new TypedBlock(BlockType.UNORDERED_LIST);
		TypedBlock listItem1 = new TypedBlock(BlockType.LIST_ITEM_BULLET);
		listItem1.add("A mono-entry line");
		list.add(listItem1);
		TypedBlock listItem2 = new TypedBlock(BlockType.LIST_ITEM_BULLET);
		listItem2.add("And another");
		list.add(listItem2);
		expected.add(list);
		TypedBlock paragraph = new TypedBlock(BlockType.PARAGRAPH);
		paragraph.add("Last line");
		expected.add(paragraph);
		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void testListStar_multipleWithParagraph()
	{
		StringWalker walker = new StringWalker("* A mono-entry line\n* And another\nLast line");

		Block result = BlockParser.parse(walker);

		TypedBlock expected = new TypedBlock(BlockType.DOCUMENT);
		TypedBlock list = new TypedBlock(BlockType.UNORDERED_LIST);
		TypedBlock listItem1 = new TypedBlock(BlockType.LIST_ITEM_BULLET);
		listItem1.add("A mono-entry line");
		list.add(listItem1);
		TypedBlock listItem2 = new TypedBlock(BlockType.LIST_ITEM_BULLET);
		listItem2.add("And another");
		list.add(listItem2);
		expected.add(list);
		TypedBlock paragraph = new TypedBlock(BlockType.PARAGRAPH);
		paragraph.add("Last line");
		expected.add(paragraph);
		assertThat(result).isEqualTo(expected);
	}

	private TypedBlock createParagraph(String... texts)
	{
		TypedBlock block = new TypedBlock(BlockType.PARAGRAPH);
		for (String text : texts)
		{
			if (text.isEmpty())
			{
				block.add(new Line());
			}
			else
			{
				block.add(text);
			}
		}
		return block;
	}
}
