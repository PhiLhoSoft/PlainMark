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
	private static final char ESCAPE_SIGN = '~';
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
				break; // Don't go beyond, as this remains within line bounds
			}

			if (walker.current() == ESCAPE_SIGN)
			{
				// Skip it
				walker.forward();
				// And consume next character (if any) literally
				appendCurrentAndForward();
			}
			FragmentDecoration decoration = decorations.get(walker.current());
			boolean processed = false;
			if (decoration != null)
			{
				processed = handleDecorationSign(decoration);
			}
			if (!processed)
			{
				appendCurrentAndForward();
			}
		}

		// We reached the end of line, see if some text remains to be processed
		if (outputString.length() > 0)
		{
			if (stack.size() == 0)
			{
				// No current style, just add text literally
				line.add(outputString.toString());
			}
			else
			{
				// Add remaining text to the current style
				handleDecorationSign(stack.peek().getDecoration());
			}
		}

		return line;
	}

	private void appendCurrentAndForward()
	{
		if (!walker.atLineEnd())
		{
			outputString.append(walker.current());
			walker.forward();
		}
	}

	/**
	 * Handles the current decoration sign (character).
	 *
	 * @param foundDecoration  the decoration corresponding to this sign
	 * @return true if the sign has been interpreted as style mark. False if the context makes it to be kept literally.
	 */
	private boolean handleDecorationSign(FragmentDecoration foundDecoration)
	{
		DecoratedFragment currentDecoratedFragment = stack.peek(); // null if stack is empty
		if (!isCurrentAMarkupSign(foundDecoration, currentDecoratedFragment))
			return false; // No special treatment, regular char
		if (currentDecoratedFragment == null)
		{
			// Not in a decoration so far
			if (outputString.length() > 0)
			{
				// We already captured some text, add it as text fragment
				line.add(outputString.toString());
				outputString.setLength(0); // Clear
			}
			// Start a new decoration
			DecoratedFragment fragment = new DecoratedFragment(foundDecoration);
			line.add(fragment);
			stack.push(fragment);
		}
		else // Inside a decoration
		{
			if (currentDecoratedFragment.getDecoration() == foundDecoration)
			{
				// End of the decorated part
				stack.pop();
				addOutputStringTo(currentDecoratedFragment);
			}
			else if (inStack(foundDecoration))
			{
				// Ignore this one, redundant, keep it as regular character
				return false;
			}
			else // We start a new, different decoration
			{
				addOutputStringTo(currentDecoratedFragment);
				DecoratedFragment fragment = new DecoratedFragment(foundDecoration);
				currentDecoratedFragment.add(fragment);
				stack.push(fragment);
			}
		}
		walker.forward(); // Skip this processed decoration character
		return true;
	}

	private boolean isCurrentAMarkupSign(
			FragmentDecoration foundDecoration, DecoratedFragment currentDecoratedFragment)
	{
		// We know current is a markup sign, but context can tell otherwise
		char previous = walker.previous();
		char next = walker.next();
		boolean starting = currentDecoratedFragment == null || // Not in a decoration
				currentDecoratedFragment.getDecoration() != foundDecoration; // Different decoration

		// Is this a starting sign?
		if (starting &&
				// Deactivated if previous char is a letter or a digit
				(Character.isLetterOrDigit(previous) ||
				// Deactivated if next char is a space
				Character.isWhitespace(next)))
			return false;

		// Is this an ending sign?
		if (!starting &&
				// Deactivated if next char is a letter or a digit
				(Character.isLetterOrDigit(next) ||
				// Deactivated if previous char is a space
				Character.isWhitespace(previous)))
			return false;

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
