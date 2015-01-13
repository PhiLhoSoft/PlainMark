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
			TypedBlock parent = fetchParent(blockType);
			if (parent != null)
			{
				Block child = line;
				if (blockType == BlockType.LIST_ITEM_BULLET || blockType == BlockType.LIST_ITEM_NUMBER)
				{
					child = new TypedBlock(blockType, line);
				}
				parent.add(child);
			}
			else
			{
				popPreviousBlockIfNeeded(blockType);
				TypedBlock block = new TypedBlock(blockType, line);
				stack.push(block);
			}
		}
	}

	private BlockType checkBlockSignWithEscape()
	{
		if (walker.current() == parsingParameters.getEscapeSign())
		{
			String blockSign = checkBlockSign(1);
			if (blockSign != null || walker.next() == parsingParameters.getEscapeSign())
			{
				// Skip this escape (really escaping something)
				walker.forward();
			}
			// Otherwise, the escape sign is kept literally
			// And we are not on a block sign
			return null;
		}

		String blockSign = checkBlockSign(0);
		BlockType blockType = processBlockSign(blockSign);
		return blockType;
	}

	private String checkBlockSign(int offset)
	{
		for (String blockSign : parsingParameters.getBlockTypeSigns())
		{
			if (walker.matchAt(offset, blockSign))
			{
				if (StringWalker.isWhitespace(walker.charAt(offset + blockSign.length(), '\0')))
					return blockSign;
			}
		}
		return checkNumberedListItem(offset);
	}

	private String checkNumberedListItem(int offset)
	{
		if (!StringWalker.isDigit(walker.charAt(offset, '\0')))
			return null;

		String digits = "0";
		int dn = 1;
		while (StringWalker.isDigit(walker.charAt(offset + dn, '\0')))
		{
			dn++;
			digits += "0"; // I don't expect more than 2 or 3 digits...
		}
		if (parsingParameters.isOrderedListSuffix(walker.charAt(offset + dn, '\0')) &&
				StringWalker.isWhitespace(walker.charAt(offset + dn + 1, '\0')))
			return digits + ".";

		return null;
	}

	private BlockType processBlockSign(String blockSign)
	{
		if (blockSign == null)
			return null;
		BlockType blockType = blockSign.startsWith("0") ? BlockType.LIST_ITEM_NUMBER : parsingParameters.getBlockType(blockSign);
		walker.forward(blockSign.length() + 1); // +1 for mandatory whitespace after the sign
		walker.skipSpaces();
		return blockType;
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

	private TypedBlock fetchParent(BlockType blockType)
	{
		if (isSameTitle(blockType, getPreviousType()))
			return stack.peek();

		if (blockType == BlockType.LIST_ITEM_BULLET)
			return fetchListParent(BlockType.UNORDERED_LIST);

		if (blockType == BlockType.LIST_ITEM_NUMBER)
			return fetchListParent(BlockType.ORDERED_LIST);

		return null; // No parent
	}

	private TypedBlock fetchListParent(BlockType listType)
	{
		TypedBlock list = findInStack(listType);
		if (list != null)
			return list;

		popPreviousBlockIfNeeded(listType);
		list = new TypedBlock(listType);
		stack.push(list);
		return list;

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
		TypedBlock block = new TypedBlock(BlockType.PARAGRAPH, line);
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
