package org.philhosoft.parser.simplemark;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.philhosoft.formattedtext.ast.BlockType;
import org.philhosoft.formattedtext.ast.FragmentDecoration;

public class ParsingParameters
{
	public static final char LINK_START_SIGN = '[';
	public static final char LINK_END_SIGN = ']';
	public static final char URL_START_SIGN = '(';
	public static final char URL_END_SIGN = ')';

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
		decorations.put('*', FragmentDecoration.STRONG);
		decorations.put('_', FragmentDecoration.EMPHASIS);
		decorations.put('-', FragmentDecoration.DELETE);
		decorations.put('`', FragmentDecoration.CODE);
	}
	private static final Map<String, BlockType> blockTypesPerPrefix = new HashMap<String, BlockType>();
	{
		blockTypesPerPrefix.put("# ", BlockType.TITLE1);
		blockTypesPerPrefix.put("## ", BlockType.TITLE2);
		blockTypesPerPrefix.put("### ", BlockType.TITLE3);
		blockTypesPerPrefix.put("* ", BlockType.LIST_ITEM);
		blockTypesPerPrefix.put("- ", BlockType.LIST_ITEM);
		blockTypesPerPrefix.put("+ ", BlockType.LIST_ITEM);
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
	public BlockType getBlockType(String prefix)
	{
		return blockTypesPerPrefix.get(prefix);
	}
	public Set<String> getBlockTypePrefixes()
	{
		return blockTypesPerPrefix.keySet();
	}
	public String getCodeBlockSign()
	{
		return codeBlockSign;
	}
}
