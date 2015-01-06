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
	private static final String TITLE_1_PREFIX = "# ";
	private static final String TITLE_2_PREFIX = "## ";
	private static final String TITLE_3_PREFIX = "### ";
	private static final String CODE_SIGN = "```";
	private static final String LIST_ITEM_U1_PREFIX = "* ";
	private static final String LIST_ITEM_U2_PREFIX = "- ";
	private static final String LIST_ITEM_U3_PREFIX = "+ ";
	private static final String LIST_ITEM_ORDERED_SUFFIX = ".";

	private StringWalker walker;
	private ParsingParameters parsingParameters;
	private TypedBlock document = new TypedBlock(BlockType.DOCUMENT);
	private Deque<TypedBlock> stack = new ArrayDeque<TypedBlock>();
	private StringBuilder outputString = new StringBuilder();

	private BlockParser(StringWalker walker, ParsingParameters parsingParameters)
	{
		this.walker = walker;
		this.parsingParameters = parsingParameters;
	}

	public static Block parse(StringWalker walker)
	{
		return parse(walker, new ParsingParameters());
	}
	public static Block parse(StringWalker walker, ParsingParameters parsingParameters)
	{
		if (walker == null || !walker.atLineStart())
			throw new IllegalStateException("Parsiing must start at the the beginning of a line");

		BlockParser parser = new BlockParser(walker, parsingParameters);
		return parser.parse();
	}

	private Block parse()
	{
		while (walker.hasMore())
		{
			walker.skipSpaces();
			BlockType blockType = checkBlockTypeWithEscape();
			if (blockType == null)
			{
				// Plain line
				Line line = FragmentParser.parse(walker);
				add(line);
			}
			else
			{
				TypedBlock block = new TypedBlock(blockType);
				stack.add(block);
				Line line = FragmentParser.parse(walker);
				add(line);
				if (blockType == BlockType.TITLE1)
				{
					document.add(stack.pop());
				}
			}
		}

		return document;
	}

	private BlockType checkBlockTypeWithEscape()
	{
		if (walker.current() == parsingParameters.getEscapeSign())
		{
			walker.forward();
			BlockType blockType = checkBlockType();
			if (blockType != null || walker.current() == parsingParameters.getEscapeSign())
			{
				// Skip it
			}
			return null;
		}
		BlockType blockType = checkBlockType();
		processBlockType(blockType);
		return blockType;
	}

	private BlockType checkBlockType()
	{
		if (walker.match(TITLE_1_PREFIX))
		{
			return BlockType.TITLE1;
		}
		return null;
	}

	private void processBlockType(BlockType type)
	{
		if (type == BlockType.TITLE1)
		{
			walker.forward(TITLE_1_PREFIX.length());
		}
		walker.skipSpaces();
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
