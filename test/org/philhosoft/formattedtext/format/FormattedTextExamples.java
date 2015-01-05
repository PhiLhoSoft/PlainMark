package org.philhosoft.formattedtext.format;

import org.philhosoft.formattedtext.ast.Block;
import org.philhosoft.formattedtext.ast.BlockType;
import org.philhosoft.formattedtext.ast.DecoratedFragment;
import org.philhosoft.formattedtext.ast.FragmentDecoration;
import org.philhosoft.formattedtext.ast.Line;
import org.philhosoft.formattedtext.ast.LinkFragment;
import org.philhosoft.formattedtext.ast.TextFragment;
import org.philhosoft.formattedtext.ast.TypedBlock;


public class FormattedTextExamples
{
	private FormattedTextExamples()
	{
	}

	public static Block buildFragments()
	{
		// Build the AST the way it will be by the (scannerless) parser

		// First line
		Line firstLine = new Line();
		firstLine.add(new TextFragment("Start of text with "));
		firstLine.add(new DecoratedFragment(FragmentDecoration.EMPHASIS, "emphasis inside"));
		firstLine.add(new TextFragment("."));

		// Meet new start of line
		TypedBlock document = new TypedBlock(BlockType.DOCUMENT);
		document.add(firstLine);

		// Second line, starting bold
		Line secondLine = new Line();
		document.add(secondLine);
		secondLine.add(new DecoratedFragment(FragmentDecoration.STRONG, "Strong init, followed by"));
		secondLine.add(new TextFragment(" plain text and "));

		LinkFragment link = new LinkFragment("a nice ", "http://www.example.com");
		DecoratedFragment linkE = new DecoratedFragment(FragmentDecoration.EMPHASIS, "link");
		link.add(linkE);
		secondLine.add(link);

		// Meet new start of line
		Line lastLine = new Line();
		document.add(lastLine);
		lastLine.add(new TextFragment("Boring plain text and "));

		DecoratedFragment df = new DecoratedFragment(FragmentDecoration.EMPHASIS, "emphasized text ");
		df.add(new DecoratedFragment(FragmentDecoration.STRONG, "and even "));
		df.add(new DecoratedFragment(FragmentDecoration.DELETE, "deleted text"));
		df.add(new DecoratedFragment(FragmentDecoration.CODE, " fixed width text"));
		df.add(new TextFragment("."));
		lastLine.add(df);

		return document;
	}

	public static Block buildTypedBlocks(boolean withSimpleLines)
	{
		// First line, title
		TypedBlock firstBlock = new TypedBlock(BlockType.TITLE1);
		Line title = new Line();
		title.add(new TextFragment("This is a title"));
		firstBlock.add(title);

		// Meet new start of line
		TypedBlock document = new TypedBlock(BlockType.DOCUMENT);
		document.add(firstBlock);

		// Second line, plain text
		if (withSimpleLines)
		{
			Line secondLine = new Line();
			document.add(secondLine);
			secondLine.add(new TextFragment("Line Two"));
		}

		// List
		TypedBlock list = new TypedBlock(BlockType.UNORDERED_LIST);
		document.add(list);
		for (int i = 0; i < 3; i++)
		{
			TypedBlock item = new TypedBlock(BlockType.LIST_ITEM);
			item.add(new Line(new TextFragment("Item " + i)));
			list.add(item);
		}

		// Empty line
		if (withSimpleLines)
		{
			Line emptyLine = new Line();
			document.add(emptyLine);
//			emptyLine.add(new TextFragment(""));
		}

		// Code block
		TypedBlock code = new TypedBlock(BlockType.CODE);
		document.add(code);
		code.add(new Line(new TextFragment("Block of code")));
		code.add(new Line(new TextFragment("on several lines")));

		// Last line, plain text
		if (withSimpleLines)
		{
			Line lastLine = new Line();
			document.add(lastLine);
			lastLine.add(new TextFragment("Last line"));
		}

		return document;
	}

	public static Block buildMixedBlockFragments()
	{
		// First line, title
		TypedBlock firstBlock = new TypedBlock(BlockType.TITLE1);
		Line title = new Line();
		title.add(new TextFragment("This is a title"));
		firstBlock.add(title);

		// Meet new start of line
		TypedBlock document = new TypedBlock(BlockType.DOCUMENT);
		document.add(firstBlock);

		// Second line
		Line secondLine = new Line();
		document.add(secondLine);
		secondLine.add(new TextFragment("Start of text with "));
		secondLine.add(new DecoratedFragment(FragmentDecoration.EMPHASIS, "emphasis inside"));
		secondLine.add(new TextFragment("."));

		// List
		TypedBlock list = new TypedBlock(BlockType.UNORDERED_LIST);
		document.add(list);
		for (int i = 0; i < 3; i++)
		{
			TypedBlock item = new TypedBlock(BlockType.LIST_ITEM);
			Line line = new Line(new TextFragment("Item " + i + " - "));
			line.add(new DecoratedFragment(FragmentDecoration.STRONG, "Strong fragment, followed by"));
			line.add(new TextFragment(" plain text and "));

			LinkFragment link = new LinkFragment("a nice ", "http://www.example.com/" + i);
			DecoratedFragment linkE = new DecoratedFragment(FragmentDecoration.EMPHASIS, "link (" + i + ")");
			link.add(linkE);
			line.add(link);
			item.add(line);
			list.add(item);
		}

		// Code block
		TypedBlock code = new TypedBlock(BlockType.CODE);
		document.add(code);
		code.add(new Line(new TextFragment("Block of code")));
		code.add(new Line(new TextFragment("on several lines")));

		// Last line
		Line lastLine = new Line();
		lastLine.add(new TextFragment("Boring plain text and "));

		DecoratedFragment df = new DecoratedFragment(FragmentDecoration.EMPHASIS, "emphasized text ");
		df.add(new DecoratedFragment(FragmentDecoration.STRONG, "and even "));
		df.add(new DecoratedFragment(FragmentDecoration.DELETE, "deleted text"));
		df.add(new DecoratedFragment(FragmentDecoration.CODE, " fixed width text"));
		df.add(new TextFragment("."));
		lastLine.add(df);
		document.add(lastLine);

		return document;
	}
}
