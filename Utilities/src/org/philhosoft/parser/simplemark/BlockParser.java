package org.philhosoft.parser.simplemark;

import org.philhosoft.formattedtext.ast.Block;
import org.philhosoft.formattedtext.ast.BlockType;
import org.philhosoft.formattedtext.ast.TypedBlock;
import org.philhosoft.parser.StringWalker;

/**
 * Parser for a text with markup.
 */
public class BlockParser
{
	private StringWalker walker;

	private BlockParser(String markup)
	{
		walker = new StringWalker(markup);
	}

	public static Block parse(String markup)
	{
		if (markup == null || markup.isEmpty())
			return new TypedBlock(BlockType.DOCUMENT);

		BlockParser parser = new BlockParser(markup);
		return parser.parse();
	}

	private Block parse()
	{
		Block block = new TypedBlock(BlockType.DOCUMENT);
		while (walker.hasMore())
		{
			walker.forward();
		}

		return block;
	}
}
