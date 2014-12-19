package org.philhosoft.formattedtext.format;

import org.philhosoft.formattedtext.ast.Block;
import org.philhosoft.formattedtext.ast.DecoratedFragment;
import org.philhosoft.formattedtext.ast.Fragment;
import org.philhosoft.formattedtext.ast.Line;
import org.philhosoft.formattedtext.ast.LinkFragment;
import org.philhosoft.formattedtext.ast.MarkupVisitor;
import org.philhosoft.formattedtext.ast.PlainTextFragment;
import org.philhosoft.formattedtext.ast.TypedBlock;

public class PlainTextVisitor implements MarkupVisitor<StringBuilder>
{
	@Override
	public void visit(DecoratedFragment fragment, StringBuilder output)
	{
		for (Fragment f : fragment.getFragments())
		{
			f.accept(this, output);
		}
	}

	@Override
	public void visit(PlainTextFragment fragment, StringBuilder output)
	{
		output.append(fragment.getText());
	}

	@Override
	public void visit(LinkFragment fragment, StringBuilder output)
	{
		fragment.getTextFragment().accept(this, output);
		output.append(" - ").append(fragment.getUrl());
	}

	@Override
	public void visit(TypedBlock typedBlock, StringBuilder output)
	{
		for (Block b : typedBlock.getBlocks())
		{
			b.accept(this, output);
			output.append("\n");
		}
	}

	@Override
	public void visit(Line line, StringBuilder output)
	{
		for (Fragment f : line.getFragments())
		{
			f.accept(this, output);
		}
	}
}
