package org.philhosoft.formattedtext.ast;

public enum BlockType
{
	DOCUMENT
	{
		@Override
		public <T> void accept(Visitor<T> visitor, T output)
		{
			visitor.visitDocument(output);
		}
	},
	PARAGRAPH
	{
		@Override
		public <T> void accept(Visitor<T> visitor, T output)
		{
			visitor.visitParagraph(output);
		}
	},
	TITLE1
	{
		@Override
		public <T> void accept(Visitor<T> visitor, T output)
		{
			visitor.visitTitle1(output);
		}
	},
	TITLE2
	{
		@Override
		public <T> void accept(Visitor<T> visitor, T output)
		{
			visitor.visitTitle2(output);
		}
	},
	TITLE3
	{
		@Override
		public <T> void accept(Visitor<T> visitor, T output)
		{
			visitor.visitTitle3(output);
		}
	},
	CODE
	{
		@Override
		public <T> void accept(Visitor<T> visitor, T output)
		{
			visitor.visitCode(output);
		}
	},
	UNORDERED_LIST
	{
		@Override
		public <T> void accept(Visitor<T> visitor, T output)
		{
			visitor.visitUnorderedList(output);
		}
	},
	ORDERED_LIST
	{
		@Override
		public <T> void accept(Visitor<T> visitor, T output)
		{
			visitor.visitOrderedList(output);
		}
	},
	LIST_ITEM_BULLET
	{
		@Override
		public <T> void accept(Visitor<T> visitor, T output)
		{
			visitor.visitListItemBullet(output);
		}
	},
	LIST_ITEM_NUMBER
	{
		@Override
		public <T> void accept(Visitor<T> visitor, T output)
		{
			visitor.visitListItemNumber(output);
		}
	};

	public interface Visitor<T>
	{
		void visitDocument(T output);
		void visitParagraph(T output);
		void visitTitle1(T output);
		void visitTitle2(T output);
		void visitTitle3(T output);
		void visitCode(T output);
		void visitUnorderedList(T output);
		void visitOrderedList(T output);
		void visitListItemBullet(T output);
		void visitListItemNumber(T output);
	}

	public abstract <T> void accept(Visitor<T> visitor, T output);
}
