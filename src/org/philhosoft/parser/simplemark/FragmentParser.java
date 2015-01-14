package org.philhosoft.parser.simplemark;

import java.util.ArrayList;
import java.util.List;

import org.philhosoft.collection.SimpleStack;
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
	private SimpleStack<Fragment> stack = new SimpleStack<Fragment>();
	private StringBuilder outputString = new StringBuilder();
	private boolean inCodeFragment;

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
		// Walk the string, until the line end (line break or end of string) is met.
		// The parser doesn't go beyond line breaks
		while (!walker.atLineEnd())
		{
			if (inCodeFragment && isStillInsideCodeFragment())
				continue;

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
		walker.forward(); // Go to start of next line, if any

		// We reached the end of line, see if some text remains to be processed
		popStack();

		return line;
	}

	private boolean isStillInsideCodeFragment()
	{
		if (walker.current() == parsingParameters.getEscapeSign())
		{
			char next = walker.next();
			if (next == parsingParameters.getEscapeSign() || next == ParsingParameters.CODE_FRAGMENT_SIGN)
			{
				// Skip it
				walker.forward();
				// And consume next character (if any) literally
				appendCurrentAndForward();
				return true;
			}
		}
		if (walker.current() == ParsingParameters.CODE_FRAGMENT_SIGN)
		{
			inCodeFragment = false;
			return false;
		}

		// Consume character literally
		appendCurrentAndForward();

		return true;
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
			if (walker.current() == ParsingParameters.LINK_START_SIGN)
			{
				handleLink();
			}
			else
			{
				appendCurrentAndForward();
			}
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
		Fragment currentFragment = stack.peek(); // null if stack is empty
		if (!isCurrentAMarkupSign(foundDecoration, currentFragment))
			return false; // No special treatment, regular char
		if (currentFragment == null)
		{
			// Not in a decoration so far
			handleNewDecoration(foundDecoration);
		}
		else
		{
			// Inside a decoration
			handleNestedDecoration(foundDecoration, (DecoratedFragment) currentFragment);
		}
		walker.forward(); // Skip this processed decoration character
		return true;
	}

	private void handleLink()
	{
		Fragment currentFragment = stack.peek(); // null if stack is empty
		if (currentFragment == null)
		{
			// Not in a decoration so far
			// We already captured some text, add it as text fragment
			addOutputToCurrentFragment();
			// Start a new fragment
			LinkFragment link = new LinkFragment();
			stack.push(link);
		}
		else
		{
			// Inside a decoration
		}
		walker.forward(); // Skip this processed decoration character
	}

	/**
	 * Checks if the current markup sign has a context allowing to see it as a markup start or end.
	 * <p>
	 * Assumes we know already that walker.current() is one of the markup signs.
	 *
	 * @param foundDecoration  the decoration corresponding to walker.current()
	 * @param currentFragment  the context where the sign is found
	 * @return true if it is OK, false if it is a plain character
	 */
	private boolean isCurrentAMarkupSign(FragmentDecoration foundDecoration, Fragment currentFragment)
	{
		// We know current is a markup sign, but context can tell otherwise
		char previous = walker.previous();
		char next = walker.next();
		boolean starting = currentFragment == null || // Not in a decoration
				(currentFragment instanceof DecoratedFragment &&
				((DecoratedFragment) currentFragment).getDecoration() != foundDecoration); // Different decoration

		// Is this a starting sign?
		if (starting &&
				// Deactivated if previous char is a letter or a digit
				(Character.isLetterOrDigit(previous) ||
				// Deactivated if next char is a space
				StringWalker.isWhitespace(next)))
			return false;

		// Is this an ending sign?
		if (!starting &&
				// Deactivated if next char is a letter or a digit
				(Character.isLetterOrDigit(next) ||
				// Deactivated if previous char is a space
				StringWalker.isWhitespace(previous)))
			return false;

		return true;
	}

	private void handleNewDecoration(FragmentDecoration foundDecoration)
	{
		// We already captured some text, add it as text fragment
		addOutputToCurrentFragment();
		// Start a new decoration
		Fragment fragment = new DecoratedFragment(foundDecoration);
		stack.push(fragment);
		flagCodeFragment(foundDecoration, true);
	}

	private void handleNestedDecoration(FragmentDecoration foundDecoration, DecoratedFragment currentFragment)
	{
		addOutputStringTo(currentFragment);

		if (currentFragment.getDecoration() == foundDecoration)
		{
			// End of the decorated part
			addFragmentToParent(stack.pop());
			flagCodeFragment(foundDecoration, false);
		}
		else // We start a new, different decoration
		{
			Fragment fragment = new DecoratedFragment(foundDecoration);
			stack.push(fragment);
			flagCodeFragment(foundDecoration, true);
		}
	}

	private void flagCodeFragment(FragmentDecoration foundDecoration, boolean start)
	{
		if (foundDecoration == FragmentDecoration.CODE)
		{
			inCodeFragment = start;
		}
	}

	private void addFragmentToParent(Fragment fragment)
	{
		if (stack.isEmpty()) // Was lLast stacked
		{
			line.add(fragment);
		}
		else
		{
			Fragment parent = stack.peek();
			if (parent != null)
			{
				parent.add(fragment);
			}
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
		Fragment currentFragment = stack.peek(); // null if stack is empty
		if (currentFragment == null)
		{
			// No current style, just add at top level
			line.add(fragment);
		}
		else
		{
			currentFragment.add(fragment);
		}
	}

	private void addOutputStringTo(Fragment currentFragment)
	{
		if (outputString.length() > 0)
		{
			currentFragment.add(outputString.toString());
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
		if (!StringWalker.isAlphaNumerical(walker.current()))
		{
			// Probably just mentioning a raw schema
			outputString.append(urlPrefix);
			return;
		}
		addOutputToCurrentFragment();
		char[] validURLChars = parsingParameters.getValidURLChars();
		while (StringWalker.isAlphaNumerical(walker.current()) || walker.matchOneOf(validURLChars))
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
	 * If we have things remaining in the stack, these are unterminated fragments, we just dump them out literally
	 * (ie. fragment signs were inactive).
	 */
	private void popStack()
	{
		addOutputToCurrentFragment();
		RestoreFragmentVisitor fragmentRestore = new RestoreFragmentVisitor();
		while (stack.size() > 0)
		{
			Fragment fragment = stack.pollLast();
			List<Fragment> decoratedFragments = new ArrayList<Fragment>();
			if (fragment instanceof DecoratedFragment)
			{
				((DecoratedFragment) fragment).getDecoration().accept(fragmentRestore, outputString);
				decoratedFragments = ((DecoratedFragment) fragment).getFragments();
			}
			else if (fragment instanceof LinkFragment)
			{
				outputString.append(ParsingParameters.LINK_START_SIGN);
				decoratedFragments = ((LinkFragment) fragment).getFragments();
			}
			else
				throw new IllegalStateException("Shouldn't have " + fragment.getClass().getName() + " in stack!");
			for (Fragment subFragment : decoratedFragments)
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
