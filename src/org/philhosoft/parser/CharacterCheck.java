package org.philhosoft.parser;

/**
 * Various character checking static methods, complementary of Character's ones.
 */
public class CharacterCheck
{
	private CharacterCheck()
	{
	}

	/**
	 * True if the given character is in the given list of chars.
	 */
	public static boolean isOneOf(char c, String characters)
	{
		for (char ch : characters.toCharArray())
		{
			if (c == ch)
				return true;
		}
		return false;
	}

	/** More restrictive than Character.isLetterOrDigit(). */
	public static boolean isAlphaNumerical(char c)
	{
		return c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9';
	}

	public static boolean isDigit(char c)
	{
		return c >= '0' && c <= '9';
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
}
