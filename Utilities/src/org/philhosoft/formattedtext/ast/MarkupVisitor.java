package org.philhosoft.formattedtext.ast;

public interface MarkupVisitor<T>
{
	void visit(DecoratedFragment fragment, T output);

	void visit(PlainTextFragment fragment, T output);

	void visit(LinkFragment fragment, T output);

	void visit(TypedBlock typedBlock, T output);

	void visit(Line line, T output);
}
