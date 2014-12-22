package org.philhosoft.formattedtext.format;

import org.philhosoft.formattedtext.ast.Block;
import org.philhosoft.formattedtext.ast.BlockType;
import org.philhosoft.formattedtext.ast.DecoratedFragment;
import org.philhosoft.formattedtext.ast.Fragment;
import org.philhosoft.formattedtext.ast.FragmentDecoration;
import org.philhosoft.formattedtext.ast.Line;
import org.philhosoft.formattedtext.ast.LinkFragment;
import org.philhosoft.formattedtext.ast.MarkupVisitor;
import org.philhosoft.formattedtext.ast.TextFragment;
import org.philhosoft.formattedtext.ast.TypedBlock;

public class HTMLVisitor implements MarkupVisitor<StringBuilder>
{
	private FragmentDecoration.Visitor<StringBuilder> fragmentStartVisitor = new FragmentDecoration.Visitor<StringBuilder>()
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
	};
	private FragmentDecoration.Visitor<StringBuilder> fragmentEndVisitor = new FragmentDecoration.Visitor<StringBuilder>()
	{
		@Override
		public void visitStrong(StringBuilder output)
		{
			output.append("</strong>");
		}
		@Override
		public void visitEmphasis(StringBuilder output)
		{
			output.append("</em>");
		}
		@Override
		public void visitDelete(StringBuilder output)
		{
			output.append("</del>");
		}
		@Override
		public void visitCode(StringBuilder output)
		{
			output.append("</code>");
		}
	};
	private BlockType.Visitor<StringBuilder> blockStartVisitor = new BlockType.Visitor<StringBuilder>()
	{
		@Override
		public void visitDocument(StringBuilder output)
		{
			output.append("<div>");
		}
		@Override
		public void visitParagraph(StringBuilder output)
		{
			output.append("<p>");
		}
		@Override
		public void visitTitle1(StringBuilder output)
		{
			output.append("<h3>");
		}
		@Override
		public void visitTitle2(StringBuilder output)
		{
			output.append("<h4>");
		}
		@Override
		public void visitTitle3(StringBuilder output)
		{
			output.append("<h5>");
		}
		@Override
		public void visitUnorderedList(StringBuilder output)
		{
			output.append("<ul>");
		}
		@Override
		public void visitOrderedList(StringBuilder output)
		{
			output.append("<ol>");
		}
		@Override
		public void visitListItem(StringBuilder output)
		{
			output.append("<li>");
		}
		@Override
		public void visitCode(StringBuilder output)
		{
			output.append("<pre><code>");
		}
	};
	private BlockType.Visitor<StringBuilder> blockStartVisitor = new BlockType.Visitor<StringBuilder>()
	{
		@Override
		public void visitDocument(StringBuilder output)
		{
			output.append("</div>");
		}
		@Override
		public void visitParagraph(StringBuilder output)
		{
			output.append("</p>");
		}
		@Override
		public void visitTitle1(StringBuilder output)
		{
			output.append("</h3>");
		}
		@Override
		public void visitTitle2(StringBuilder output)
		{
			output.append("</h4>");
		}
		@Override
		public void visitTitle3(StringBuilder output)
		{
			output.append("</h5>");
		}
		@Override
		public void visitUnorderedList(StringBuilder output)
		{
			output.append("</ul>");
		}
		@Override
		public void visitOrderedList(StringBuilder output)
		{
			output.append("</ol>");
		}
		@Override
		public void visitListItem(StringBuilder output)
		{
			output.append("</li>");
		}
		@Override
		public void visitCode(StringBuilder output)
		{
			output.append("</code></pre>");
		}
	};

	@Override
	public void visit(DecoratedFragment fragment, StringBuilder output)
	{
		for (Fragment f : fragment.getFragments())
		{
			f.accept(this, output);
		}
	}

	@Override
	public void visit(TextFragment fragment, StringBuilder output)
	{
		output.append(fragment.getText());
	}

	@Override
	public void visit(LinkFragment fragment, StringBuilder output)
	{
		output.append("<a href='").append(fragment.getUrl()).append("'>");
		for (Fragment f : fragment.getFragments())
		{
			f.accept(this, output);
		}
		output.append("</a>");
	}

	@Override
	public void visit(TypedBlock typedBlock, StringBuilder output)
	{
		for (Block b : typedBlock.getBlocks())
		{
			b.accept(this, output);
		}
	}

	@Override
	public void visit(Line line, StringBuilder output)
	{
		for (Fragment f : line.getFragments())
		{
			f.accept(this, output);
		}
		output.append("\n");
	}
}
