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
			if (walker.atLineEnd())
			{
				manageEmptyLine();
			}
			else
			{
				manageLine();
			}
		}
		popStack();

		return document;
	}

	private void manageEmptyLine()
	{
		TypedBlock block = stack.peek();
		if (block != null)
		{
			// End of current block
			document.add(stack.pop());
		}
		// Skip it
		walker.forward();
	}

	private void manageLine()
	{
		BlockType blockType = checkBlockTypeWithEscape();
		Line line = FragmentParser.parse(walker, parsingParameters);
		if (blockType == null)
		{
			// Plain line
			popPreviousBlockIfNeeded(BlockType.PARAGRAPH);
			addLine(line);
		}
		else
		{
			if (isSameTitle(blockType, getPreviousType()))
			{
				stack.peek().add(line);
			}
			else
			{
				popPreviousBlockIfNeeded(blockType);
				TypedBlock block = new TypedBlock(blockType);
				block.add(line);
				stack.push(block);
			}
		}
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
		BlockType previousType = getPreviousType();
		if (previousType == null)
			return;
		if (isTitleBlock(blockType) &&
				// We don't support titles in paragraphs
				(previousType == BlockType.PARAGRAPH ||
				// And don't support sub-blocks in titles
				isTitleBlock(previousType) && blockType != previousType))
		{
			document.add(stack.pop());
		}
		else if (blockType == BlockType.PARAGRAPH &&
				// We don't accept other blocks in paragraphs
				previousType != BlockType.PARAGRAPH)
		{
			document.add(stack.pop());
		}
	}

	private BlockType getPreviousType()
	{
		TypedBlock previousBlock = stack.peek();
		if (previousBlock == null)
			return null;
		BlockType previousType = previousBlock.getType();
		return previousType;
	}

	private void addParagraph(Line line)
	{
		TypedBlock block = new TypedBlock(BlockType.PARAGRAPH);
		block.add(line);
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

	private boolean isTitleBlock(BlockType blockType)
	{
		return blockType == BlockType.TITLE1 || blockType == BlockType.TITLE2 || blockType == BlockType.TITLE3;
	}
	private boolean isSameTitle(BlockType blockType, BlockType previousType)
	{
		return isTitleBlock(blockType) && isTitleBlock(previousType) && blockType == previousType;
	}

	private void popStack()
	{
		while (stack.size() > 0)
		{
			document.add(stack.pop());
		}
	}
}
