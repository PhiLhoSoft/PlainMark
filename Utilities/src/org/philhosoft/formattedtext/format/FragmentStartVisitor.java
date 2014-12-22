package org.philhosoft.formattedtext.format;

import org.philhosoft.formattedtext.ast.FragmentDecoration;

public class FragmentStartVisitor implements FragmentDecoration.Visitor<StringBuilder>
{
	@Override
	public void visitStrong(StringBuilder output)
	{
		output.append("<strong>");
	}
	@Override
	public void visitEmphasis(StringBuilder output)
	{
		output.append("<em>");
	}
	@Override
	public void visitDelete(StringBuilder output)
	{
		output.append("<del>");
	}
	@Override
	public void visitCode(StringBuilder output)
	{
		output.append("<code>");
	}
}
