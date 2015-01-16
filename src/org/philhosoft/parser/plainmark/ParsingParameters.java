package org.philhosoft.parser.plainmark;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.philhosoft.formattedtext.ast.BlockType;
import org.philhosoft.formattedtext.ast.FragmentDecoration;
import org.philhosoft.parser.CharacterCheck;

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

	private static final String[] DEFAULT_URL_PREFIXES =
	{
		"http://", "https://", "ftp://", "ftps://", "sftp://",
	};
	// http://stackoverflow.com/questions/1856785/characters-allowed-in-a-url
	private static final String VALID_URL_CHARS =
		"-._~" + // unreserved (with alpha-num, of course)
		":/?#[]@" + // reserved, gen-delims
		"!$&'()*+,;=" + // reserved, sub-delims
		"%"; // escape

	private static final Map<Character, FragmentDecoration> DECORATIONS = new HashMap<Character, FragmentDecoration>();
	{
		DECORATIONS.put(STRONG_SIGN, FragmentDecoration.STRONG);
		DECORATIONS.put(EMPHASIS_SIGN, FragmentDecoration.EMPHASIS);
		DECORATIONS.put(DELETE_SIGN, FragmentDecoration.DELETE);
		DECORATIONS.put(CODE_FRAGMENT_SIGN, FragmentDecoration.CODE);
	}
	private static final Map<String, BlockType> BLOCK_TYPES_PER_SIGN = new HashMap<String, BlockType>();
	{
		BLOCK_TYPES_PER_SIGN.put("#", BlockType.TITLE1);
		BLOCK_TYPES_PER_SIGN.put("##", BlockType.TITLE2);
		BLOCK_TYPES_PER_SIGN.put("###", BlockType.TITLE3);
		BLOCK_TYPES_PER_SIGN.put("*", BlockType.LIST_ITEM_BULLET);
		BLOCK_TYPES_PER_SIGN.put("-", BlockType.LIST_ITEM_BULLET);
		BLOCK_TYPES_PER_SIGN.put("+", BlockType.LIST_ITEM_BULLET);
	}

	private char escapeSign = '~';
	private String codeBlockSign = "```";

	private int maxLinkLength = 30;
	private String ellipsis = "\u2026";

	private List<String> urlPrefixes = new ArrayList<String>();
	{
		for (String urlPrefix : DEFAULT_URL_PREFIXES)
		{
			urlPrefixes.add(urlPrefix);
		}
	}

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
	public List<String> getUrlPrefixes()
	{
		return urlPrefixes;
	}
	public void setUrlPrefixes(List<String> urlPrefixes)
	{
		this.urlPrefixes = urlPrefixes;
	}

	public FragmentDecoration getFragmentDecoration(char sign)
	{
		return DECORATIONS.get(sign);
	}
	public BlockType getBlockType(String blockSign)
	{
		return BLOCK_TYPES_PER_SIGN.get(blockSign);
	}
	public Set<String> getBlockTypeSigns()
	{
		return BLOCK_TYPES_PER_SIGN.keySet();
	}
	public String getCodeBlockSign()
	{
		return codeBlockSign;
	}

	// Do some checks to avoid exposing internal structures

	public boolean isOrderedListSuffix(char c)
	{
		// Currently only one, we might add alternatives
		return c == ORDERED_LIST_SUFFIX;
	}
	public boolean isValidURLChar(char c)
	{
		return CharacterCheck.isAlphaNumerical(c) || CharacterCheck.isOneOf(c, VALID_URL_CHARS);
	}
}
