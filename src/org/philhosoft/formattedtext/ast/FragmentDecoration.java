package org.philhosoft.formattedtext.ast;


public enum FragmentDecoration
{
	LINK
	{
		@Override
		public <T> void accept(Visitor<T> visitor, T output)
		{
			visitor.visitLink(output);
		}
	},
	STRONG // Bold
	{
		@Override
		public <T> void accept(Visitor<T> visitor, T output)
		{
			visitor.visitStrong(output);
		}
	},
	EMPHASIS // Italic
	{
		@Override
		public <T> void accept(Visitor<T> visitor, T output)
		{
			visitor.visitEmphasis(output);
		}
	},
	DELETE // Strike through
	{
		@Override
		public <T> void accept(Visitor<T> visitor, T output)
		{
			visitor.visitDelete(output);
		}
	},
	CODE // Fixed width
	{
		@Override
		public <T> void accept(Visitor<T> visitor, T output)
		{
			visitor.visitCode(output);
		}
	};

	public interface Visitor<T>
	{
		void visitLink(T output);
		void visitStrong(T output);
		void visitEmphasis(T output);
		void visitDelete(T output);
		void visitCode(T output);
	}

	public abstract <T> void accept(Visitor<T> visitor, T output);
}
