package org.philhosoft.parser.simplemark;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.philhosoft.formattedtext.ast.DecoratedFragment;
import org.philhosoft.formattedtext.ast.Fragment;
import org.philhosoft.formattedtext.ast.FragmentDecoration;
import org.philhosoft.formattedtext.ast.Line;
import org.philhosoft.formattedtext.ast.TextFragment;
import org.philhosoft.parser.StringWalker;

/**
 * Scannerless parser of a line made of fragments of text.
 * <p>
 * Made to be complementary of a line parser, which will feed this automaton with a single line.
 * If a newline is found in the given text, the parser ends.
 */
public class FragmentParser
{
	private StringWalker walker;
	private Line line = new Line();
	private Deque<FragmentDecoration> stack = new ArrayDeque<FragmentDecoration>();
	private StringBuilder sb = new StringBuilder();

	private FragmentParser(StringWalker walker)
	{
		this.walker = walker;
	}

	public static Line parse(StringWalker walker)
	{
		FragmentParser parser = new FragmentParser(walker);
		return parser.parse();
	}

	private Line parse()
	{
		while (walker.hasMore())
		{
			if (walker.atLineEnd())
			{
				walker.forward(); // Skip line end
				break; // Don't go beyond
			}
			if (walker.current() == '*')
			{
				handleStar();
			}

			sb.append(walker.current());
			walker.forward();
		}

		Fragment fragment = new TextFragment(sb.toString());
		line.add(fragment);

		return line;
	}

	private void handleStar()
	{
		if (stack.size() == 0)
		{
			if (sb.length() > 0)
			{
				line.add(new TextFragment(sb.toString()));
				sb.setLength(0); // Clear
			}
		}
		if (stack.peek() == FragmentDecoration.STRONG)
		{
			// End of the strong part
			stack.pop();
			List<Fragment> fragments = line.getFragments();
			DecoratedFragment fragment = (DecoratedFragment) fragments.get(fragments.size() - 1);
			fragment.add(new TextFragment(sb.toString()));
			sb.setLength(0); // Clear
		}
		else
		{
			line.add(new DecoratedFragment(FragmentDecoration.STRONG));
			stack.push(FragmentDecoration.STRONG);
		}
		walker.forward(); // Skip this star
	}
}
