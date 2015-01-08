package org.philhosoft.parser.simplemark;

import java.util.ArrayDeque;
import java.util.Deque;

import org.philhosoft.formattedtext.ast.DecoratedFragment;
import org.philhosoft.formattedtext.ast.Fragment;
import org.philhosoft.formattedtext.ast.FragmentDecoration;
import org.philhosoft.formattedtext.ast.Line;
import org.philhosoft.formattedtext.ast.LinkFragment;
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
	private ParsingParameters parsingParameters;
	private Line line = new Line();
	private Deque<DecoratedFragment> stack = new ArrayDeque<DecoratedFragment>();
	private StringBuilder outputString = new StringBuilder();

	private FragmentParser(StringWalker walker, ParsingParameters parsingParameters)
	{
		this.walker = walker;
		this.parsingParameters = parsingParameters;
	}

	/**
	 * Parses the string given with the walker, and returns a Line, leaving the walker at the end of the line.
	 *
	 * @param walker  the walker at the position where we want to start parsing
	 * @return the resulting line
	 */
	public static Line parse(StringWalker walker)
	{
		FragmentParser parser = new FragmentParser(walker, new ParsingParameters());
		return parser.parse();
	}

	/**
	 * When autolinking URLs
	 * (eg. http://www.example.com/whatever becoming (www.example.com/whatever)[http://www.example.com/whatever]),
	 * the link text length will be limited (with ellipsis). Default is 30 chars, this methods allows to change this
	 * limit.
	 *
	 * @param walker  the walker at the position where we want to start parsing
	 * @param maxLinkLength  maximum length (without ellipsis) of the link text. If set to zero or lower, there is no limit.
	 */
	public static Line parse(StringWalker walker, ParsingParameters parsingParameters)
	{
		FragmentParser parser = new FragmentParser(walker, parsingParameters);
		return parser.parse();
	}

	private Line parse()
	{
		while (walker.hasMore())
		{
			if (walker.atLineEnd())
			{
				walker.forward(); // Go to start of next line, if any
				break; // Don't go beyond, as this parser remains within line bounds
			}

			if (handleEscapeSign())
				continue;

			String urlPrefix = findURLPrefix();
			if (urlPrefix != null)
			{
				handleURL(urlPrefix);
				continue;
			}

			handleDecoration();
		}

		// We reached the end of line, see if some text remains to be processed
		popStack();

		return line;
	}

	private boolean handleEscapeSign()
	{
		if (walker.current() != parsingParameters.getEscapeSign())
			return false;
		char next = walker.next();
		if (parsingParameters.getFragmentDecoration(next) != null ||
				next == ParsingParameters.LINK_START_SIGN ||
				next == parsingParameters.getEscapeSign())
		{
			// Skip it
			walker.forward();
			// And consume next character (if any) literally
			appendCurrentAndForward();
			return true;
		}
		return false;
	}

	private void appendCurrentAndForward()
	{
		if (!walker.atLineEnd())
		{
			outputString.append(walker.current());
			walker.forward();
		}
	}

	private void handleDecoration()
	{
		FragmentDecoration decoration = parsingParameters.getFragmentDecoration(walker.current());
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

	/**
	 * Handles the current decoration sign (character).
	 *
	 * @param foundDecoration  the decoration corresponding to this sign
	 * @return true if the sign has been interpreted as style mark. false if the context makes it to be kept literally.
	 */
	private boolean handleDecorationSign(FragmentDecoration foundDecoration)
	{
		DecoratedFragment currentDecoratedFragment = stack.peek(); // null if stack is empty
		if (!isCurrentAMarkupSign(foundDecoration, currentDecoratedFragment))
			return false; // No special treatment, regular char
		if (currentDecoratedFragment == null)
		{
			// Not in a decoration so far
			handleNewDecoration(foundDecoration);
		}
		else // Inside a decoration
		{
			handleNestedDecoration(foundDecoration, currentDecoratedFragment);
		}
		walker.forward(); // Skip this processed decoration character
		return true;
	}

	/**
	 * Checks if the current markup sign has a context allowing to see it as a markup start or end.
	 * <p>
	 * Assumes we know already that walker.current() is one of the markup signs.
	 *
	 * @param foundDecoration  the decoration corresponding to walker.current()
	 * @param currentDecoratedFragment  the context where the sign is found
	 * @return true if it is OK, false if it is a plain character
	 */
	private boolean isCurrentAMarkupSign(FragmentDecoration foundDecoration, DecoratedFragment currentDecoratedFragment)
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

	private void handleNewDecoration(FragmentDecoration foundDecoration)
	{
		// We already captured some text, add it as text fragment
		addOutputToCurrentFragment();
		// Start a new decoration
		DecoratedFragment fragment = new DecoratedFragment(foundDecoration);
		stack.push(fragment);
	}

	private void handleNestedDecoration(FragmentDecoration foundDecoration, DecoratedFragment currentDecoratedFragment)
	{
		if (currentDecoratedFragment.getDecoration() == foundDecoration)
		{
			// End of the decorated part
			addOutputStringTo(currentDecoratedFragment);

			if (stack.size() == 1) // Last stacked
			{
				line.add(stack.pop());
			}
			else
			{
				DecoratedFragment fragment = stack.pop();
				stack.peek().add(fragment);
			}
		}
		else // We start a new, different decoration
		{
			addOutputStringTo(currentDecoratedFragment);

			DecoratedFragment fragment = new DecoratedFragment(foundDecoration);
			stack.push(fragment);
		}
	}

	/**
	 * Adds the current content of outputString to the current fragment.
	 */
	private void addOutputToCurrentFragment()
	{
		if (outputString.length() > 0)
		{
			addFragment(new TextFragment(outputString.toString()));
			outputString.setLength(0); // Clear
		}
	}

	/**
	 * Adds the given fragment directly to the line if stack is empty,
	 * or to the current decorated fragment if we are inside one.
	 */
	private void addFragment(Fragment fragment)
	{
		DecoratedFragment currentDecoratedFragment = stack.peek(); // null if stack is empty
		if (currentDecoratedFragment == null)
		{
			// No current style, just add at top level
			line.add(fragment);
		}
		else
		{
			currentDecoratedFragment.add(fragment);
		}
	}

	private void addOutputStringTo(DecoratedFragment currentDecoratedFragment)
	{
		if (outputString.length() > 0)
		{
			currentDecoratedFragment.add(outputString.toString());
			outputString.setLength(0); // Clear
		}
	}

	private String findURLPrefix()
	{
		for (String p : parsingParameters.getUrlPrefixes())
		{
			if (walker.match(p))
				return p;
		}
		return null;
	}

	private void handleURL(String urlPrefix)
	{
		walker.forward(urlPrefix.length());
		if (!walker.isAlphaNumerical(walker.current()))
		{
			// Probably just mentioning a raw schema
			outputString.append(urlPrefix);
			return;
		}
		addOutputToCurrentFragment();
		char[] validURLChars = parsingParameters.getValidURLChars();
		while (walker.isAlphaNumerical(walker.current()) || walker.matchOneOf(validURLChars))
		{
			outputString.append(walker.current());
			walker.forward();
		}
		LinkFragment lf = makeLinkFragmentFromURL(urlPrefix, outputString.toString());
		addFragment(lf);
		outputString.setLength(0);
	}

	private LinkFragment makeLinkFragmentFromURL(String urlPrefix, String url)
	{
		String text = url;
		int maxLinkLength = parsingParameters.getMaxLinkLength();
		if (maxLinkLength  > 0 && maxLinkLength < text.length())
		{
			text = text.substring(0, maxLinkLength) + parsingParameters.getEllipsis();
		}
		LinkFragment lf = new LinkFragment(text, urlPrefix + outputString.toString());
		return lf;
	}

	/**
	 * If we have things remaining in the stack, these are unterminated fragments, we just dump them out literally (ie. fragment signs were inactive).
	 */
	private void popStack()
	{
		addOutputToCurrentFragment();
		RestoreFragmentVisitor fragmentRestore = new RestoreFragmentVisitor();
		while (stack.size() > 0)
		{
			DecoratedFragment fragment = stack.pollLast();
			fragment.getDecoration().accept(fragmentRestore, outputString);
			for (Fragment subFragment : fragment.getFragments())
			{
				if (subFragment instanceof LinkFragment || subFragment instanceof DecoratedFragment)
				{
					line.add(outputString.toString());
					outputString.setLength(0);
					line.add(subFragment);
				}
				else if (subFragment instanceof TextFragment)
				{
					outputString.append(((TextFragment) subFragment).getText());
				}
				else
					throw new IllegalStateException("A new class must be handled here");
			}

			line.add(outputString.toString());
			outputString.setLength(0);
		}
	}
}
