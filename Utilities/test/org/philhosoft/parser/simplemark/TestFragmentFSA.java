package org.philhosoft.parser.simplemark;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.philhosoft.ast.formattedtext.PlainTextFragment;


public class TestFragmentFSA
{
	@Test
	public void test()
	{
		assertThat(FragmentFSA.parse("Simple plain text")).isEqualTo(new PlainTextFragment("Simple plain text"));
	}
}
