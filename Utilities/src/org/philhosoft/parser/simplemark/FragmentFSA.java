package org.philhosoft.parser.simplemark;

import org.philhosoft.formattedtext.ast.Fragment;
import org.philhosoft.formattedtext.ast.PlainTextFragment;
import org.philhosoft.fsa.FiniteStateAutomaton;
import org.philhosoft.fsa.State;
import org.philhosoft.fsa.Transition;
import org.philhosoft.parser.StringWalker;

/**
 * Finite State Automaton for parsing a line made of fragments of text.
 * <p>
 * Made to be complementary of a line parser, which will feed this automaton with a single line.
 * If a newline is found in the given text, the automaton ends.
 */
public class FragmentFSA extends FiniteStateAutomaton<StringWalker>
{
	private enum MarkupParsingState implements State
	{ INITIAL, PLAIN_TEXT, END }

	private StringWalker walker;

	private FragmentFSA(StringWalker walker)
	{
		this.walker = walker;
		addStates();
	}

	@Override
	protected StringWalker provide()
	{
		return walker;
	}

	public static Fragment parse(StringWalker walker)
	{
		FragmentFSA parser = new FragmentFSA(walker);
		return parser.parse();
	}

	private Fragment parse()
	{
		Fragment fragment = new PlainTextFragment("");
		StringBuilder sb = new StringBuilder();

		MarkupParsingState state = MarkupParsingState.INITIAL;
		start(state);
		while (walker.hasMore() && state != MarkupParsingState.END)
		{
			state = (MarkupParsingState) next();
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


	private class InitialAction implements Transition<StringWalker>
	{
		@Override
		public State evaluate(StringWalker walker)
		{
			return MarkupParsingState.PLAIN_TEXT;
		}
	}
	private class PlainTextAction implements Transition<StringWalker>
	{
		@Override
		public State evaluate(StringWalker walker)
		{
			if (walker.atLineEnd())
				return MarkupParsingState.END;

			return MarkupParsingState.PLAIN_TEXT;
		}
	}
	private class EndAction implements Transition<StringWalker>
	{
		@Override
		public State evaluate(StringWalker walker)
		{
			return MarkupParsingState.END;
		}
	}
}
