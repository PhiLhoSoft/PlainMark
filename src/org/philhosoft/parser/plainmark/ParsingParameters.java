package org.philhosoft.parser.plainmark;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.philhosoft.formattedtext.ast.BlockType;
import org.philhosoft.formattedtext.ast.FragmentDecoration;

public class ParsingParameters
{
	public static final char STRONG_SIGN = '*';
	public static final char EMPHASIS_SIGN = '_';
	public static final char DELETE_SIGN = '-';
	public static final char CODE_FRAGMENT_SIGN = '`';

	public static final char LINK_START_SIGN = '[';
	public static final char LINK_END_SIGN = ']';
	public static final char URL_START_SIGN = '(';
	public static final char URL_END_SIGN = ')';

	private static final char ORDERED_LIST_SUFFIX = '.';
	private char escapeSign = '~';

	private String[] urlPrefixes =
	{
		"http://", "https://", "ftp://", "ftps://",
	};
	// http://stackoverflow.com/questions/1856785/characters-allowed-in-a-url
	// Used only for autolinking. Obviously, ) will terminate the URL in explicit links, and should be escaped to %29.
	// ] can be escaped to %5D if needed, too.
	private char[] validURLChars =
	{
		'-', '.', '_', '~', // unreserved (with alpha-num, of course)
		':', '/', '?', '#', '[', ']', '@', // reserved, gen-delims
		'!', '$', '&', '\'', '(', ')', '*', '+', ',', ';', '=', // reserved, sub-delims
		'%' // escape
	};

	private static final Map<Character, FragmentDecoration> decorations = new HashMap<Character, FragmentDecoration>();
	{
		decorations.put(STRONG_SIGN, FragmentDecoration.STRONG);
		decorations.put(EMPHASIS_SIGN, FragmentDecoration.EMPHASIS);
		decorations.put(DELETE_SIGN, FragmentDecoration.DELETE);
		decorations.put(CODE_FRAGMENT_SIGN, FragmentDecoration.CODE);
	}
	private static final Map<String, BlockType> blockTypesPerSign = new HashMap<String, BlockType>();
	{
		blockTypesPerSign.put("#", BlockType.TITLE1);
		blockTypesPerSign.put("##", BlockType.TITLE2);
		blockTypesPerSign.put("###", BlockType.TITLE3);
		blockTypesPerSign.put("*", BlockType.LIST_ITEM_BULLET);
		blockTypesPerSign.put("-", BlockType.LIST_ITEM_BULLET);
		blockTypesPerSign.put("+", BlockType.LIST_ITEM_BULLET);
	}
	private String codeBlockSign = "```";

	private int maxLinkLength = 30;
	private String ellipsis = "\u2026";

	public char getEscapeSign()
	{
		return escapeSign;
	}
	public void setEscapeSign(char escapeSign)
	{
		this.escapeSign = escapeSign;
	}
	public int getMaxLinkLength()
	{
		return maxLinkLength;
	}
	public void setMaxLinkLength(int maxLinkLength)
	{
		this.maxLinkLength = maxLinkLength;
	}
	public String getEllipsis()
	{
		return ellipsis;
	}
	public void setEllipsis(String ellipsis)
	{
		this.ellipsis = ellipsis;
	}
	public String[] getUrlPrefixes()
	{
		return urlPrefixes;
	}
	public void setUrlPrefixes(String[] urlPrefixes)
	{
		this.urlPrefixes = urlPrefixes;
	}
	public char[] getValidURLChars()
	{
		return validURLChars;
	}
	public void setValidURLChars(char[] validURLChars)
	{
		this.validURLChars = validURLChars;
	}

	public FragmentDecoration getFragmentDecoration(char sign)
	{
		return decorations.get(sign);
	}
	public BlockType getBlockType(String blockSign)
	{
		return blockTypesPerSign.get(blockSign);
	}
	public Set<String> getBlockTypeSigns()
	{
		return blockTypesPerSign.keySet();
	}
	public String getCodeBlockSign()
	{
		return codeBlockSign;
	}
	public boolean isOrderedListSuffix(char c)
	{
		// Currently only one, we might add alternatives
		return c == ORDERED_LIST_SUFFIX;
	}
}
