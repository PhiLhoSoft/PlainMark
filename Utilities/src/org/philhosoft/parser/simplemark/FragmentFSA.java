package org.philhosoft.parser.simplemark;

import org.philhosoft.ast.formattedtext.Fragment;
import org.philhosoft.ast.formattedtext.PlainTextFragment;
import org.philhosoft.fsa.FiniteStateAutomaton;
import org.philhosoft.fsa.State;
import org.philhosoft.fsa.Transition;
import org.philhosoft.parser.StringWalker;

/**
 * Finite State Automaton for parsing a line made of fragments of text.
 */
public class FragmentFSA extends FiniteStateAutomaton<Character>
{
	private enum MarkupParsingState implements State
	{ INITIAL, PLAIN_TEXT, END }

	private StringWalker walker;

	private FragmentFSA(String markup)
	{
		walker = new StringWalker(markup);
		addStates();
	}

	@Override
	protected Character provide()
	{
		return walker.current();
	}

	public static Fragment parse(String markup)
	{
		if (markup == null || markup.isEmpty())
			return new PlainTextFragment("");

		FragmentFSA parser = new FragmentFSA(markup);
		return parser.parse();
	}

	private Fragment parse()
	{
		Fragment fragment = new PlainTextFragment("");
		StringBuilder sb = new StringBuilder();

		start(MarkupParsingState.INITIAL);
		while (walker.hasMore())
		{
			MarkupParsingState state = (MarkupParsingState) next();
			if (state == MarkupParsingState.PLAIN_TEXT)
			{
				sb.append(walker.current());
			}
			walker.forward();
		}

		fragment = new PlainTextFragment(sb.toString());

//		MarkupParsingState state = (MarkupParsingState) getState();
		return fragment;
	}



	private void addStates()
	{
		addState(MarkupParsingState.INITIAL, new InitialAction());
		addState(MarkupParsingState.PLAIN_TEXT, new PlainTextAction());
		addState(MarkupParsingState.END, new EndAction());
	}


	private class InitialAction implements Transition<Character>
	{
		@Override
		public State evaluate(Character c)
		{
			return MarkupParsingState.PLAIN_TEXT;
		}
	}
	private class PlainTextAction implements Transition<Character>
	{
		@Override
		public State evaluate(Character c)
		{
//			return MarkupParsingState.BLABLA;
//
//			if (c == '.')
//				return MarkupParsingState.BLABLA;
//
			return MarkupParsingState.PLAIN_TEXT;
		}
	}
	private class EndAction implements Transition<Character>
	{
		@Override
		public State evaluate(Character c)
		{
			return MarkupParsingState.END;
		}
	}
}
