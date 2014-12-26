package org.philhosoft.parser.simplemark;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import org.philhosoft.formattedtext.ast.DecoratedFragment;
import org.philhosoft.formattedtext.ast.FragmentDecoration;
import org.philhosoft.formattedtext.ast.Line;
import org.philhosoft.parser.StringWalker;

/**
 * Scannerless parser of a line made of fragments of text.
 * <p>
 * Made to be complementary of a line parser, which will feed this automaton with a single line.
 * If a newline is found in the given text, the parser ends.
 */
public class FragmentParser
{
	Map<Character, FragmentDecoration> decorations = new HashMap<Character, FragmentDecoration>();
	{
		decorations.put('*', FragmentDecoration.STRONG);
		decorations.put('_', FragmentDecoration.EMPHASIS);
		decorations.put('-', FragmentDecoration.DELETE);
		decorations.put('`', FragmentDecoration.CODE);
	}

	private StringWalker walker;
	private Line line = new Line();
	private Deque<DecoratedFragment> stack = new ArrayDeque<DecoratedFragment>();
	private StringBuilder outputString = new StringBuilder();

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
			FragmentDecoration decoration = decorations.get(walker.current());
			boolean processed = false;
			if (decoration != null)
			{
				processed = handleDecorationCharacter(decoration);
			}
			if (!processed && !walker.atLineEnd())
			{
				outputString.append(walker.current());
				walker.forward();
			}
		}

		// We reached the end of line, see if some text remains to be processed
		if (outputString.length() > 0)
		{
			if (stack.size() == 0)
			{
				line.add(outputString.toString());
			}
			else
			{
				handleDecorationCharacter(stack.peek().getDecoration());
			}
		}

		return line;
	}

	private boolean handleDecorationCharacter(FragmentDecoration decoration)
	{
		if (stack.size() == 0)
		{
			if (outputString.length() > 0)
			{
				line.add(outputString.toString());
				outputString.setLength(0); // Clear
			}
		}
		DecoratedFragment currentDecoratedFragment = stack.peek();
		if (currentDecoratedFragment == null) // Not inside a decoration
		{
			// Start a new decoration
			DecoratedFragment fragment = new DecoratedFragment(decoration);
			line.add(fragment);
			stack.push(fragment);
		}
		else // Inside a decoration
		{
			if (currentDecoratedFragment.getDecoration() == decoration)
			{
				// End of the decorated part
				stack.pop();
				addOutputStringTo(currentDecoratedFragment);
			}
			else if (inStack(decoration))
			{
				// Ignore this one, redundant, keep it as regular character
				return false;
			}
			else // We start a new, different decoration
			{
				addOutputStringTo(currentDecoratedFragment);
				DecoratedFragment fragment = new DecoratedFragment(decoration);
				currentDecoratedFragment.add(fragment);
				stack.push(fragment);
			}
		}
		walker.forward(); // Skip this processed decoration character
		return true;
	}

	private void addOutputStringTo(DecoratedFragment currentDecoratedFragment)
	{
		if (outputString.length() > 0)
		{
			currentDecoratedFragment.add(outputString.toString());
			outputString.setLength(0); // Clear
		}
	}

	private boolean inStack(FragmentDecoration decoration)
	{
		for (DecoratedFragment decoratedFragment : stack)
		{
			if (decoratedFragment.getDecoration() == decoration)
				return true;
		}
		return false;
	}
}
