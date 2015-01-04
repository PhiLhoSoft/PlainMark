package org.philhosoft.formattedtext.format;

import java.util.List;

import org.philhosoft.formattedtext.ast.Block;
import org.philhosoft.formattedtext.ast.Fragment;
import org.philhosoft.formattedtext.ast.MarkupVisitor;

public class VisitorHelper
{
	private VisitorHelper()
	{
	}

	/**
	 * Visits each fragment in the given list, properly setting up the context for each one.
	 *
	 * @param list  list of fragments to visit
	 * @param which
	 * @param context  the visiting context
	 */
	public static void visitFragments(List<Fragment> list, MarkupVisitor<VisitorContext> visitor,
			Block parentBlock, VisitorContext context)
	{
		context.push(parentBlock, true, list.size() < 2);
		int last = list.size() - 1;
		for (int i = 0; i <= last; i++)
		{
			context.setFirstLast(i == 0, i == last);
			Fragment f = list.get(i);
			f.accept(visitor, context);
		}
		context.pop();
	}

	/**
	 * Visits each block in the given list, properly setting up the context for each one.
	 *
	 * @param list  list of blocks to visit
	 * @param which
	 * @param context  the visiting context
	 */
	public static void visitBlocks(List<Block> list, MarkupVisitor<VisitorContext> visitor,
			Block parentBlock, VisitorContext context)
	{
		context.push(parentBlock, true, list.size() < 2);
		int last = list.size() - 1;
		for (int i = 0; i <= last; i++)
		{
			context.setFirstLast(i == 0, i == last);
			Block f = list.get(i);
			f.accept(visitor, context);
		}
		context.pop();
	}
}

