package org.philhosoft.formattedtext.ast;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class TestMarkedText
{
	@Test
	public void testTextFragment() throws Exception
	{
		TextFragment etf1 = new TextFragment("");
		TextFragment etf2 = new TextFragment("");

		TextFragment tf1 = new TextFragment("Foo");
		TextFragment tf2 = new TextFragment("Foo");

		assertThat(etf1).isEqualTo(etf2);
		assertThat(tf1).isEqualTo(tf2);
	}

	@Test
	public void testLine() throws Exception
	{
		Line el1 = new Line();
		Line el2 = new Line();
		Line l1 = new Line(new TextFragment("Foo"));
		Line l2 = new Line(new TextFragment("Foo"));

		assertThat(el1).isEqualTo(el2);
		assertThat(l1).isEqualTo(l2);
	}
}
