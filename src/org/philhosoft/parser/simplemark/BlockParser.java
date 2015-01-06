package org.philhosoft.parser.simplemark;

import java.util.ArrayDeque;
import java.util.Deque;

import org.philhosoft.formattedtext.ast.Block;
import org.philhosoft.formattedtext.ast.BlockType;
import org.philhosoft.formattedtext.ast.Line;
import org.philhosoft.formattedtext.ast.TypedBlock;
import org.philhosoft.parser.StringWalker;

/**
 * Parser for a text with markup.
 */
public class BlockParser
{
	private static final String TITLE_1 = "# ";
	private static final String TITLE_2 = "## ";
	private static final String TITLE_3 = "### ";
	private static final String CODE = "```";
	private static final String LIST_ITEM_U1 = "* ";
	private static final String LIST_ITEM_U2 = "- ";
	private static final String LIST_ITEM_U3 = "+ ";
	private static final String LIST_ITEM_ORDERED = ".";

	private StringWalker walker;
	private TypedBlock document = new TypedBlock(BlockType.DOCUMENT);
	private Deque<TypedBlock> stack = new ArrayDeque<TypedBlock>();
	private StringBuilder outputString = new StringBuilder();

	private BlockParser(StringWalker walker)
	{
		this.walker = walker;
	}

	public static Block parse(StringWalker walker)
	{
		if (walker == null || !walker.atLineStart())
			throw new IllegalStateException("Parsiing must start at the the beginning of a line");

		BlockParser parser = new BlockParser(walker);
		return parser.parse();
	}

	private Block parse()
	{
		while (walker.hasMore())
		{
			walker.skipSpaces();
			BlockType blockType = checkBlockType();
			if (blockType == null)
			{
				// Plain line
				Line line = FragmentParser.parse(walker);
				add(line);
			}
		}

		return document;
	}

	private BlockType checkBlockType()
	{
		return null;
	}

	private void add(Line line)
	{
		TypedBlock block = stack.peek();
		if (block == null)
		{
			document.add(line);
		}
		else
		{
			block.add(line);
		}
	}
}
