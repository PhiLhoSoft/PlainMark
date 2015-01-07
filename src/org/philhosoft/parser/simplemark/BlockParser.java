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
	private StringWalker walker;
	private ParsingParameters parsingParameters;
	private TypedBlock document = new TypedBlock(BlockType.DOCUMENT);
	private Deque<TypedBlock> stack = new ArrayDeque<TypedBlock>();
//	private StringBuilder outputString = new StringBuilder();

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
			throw new IllegalStateException("Parsing must start at the beginning of a line");

		BlockParser parser = new BlockParser(walker, parsingParameters);
		return parser.parse();
	}

	private Block parse()
	{
		while (walker.hasMore())
		{
			walker.skipSpaces();
			BlockType blockType = checkBlockTypeWithEscape();
			Line line = FragmentParser.parse(walker, parsingParameters);
			if (blockType == null)
			{
				// Plain line
				addLine(line);
			}
			else
			{
				TypedBlock block = new TypedBlock(blockType);
				block.add(line);
				popPreviousBlockIfNeeded(blockType);
				addBlock(block);
			}
		}

		return document;
	}

	private BlockType checkBlockTypeWithEscape()
	{
		if (walker.current() == parsingParameters.getEscapeSign())
		{
			String prefix = checkBlockType(1);
			if (prefix != null || walker.next() == parsingParameters.getEscapeSign())
			{
				// Skip this escape (really escaping something)
				walker.forward();
			}
			// Otherwise, the escape sign is kept literally
			// And we are not on a block prefix
			return null;
		}

		String prefix = checkBlockType(0);
		BlockType blockType = parsingParameters.getBlockType(prefix);
		processBlockType(prefix, blockType);
		return blockType;
	}

	private String checkBlockType(int offset)
	{
		for (String prefix : parsingParameters.getBlockTypePrefixes())
		{
			if (walker.matchAt(offset, prefix))
				return prefix;
		}

		return null;
	}

	private void processBlockType(String prefix, BlockType type)
	{
		if (prefix == null)
			return;
		walker.forward(prefix.length());
		walker.skipSpaces();
	}

	private void popPreviousBlockIfNeeded(BlockType blockType)
	{
		if (blockType == BlockType.TITLE1 || blockType == BlockType.TITLE2 || blockType == BlockType.TITLE3)
		{
			TypedBlock currentBlock = stack.peek();
			if (currentBlock != null && currentBlock.getType() == BlockType.PARAGRAPH)
			{
				// We don't support titles in paragraphs
				stack.pop();
			}
		}
	}

	private void addParagraph(Line line)
	{
		TypedBlock block = new TypedBlock(BlockType.PARAGRAPH);
		block.add(line);
		document.add(block);
		stack.push(block);
	}

	private void addLine(Line line)
	{
		TypedBlock block = stack.peek();
		if (block == null)
		{
			addParagraph(line);
		}
		else
		{
			block.add(line);
		}
	}

	private void addBlock(TypedBlock block)
	{
		TypedBlock currentBlock = stack.peek();
		if (currentBlock == null)
		{
			document.add(block);
		}
		else
		{
			currentBlock.add(block);
		}
	}
}
