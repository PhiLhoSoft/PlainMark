package org.philhosoft.formattedtext.ast;

public interface MarkupVisitor<T>
{
	void visit(DecoratedFragment fragment, T context);

	void visit(LinkFragment fragment, T context);

	void visit(TextFragment fragment, T context);

	void visit(TypedBlock typedBlock, T context);

	void visit(Line line, T context);
}
