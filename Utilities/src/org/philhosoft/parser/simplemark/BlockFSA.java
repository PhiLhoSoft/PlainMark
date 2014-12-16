package org.philhosoft.parser.simplemark;

import org.philhosoft.ast.formattedtext.Block;
import org.philhosoft.ast.formattedtext.BlockType;
import org.philhosoft.ast.formattedtext.TypedBlock;
import org.philhosoft.fsa.FiniteStateAutomaton;
import org.philhosoft.fsa.State;
import org.philhosoft.fsa.Transition;
import org.philhosoft.parser.StringWalker;

/**
 * Finite State Automaton for parsing a text with markup.
 */
public class BlockFSA extends FiniteStateAutomaton<Character>
{
	private enum MarkupParsingState implements State
	{ INITIAL, BLABLA, END, ERROR }

	private StringWalker walker;

	private BlockFSA(String markup)
	{
		walker = new StringWalker(markup);
		addStates();
	}

	@Override
	protected Character provide()
	{
		return walker.current();
	}

	public static Block parse(String markup)
	{
		if (markup == null || markup.isEmpty())
			return new TypedBlock(BlockType.DOCUMENT);

		BlockFSA parser = new BlockFSA(markup);
		return parser.parse();
	}

	private Block parse()
	{
		Block block = new TypedBlock(BlockType.DOCUMENT);
		start(MarkupParsingState.INITIAL);
		while (walker.hasMore())
		{
			MarkupParsingState state = (MarkupParsingState) next();
			if (state == MarkupParsingState.ERROR)
				return block;

			walker.forward();
		}

//		MarkupParsingState state = (MarkupParsingState) getState();
		return block;
	}



	private void addStates()
	{
		addState(MarkupParsingState.INITIAL, new InitialAction());
		addState(MarkupParsingState.END, new EndAction());
	}


	private class InitialAction implements Transition<Character>
	{
		@Override
		public State evaluate(Character c)
		{
			if (c == '+' || c == '-')
				return MarkupParsingState.BLABLA;

			if (c == '.')
				return MarkupParsingState.BLABLA;

			return MarkupParsingState.ERROR; // Unexpected char
		}
	}
	private class EndAction implements Transition<Character>
	{
		@Override
		public State evaluate(Character c)
		{
			return MarkupParsingState.ERROR; // Should not have a character beyond the end
		}
	}
}
