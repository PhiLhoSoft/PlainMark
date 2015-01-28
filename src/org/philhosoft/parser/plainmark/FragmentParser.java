package org.philhosoft.parser.plainmark;

import org.philhosoft.collection.SimpleStack;
import org.philhosoft.formattedtext.ast.DecoratedFragment;
import org.philhosoft.formattedtext.ast.Fragment;
import org.philhosoft.formattedtext.ast.FragmentDecoration;
import org.philhosoft.formattedtext.ast.Line;
import org.philhosoft.formattedtext.ast.LinkFragment;
import org.philhosoft.formattedtext.ast.TextFragment;
import org.philhosoft.parser.CharacterCheck;
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
	private RestoreFragmentVisitor fragmentRestore = new RestoreFragmentVisitor();
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

			handleMarkup();
		}
		walker.forward(); // Go to start of next line, if any

		// We reached the end of line, see if some text remains to be processed
		popStack(stack.size() - 1, line);

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
				next == ParsingParameters.LINK_END_SIGN ||
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

	private void handleMarkup()
	{
		FragmentDecoration decoration = parsingParameters.getFragmentDecoration(walker.current());
		boolean processed = false;
		if (decoration != null)
		{
			processed = handleDecorationSign(decoration);
		}
		if (!processed)
		{
			processed = handleLinkMarkup();
		}
		if (!processed)
		{
			appendCurrentAndForward();
		}
	}

	private boolean handleLinkMarkup()
	{
		if (walker.current() == ParsingParameters.LINK_START_SIGN)
		{
			handleLinkStart();
			return true;
		}
		if (walker.current() == ParsingParameters.LINK_END_SIGN)
		{
			return handleLinkEnd();
		}

		return false;
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
			// Inside a parent fragment
			handleDecorationInsideAnother(foundDecoration, currentFragment);
		}
		walker.forward(); // Skip this processed decoration character
		return true;
	}

	private void handleLinkStart()
	{
		// We already captured some text, add it as text fragment
		addOutputStringToCurrentFragment();
		// Start a new fragment
		LinkFragment link = new LinkFragment();
		stack.push(link);
		walker.forward(); // Skip this processed decoration character
	}

	private boolean handleLinkEnd()
	{
		if (walker.next() == ParsingParameters.URL_START_SIGN && handleURLStart())
			return true;

		// Not a link end
		// Find if we have a link in the stack. If so, we deactivate it: we found nested brackets.
		Fragment currentFragment = stack.peek();
		if (currentFragment instanceof LinkFragment)
		{
			addOutputStringToCurrentFragment();
			stack.pop();
			convertLinkTextToTextFragments(currentFragment);
		}

		return false;
	}

	private boolean handleURLStart()
	{
		// We are really in a link, if there is one in the stack
		// Search the link up, we will have to pop out the unterminated fragments if we find them
		int depth = findLinkDepthInStack();
		if (depth < 0)
			return false;

		LinkFragment link = (LinkFragment) stack.peekAt(depth);
		// Remove and restore any unterminated fragment before the link
		popStack(depth - 1, link);
		// Pop out the link that surfaced
		stack.pop();
		walker.forward(2); // Skip ](
		if (addURLUpToClosingParenthesis(link))
		{
			addFragment(link);
		}
		else
		{
			convertLinkTextToTextFragments(link);
			addFragment(new TextFragment(new String(new char[] { ParsingParameters.LINK_END_SIGN, ParsingParameters.URL_START_SIGN })));
		}
		return true;
	}

	private int findLinkDepthInStack()
	{
		int depth = 0;
		for (Fragment fragment : stack)
		{
			if (fragment instanceof LinkFragment)
				return depth;
			depth++;
		}
		return -1;
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
				currentFragment.getDecoration() != foundDecoration; // Different decoration

		// Is this a starting sign?
		if (starting &&
				// Deactivated if previous char is a letter or a digit
				(Character.isLetterOrDigit(previous) ||
				// Deactivated if next char is a space
				CharacterCheck.isWhitespace(next)))
			return false;

		// Is this an ending sign?
		if (!starting &&
				// Deactivated if next char is a letter or a digit
				(Character.isLetterOrDigit(next) ||
				// Deactivated if previous char is a space
				CharacterCheck.isWhitespace(previous)))
			return false;

		return true;
	}

	private void handleNewDecoration(FragmentDecoration foundDecoration)
	{
		// We already captured some text, add it as text fragment
		addOutputStringToCurrentFragment();
		// Start a new decoration
		Fragment fragment = new DecoratedFragment(foundDecoration);
		stack.push(fragment);
		flagCodeFragment(foundDecoration, true);
	}

	private void handleDecorationInsideAnother(FragmentDecoration foundDecoration, Fragment parentFragment)
	{
		if (parentFragment.getDecoration() == foundDecoration)
		{
			// End of the decorated part
			handleClosingDecoration(foundDecoration, (DecoratedFragment) parentFragment);
			return;
		}

		// We start a new, different, nested decoration
		addOutputStringTo(parentFragment);
		Fragment fragment = new DecoratedFragment(foundDecoration);
		stack.push(fragment);
		flagCodeFragment(foundDecoration, true);
	}

	private void handleClosingDecoration(FragmentDecoration foundDecoration, DecoratedFragment parent)
	{
		if (parent.getFragments().isEmpty() && outputString.length() == 0)
		{
			// Empty fragment, restore the initial decoration sign twice
			foundDecoration.accept(fragmentRestore, outputString);
			foundDecoration.accept(fragmentRestore, outputString);
			stack.pop();
		}
		else
		{
			addOutputStringTo(parent);
			addFragmentToParent(stack.pop());
		}
		flagCodeFragment(foundDecoration, false);
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

	private void addOutputStringTo(Fragment currentFragment)
	{
		if (outputString.length() > 0)
		{
			currentFragment.add(outputString.toString());
			outputString.setLength(0); // Clear
		}
	}

	/**
	 * Adds the current content of outputString to the current fragment.
	 */
	private void addOutputStringToCurrentFragment()
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
		if (!CharacterCheck.isAlphaNumerical(walker.current()))
		{
			// Probably just mentioning a raw schema
			outputString.append(urlPrefix);
			return;
		}
		addOutputStringToCurrentFragment();
		walkTheURL();
		LinkFragment lf = makeLinkFragmentFromURL(urlPrefix, outputString.toString());
		addFragment(lf);
		outputString.setLength(0);
	}

	private boolean addURLUpToClosingParenthesis(LinkFragment parent)
	{
		boolean valid = walkTheURL();
		if (valid)
		{
			walker.forward(); // Skip closing parenthesis
			parent.setURL(outputString.toString());
			outputString.setLength(0);
		}
		return valid;
	}

	private void convertLinkTextToTextFragments(Fragment linkFragment)
	{
		addFragment(new TextFragment(String.valueOf(ParsingParameters.LINK_START_SIGN)));
		for (Fragment fragment : ((LinkFragment) linkFragment).getFragments())
		{
			addFragment(fragment);
		}
	}

	private LinkFragment makeLinkFragmentFromURL(String urlPrefix, String url)
	{
		String text = url;
		int maxLinkLength = parsingParameters.getMaxLinkLength();
		int textLength = text.length();
		if (maxLinkLength > 0 && textLength > maxLinkLength)
		{
			switch (parsingParameters.getLinkEllipsisPlacement())
			{
			case END:
				text = text.substring(0, maxLinkLength) + parsingParameters.getEllipsis();
				break;
			case START:
				text = parsingParameters.getEllipsis() + text.substring(textLength - maxLinkLength, textLength);
				break;
			case MIDDLE:
				text = text.substring(0, maxLinkLength / 2) + parsingParameters.getEllipsis() +
					text.substring(textLength - maxLinkLength + maxLinkLength / 2, textLength);
				break;
			}
		}
		LinkFragment lf = new LinkFragment(text, urlPrefix + outputString.toString());
		return lf;
	}

	/**
	 * @return false if the parentheses are not balanced
	 */
	private boolean walkTheURL()
	{
		int openedParentheses = 0;
		while (parsingParameters.isValidURLChar(walker.current()))
		{
			if (walker.current() == ParsingParameters.URL_START_SIGN)
			{
				openedParentheses++;
			}
			if (walker.current() == ParsingParameters.URL_END_SIGN)
			{
				if (openedParentheses == 0)
					return true; // End of URL

				openedParentheses--;
			}
			outputString.append(walker.current());
			walker.forward();
		}
		return false;
	}

	/**
	 * If we have things remaining in the stack, these are unterminated fragments, we just dump them out literally
	 * (ie. fragment signs were inactive).
	 *
	 * @param startingPosition  the starting position, from the head. To pop out all the stack, use stack.size() - 1.
	 * @param targetFragment  the fragment where to add the sub-fragments
	 */
	private void popStack(int startingPosition, Fragment targetFragment)
	{
		addOutputStringToCurrentFragment();
		if (startingPosition > stack.size() - 1)
			return;
		while (startingPosition >= 0)
		{
			Fragment fragment = stack.pollAt(startingPosition--);
			restoreFragment(fragment, targetFragment);
		}
	}

	/**
	 * Transforms a fragment back to its initial sign and its text.
	 */
	private void restoreFragment(Fragment fragment, Fragment target)
	{
		FragmentDecoration decoration = fragment.getDecoration();
		if (decoration == null)
			throw new IllegalStateException("TextFragments shouldn't go in the stack");
		decoration.accept(fragmentRestore, outputString);
		for (Fragment subFragment : fragment.getFragments())
		{
			if (subFragment instanceof TextFragment)
			{
				outputString.append(((TextFragment) subFragment).getText());
			}
			else
			{
				target.add(outputString.toString());
				outputString.setLength(0);
				target.add(subFragment);
			}
		}

		target.add(outputString.toString());
		outputString.setLength(0);
	}
}
