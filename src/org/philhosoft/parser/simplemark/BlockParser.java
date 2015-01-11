package org.philhosoft.parser.simplemark;

import org.philhosoft.collection.SimpleStack;
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
	private SimpleStack<TypedBlock> stack = new SimpleStack<TypedBlock>();
	private boolean inCodeBlock;

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
			if (walker.match(parsingParameters.getCodeBlockSign()))
			{
				handleCodeBlockSign();
				continue;
			}
			if (inCodeBlock)
			{
				addCurrentLine();
				continue;
			}

			walker.skipSpaces();
			if (walker.atLineEnd())
			{
				handleEmptyLine();
			}
			else
			{
				handleLine();
			}
		}
		popStack();

		return document;
	}

	private void handleCodeBlockSign()
	{
		inCodeBlock = !inCodeBlock;
		if (inCodeBlock)
		{
			popPreviousBlockIfNeeded(BlockType.CODE);
			stack.push(new TypedBlock(BlockType.CODE));
		}
		else
		{
			document.add(stack.pop());
		}
		walker.goToNextLine();
	}

	private void handleEmptyLine()
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

	private void handleLine()
	{
		BlockType blockType = checkBlockSignWithEscape();
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
				addListIfNeeded(blockType);
				TypedBlock block = new TypedBlock(blockType);
				block.add(line);
				if (blockType == BlockType.LIST_ITEM_BULLET || blockType == BlockType.LIST_ITEM_NUMBER)
				{
					stack.peek().add(block);
				}
				else
				{
					stack.push(block);
				}
			}
		}
	}

	private BlockType checkBlockSignWithEscape()
	{
		if (walker.current() == parsingParameters.getEscapeSign())
		{
			String blockSign = checkBlockType(1);
			if (blockSign != null || walker.next() == parsingParameters.getEscapeSign())
			{
				// Skip this escape (really escaping something)
				walker.forward();
			}
			// Otherwise, the escape sign is kept literally
			// And we are not on a block sign
			return null;
		}

		String blockSign = checkBlockType(0);
		BlockType blockType = parsingParameters.getBlockType(blockSign);
		processBlockSign(blockSign);
		return blockType;
	}

	private String checkBlockType(int offset)
	{
		for (String blockSign : parsingParameters.getBlockTypeSigns())
		{
			if (walker.matchAt(offset, blockSign))
				return blockSign;
		}

		return null;
	}

	private void processBlockSign(String blockSign)
	{
		if (blockSign == null)
			return;
		walker.forward(blockSign.length());
		walker.skipSpaces();
	}

	private void popPreviousBlockIfNeeded(BlockType blockType)
	{
		BlockType previousType = getPreviousType();
		if (previousType == null)
			return;
		if (isTitleBlock(previousType) && blockType != previousType)
		{
			// We don't support sub-blocks in titles
			document.add(stack.pop());
		}
		else if (previousType == BlockType.PARAGRAPH && blockType != BlockType.PARAGRAPH)
		{
			// We don't accept other blocks in paragraphs
			document.add(stack.pop());
		}
		else if ((previousType == BlockType.UNORDERED_LIST || previousType == BlockType.ORDERED_LIST) &&
				(blockType != BlockType.LIST_ITEM_BULLET && blockType != BlockType.LIST_ITEM_NUMBER))
		{
			// Currently, we don't put blocks in list items
			document.add(stack.pop());
		}
	}

	private void addListIfNeeded(BlockType blockType)
	{
		if (blockType == BlockType.LIST_ITEM_BULLET)
		{
			addUnorderedListIfNeeded();
		}
		else if (blockType == BlockType.LIST_ITEM_NUMBER)
		{
			addOrderedListIfNeeded();
		}
	}

	private void addUnorderedListIfNeeded()
	{
		TypedBlock ul = findInStack(BlockType.UNORDERED_LIST);
		if (ul == null)
		{
			stack.push(new TypedBlock(BlockType.UNORDERED_LIST));
		}
		else
		{
//				return; // Already in a list
		}
	}
	private void addOrderedListIfNeeded()
	{
		TypedBlock ol = findInStack(BlockType.ORDERED_LIST);
		if (ol == null)
		{
			stack.push(new TypedBlock(BlockType.ORDERED_LIST));
		}
		else
		{
//				return; // Already in a list
		}
	}

	private TypedBlock findInStack(BlockType blockType)
	{
		for (TypedBlock block : stack)
		{
			if (block.getType() == blockType)
				return block;
		}
		return null;
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

	/**
	 * Raw add, for code blocks.
	 */
	private void addCurrentLine()
	{
		StringBuilder sb = new StringBuilder();
		do
		{
			if (!walker.atLineEnd())
			{
				sb.append(walker.current());
			}
			walker.forward();
		} while (!walker.atLineStart() && walker.hasMore());
		Line line = new Line(sb.toString());
		addLine(line);
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
