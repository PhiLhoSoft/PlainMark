package org.philhosoft.collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.NoSuchElementException;

import org.junit.Test;


public class TestSimpleStack
{
	@Test
	public void testRegular()
	{
		SimpleStack<String> stack = new SimpleStack<String>();

		assertThat(stack).isEmpty();
		assertThat(stack.size()).isEqualTo(0);
		assertThat(stack.peek()).isNull();
		assertThat(stack.poll()).isNull();
		try
		{
			stack.pop();
			fail("Should have thrown an exception");
		}
		catch (NoSuchElementException e)
		{
		}
		assertThat(stack.peekLast()).isNull();
		assertThat(stack.pollLast()).isNull();
		assertThat(stack.peekAt(0)).isNull();
		assertThat(stack.pollAt(0)).isNull();

		for (String s : stack)
		{
			fail("Should not enter the loop! " + s);
		}

		stack.push("Foo");

		assertThat(stack).isNotEmpty();
		assertThat(stack.size()).isEqualTo(1);
		assertThat(stack.peek()).isEqualTo("Foo");
		assertThat(stack.peekLast()).isEqualTo("Foo");
		assertThat(stack.peekAt(0)).isEqualTo("Foo");

		for (String s : stack)
		{
			assertThat(s).isEqualTo("Foo");
		}

		stack.push("Bar");
		stack.push("Doh");

		assertThat(stack).isNotEmpty();
		assertThat(stack.size()).isEqualTo(3);
		assertThat(stack.peek()).isEqualTo("Doh");

		int i = 0;
		for (String s : stack)
		{
			switch (i)
			{
			case 0:
				assertThat(s).isEqualTo("Doh");
				break;
			case 1:
				assertThat(s).isEqualTo("Bar");
				break;
			case 2:
				assertThat(s).isEqualTo("Foo");
				break;
			default:
				fail("Too much elements!");
			}
			i++;
		}
		assertThat(stack.peekLast()).isEqualTo("Foo");
		assertThat(stack.peekAt(2)).isEqualTo("Foo");
		assertThat(stack.peekAt(1)).isEqualTo("Bar");
		assertThat(stack.peekAt(0)).isEqualTo("Doh");
		assertThat(stack.peekAt(3)).isNull();
		assertThat(stack.peekAt(-1)).isNull();

		String pop1 = stack.pop();

		assertThat(pop1).isEqualTo("Doh");
		assertThat(stack).isNotEmpty();
		assertThat(stack.size()).isEqualTo(2);
		assertThat(stack.peek()).isEqualTo("Bar");

		String pop2 = stack.pop();

		assertThat(pop2).isEqualTo("Bar");
		assertThat(stack).isNotEmpty();
		assertThat(stack.size()).isEqualTo(1);
		assertThat(stack.peek()).isEqualTo("Foo");

		String pop3 = stack.pop();

		assertThat(pop3).isEqualTo("Foo");
		assertThat(stack).isEmpty();
		assertThat(stack.size()).isEqualTo(0);
		assertThat(stack.peek()).isNull();
		assertThat(stack.poll()).isNull();

		try
		{
			stack.pop();
			fail("Should have thrown an exception");
		}
		catch (NoSuchElementException e)
		{
		}
		assertThat(stack).isEmpty();
		assertThat(stack.size()).isEqualTo(0);
		assertThat(stack.peek()).isNull();
	}

	@Test
	public void testPollLast()
	{
		SimpleStack<String> stack = new SimpleStack<String>();
		stack.push("Bar B");
		stack.push("Middle");
		stack.push("Doh D");

		assertThat(stack).isNotEmpty();
		assertThat(stack.size()).isEqualTo(3);
		assertThat(stack.peek()).isEqualTo("Doh D");
		assertThat(stack.peekLast()).isEqualTo("Bar B");

		String polled = stack.pollLast();

		assertThat(polled).isEqualTo("Bar B");
		assertThat(stack.peek()).isEqualTo("Doh D");
		assertThat(stack).isNotEmpty();
		assertThat(stack.size()).isEqualTo(2);

		stack.clear();

		assertThat(stack).isEmpty();
		assertThat(stack.size()).isEqualTo(0);
	}

	@Test
	public void testPeekPollAt()
	{
		SimpleStack<String> stack = new SimpleStack<String>();
		stack.push("Shi");
		stack.push("San");
		stack.push("Ni");
		stack.push("Ichi");

		assertThat(stack.peekAt(3)).isEqualTo("Shi");
		assertThat(stack.pollAt(3)).isEqualTo("Shi");
		assertThat(stack.size()).isEqualTo(3);
		assertThat(stack.peekAt(2)).isEqualTo("San");
		assertThat(stack.pollAt(2)).isEqualTo("San");
		assertThat(stack.size()).isEqualTo(2);
		assertThat(stack.peekAt(0)).isEqualTo("Ichi");
		assertThat(stack.pollAt(0)).isEqualTo("Ichi");
		assertThat(stack.size()).isEqualTo(1);
		assertThat(stack.peekAt(0)).isEqualTo("Ni");
		assertThat(stack.pollAt(0)).isEqualTo("Ni");
		assertThat(stack).isEmpty();
	}
}
