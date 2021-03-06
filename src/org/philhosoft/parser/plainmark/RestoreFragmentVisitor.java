package org.philhosoft.parser.plainmark;

import org.philhosoft.formattedtext.ast.FragmentDecoration;

// If ParsingParameters allows to change these signs, we will need to get them from there.
public class RestoreFragmentVisitor implements FragmentDecoration.Visitor<StringBuilder>
{
	@Override
	public void visitLink(StringBuilder sb)
	{
		sb.append(ParsingParameters.LINK_START_SIGN);
	}
	@Override
	public void visitStrong(StringBuilder sb)
	{
		sb.append("*");
	}
	@Override
	public void visitEmphasis(StringBuilder sb)
	{
		sb.append("_");
	}
	@Override
	public void visitDelete(StringBuilder sb)
	{
		sb.append("-");
	}
	@Override
	public void visitCode(StringBuilder sb)
	{
		sb.append("`");
	}
}
