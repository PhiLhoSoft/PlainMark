package org.philhosoft.parser.simplemark;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.philhosoft.formattedtext.ast.TextFragment;
import org.philhosoft.parser.StringWalker;


public class TestFragmentFSA
{
	@Test
	public void testPlain()
	{
		assertThat(FragmentFSA.parse(new StringWalker(""))).isEqualTo(new TextFragment(""));
		assertThat(FragmentFSA.parse(new StringWalker("Simple plain text"))).isEqualTo(new TextFragment("Simple plain text"));
	}

	@Test
	public void testMultiline()
	{
		// Fragment parser stops on a line end
		StringWalker walker = new StringWalker("Two\nLines");
		assertThat(FragmentFSA.parse(walker)).isEqualTo(new TextFragment("Two"));
		assertThat(walker.atLineStart()).isTrue();
		assertThat(walker.current()).isEqualTo('L');
	}
}
