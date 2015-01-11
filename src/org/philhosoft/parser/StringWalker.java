package org.philhosoft.parser;

// Inspired by Scintilla's StyleContext class.
// Usually, I start my method names with verbs or do real getters, but here I took at more concise approach, for readability.
// Closer of Ceylon style with direct access to properties.
/**
 * Allows to "walk" through a string, character by character, always forward but keeping an eye on the immediate past (and future!).
 * <p>
 * Intended to be used by a parser, abstracting the concept of end-of-line (EOL).
 */
public class StringWalker
{
	private static final char PLACEHOLDER_CHAR = '\0';

	private String walked;
	private int cursor;
	private boolean atLineStart, atLineEnd;

	private char previous = PLACEHOLDER_CHAR;
	private char current = PLACEHOLDER_CHAR;
	private char next = PLACEHOLDER_CHAR;

	public StringWalker(String toWalk)
	{
		this.walked = toWalk;
		atLineStart = true;
		cursor = -1;
		fetchNextCharacter();
		current = next;
		cursor++;
		fetchNextCharacter();
		updateAtLineEnd();
	}

	/**
	 * True if there are more characters to walk through.
	 */
	public boolean hasMore()
	{
		return cursor < walked.length();
	}
	/**
	 * Advance by one character (two if these are a Windows line-ending CR+LF pair).
	 */
	public void forward()
	{
		if (hasMore())
		{
			atLineStart = atLineEnd;
			cursor++;
			previous = current;
			current = next;
			updateAtLineEnd();
			fetchNextCharacter();
		}
		else
		{
			atLineStart = false;
			atLineEnd = true;
			previous = current = next = PLACEHOLDER_CHAR;
		}
	}

	/**
	 * Advances by n characters (see {@link #forward()} remark on EOL).
	 */
	public void forward(int n)
	{
		for (int i = 0; i < n; i++)
		{
			forward();
		}
	}

	/**
	 * Goes forward, skipping whitespace characters (space & tab only).
	 *
	 * @return the number of whitespace characters that has been skipped
	 */
	public int skipSpaces()
	{
		int counter = 0;
		while (isWhitespace(current))
		{
			forward();
			counter++;
		}
		return counter;
	}

	public void goToNextLine()
	{
		do
		{
			forward();
		} while (!atLineStart && hasMore());
	}

	/**
	 * True if we are no an end-of-line character<br>
	 * (classical CR and LF, but also Unicode EOL code points).
	 */
	public boolean atLineEnd()
	{
		return atLineEnd;
	}
	/**
	 * True if we are at the start of a line (just after an EOL).
	 */
	public boolean atLineStart()
	{
		return atLineStart;
	}

	/**
	 * Returns the current character, if any (space otherwise).
	 */
	public char current()
	{
		return current;
	}
	/**
	 * Returns the previous character, if any (space otherwise).
	 */
	public char previous()
	{
		return previous;
	}
	/**
	 * Returns the next character, if any (space otherwise).
	 */
	public char next()
	{
		return next;
	}

	/**
	 * True if the current character is the given one.
	 */
	public boolean match(char c)
	{
		return c == current;
	}
	/**
	 * True if the current and next characters are the given ones.
	 */
	public boolean match(char c1, char c2)
	{
		return c1 == current && c2 == next;
	}
	/**
	 * True if the string at the current position matches the given string.
	 */
	public boolean match(String s)
	{
		if (s == null || s.isEmpty())
			return false; // Whatever...
		if (s.charAt(0) != current)
			return false;
		if (s.length() == 1)
			return true;
		if (s.charAt(1) != next)
			return false;
		if (s.length() == 2)
			return true;
		for (int i = 2; i < s.length(); i++)
		{
			if (s.charAt(i) != safeCharAt(cursor + i, PLACEHOLDER_CHAR))
				return false;
		}
		return true;
	}
	/**
	 * match() with forward offset.
	 */
	public boolean matchAt(int offset, String s)
	{
		if (s == null || s.isEmpty())
			return false; // Whatever...
		// Same algo without the quick exits
		for (int i = 0; i < s.length(); i++)
		{
			if (s.charAt(i) != safeCharAt(offset + cursor + i, PLACEHOLDER_CHAR))
				return false;
		}
		return true;
	}

	/**
	 * True if the current character is in the given list of chars.
	 */
	public boolean matchOneOf(char... characters)
	{
		for (char c : characters)
		{
			if (current == c)
				return true;
		}
		return false;
	}

	/** More restrictive than Character.isLetterOrDigit(). */
	public static boolean isAlphaNumerical(char c)
	{
		return c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9';
	}

	public static boolean isLineTerminator(char c)
	{
		/*
		From Java's Pattern class JavaDoc:
		A line terminator is a one- or two-character sequence that marks the end of a line of the input character sequence.
		The following are recognized as line terminators:

		A newline (line feed) character ('\n'),
		A carriage-return character followed immediately by a newline character ("\r\n"),
		A standalone carriage-return character ('\r'),
		A next-line character ('\u0085'),
		A line-separator character ('\u2028'), or
		A paragraph-separator character ('\u2029).
		*/
		return c == '\n' || c == '\r' || c == '\u0085' || c == '\u2028' || c == '\u2029';
	}

	/**
	 * Only whitespace, not line terminators.
	 */
	public static boolean isWhitespace(char c)
	{
		return c == ' ' || c == '\t';
	}

	private void fetchNextCharacter()
	{
		next = safeCharAt(cursor + 1, PLACEHOLDER_CHAR);
		if (current == '\r' && next == '\n')
		{
			next = safeCharAt(++cursor + 1, PLACEHOLDER_CHAR);
		}
	}
	private char safeCharAt(int pos, char defaultChar)
	{
		if (pos >= 0 && pos < walked.length())
			return walked.charAt(pos);

		return defaultChar;
	}

	private void updateAtLineEnd()
	{
		atLineEnd = isLineTerminator(current) || !hasMore();
	}

	@Override
	public String toString()
	{
		String position = "";
		position += atLineStart ? "line start" : "";
		position += atLineEnd ? "line end" : "";
		return "StringWalker[cursor=" + cursor + (position.isEmpty() ? "" : ", " + position) +
				", context=" + toString(previous) + toString(current) + toString(next) +
				", [" + (hasMore() ? walked.substring(cursor) : "") + "]]";
	}
	private String toString(char c)
	{
		if (c == PLACEHOLDER_CHAR)
			return "<none>";
		if (isLineTerminator(c))
			return "\\n";
		return Character.toString(c);
	}
}
