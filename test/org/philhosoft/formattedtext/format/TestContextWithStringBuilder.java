package org.philhosoft.formattedtext.format;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.philhosoft.formattedtext.ast.Block;
import org.philhosoft.formattedtext.ast.Line;


public class TestContextWithStringBuilder
{
	@Test
	public void testStack() throws Exception
	{
		ContextWithStringBuilder context = new ContextWithStringBuilder();

		Block b = new Line();
		context.push(b, false, true);
		context.push(b, true, true);
		context.push(b, true, false);
		context.push(b, false, false);

		assertThat(context.isFirst()).isFalse();
		assertThat(context.isLast()).isFalse();

		context.pop();

		assertThat(context.isFirst()).isTrue();
		assertThat(context.isLast()).isFalse();

		context.pop();

		assertThat(context.isFirst()).isTrue();
		assertThat(context.isLast()).isTrue();

		context.pop();

		assertThat(context.isFirst()).isFalse();
		assertThat(context.isLast()).isTrue();

		context.pop();

		assertThat(context.isFirst()).isTrue();
		assertThat(context.isLast()).isTrue();
	}

	@Test
	public void testUpdate() throws Exception
	{
		ContextWithStringBuilder context = new ContextWithStringBuilder();

		Block b = new Line();
		context.push(b, false, false);
		context.push(b, true, true);

		assertThat(context.isFirst()).isTrue();
		assertThat(context.isLast()).isTrue();

		context.setFirstLast(true, false);

		assertThat(context.isFirst()).isTrue();
		assertThat(context.isLast()).isFalse();

		context.pop();

		assertThat(context.isFirst()).isFalse();
		assertThat(context.isLast()).isFalse();

		context.pop();

		assertThat(context.isFirst()).isTrue();
		assertThat(context.isLast()).isTrue();
	}
}
