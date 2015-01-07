package org.philhosoft.parser;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;


public class TestStringWalker
{
	@Test
	public void testSimple()
	{
		String s = "Simple";
		StringWalker walker = new StringWalker(s);

		assertThat(walker.hasMore()).isTrue();
		assertThat(walker.atLineEnd()).isFalse();
		assertThat(walker.atLineStart()).isTrue();

		assertThat(walker.previous()).isEqualTo('\0');
		assertThat(walker.current()).isEqualTo('S');
		assertThat(walker.next()).isEqualTo('i');

		assertThat(walker.match('S')).isTrue();
		assertThat(walker.match('s')).isFalse();
		assertThat(walker.match('S', 'i')).isTrue();
		assertThat(walker.match('S', 'I')).isFalse();
		assertThat(walker.match("Simple")).isTrue();
		assertThat(walker.matchAt(3, "ple")).isTrue();
		assertThat(walker.match("Sample")).isFalse();
		assertThat(walker.matchAt(3, "Simple")).isFalse();

		walker.forward();

		assertThat(walker.hasMore()).isTrue();
		assertThat(walker.atLineEnd()).isFalse();
		assertThat(walker.atLineStart()).isFalse();

		assertThat(walker.previous()).isEqualTo('S');
		assertThat(walker.current()).isEqualTo('i');
		assertThat(walker.next()).isEqualTo('m');

		assertThat(walker.match('i')).isTrue();
		assertThat(walker.match('S')).isFalse();
		assertThat(walker.match('i', 'm')).isTrue();
		assertThat(walker.match('i', 'x')).isFalse();
		assertThat(walker.match("impl")).isTrue();
		assertThat(walker.matchAt(2, "ple")).isTrue();
		assertThat(walker.match("mple")).isFalse();
		assertThat(walker.matchAt(2, "ample")).isFalse();

		walker.forward(3);

		assertThat(walker.hasMore()).isTrue();
		assertThat(walker.atLineEnd()).isFalse();
		assertThat(walker.atLineStart()).isFalse();

		assertThat(walker.previous()).isEqualTo('p');
		assertThat(walker.current()).isEqualTo('l');
		assertThat(walker.next()).isEqualTo('e');

		assertThat(walker.match('l')).isTrue();
		assertThat(walker.match('x')).isFalse();
		assertThat(walker.match('l', 'e')).isTrue();
		assertThat(walker.match('x', 'x')).isFalse();
		assertThat(walker.match("le")).isTrue();
		assertThat(walker.matchAt(0, "le")).isTrue();
		assertThat(walker.match("le  ")).isFalse();
		assertThat(walker.match("leet")).isFalse();
		assertThat(walker.matchAt(1, "le")).isFalse();

		walker.forward(2);

		assertThat(walker.hasMore()).isFalse();
		assertThat(walker.atLineEnd()).isTrue();
		assertThat(walker.atLineStart()).isFalse();

		assertThat(walker.previous()).isEqualTo('e');
		assertThat(walker.current()).isEqualTo('\0');
		assertThat(walker.next()).isEqualTo('\0');

		assertThat(walker.match('x')).isFalse();
		assertThat(walker.match('x', 'x')).isFalse();
		assertThat(walker.match("meet")).isFalse();
		assertThat(walker.matchAt(10, "meet")).isFalse();

		walker.forward();

		assertThat(walker.hasMore()).isFalse();
		assertThat(walker.atLineEnd()).isTrue();
		assertThat(walker.atLineStart()).isFalse();

		assertThat(walker.previous()).isEqualTo('\0');
		assertThat(walker.current()).isEqualTo('\0');
		assertThat(walker.next()).isEqualTo('\0');

		assertThat(walker.match('x')).isFalse();
		assertThat(walker.match('x', 'x')).isFalse();
		assertThat(walker.match("meet")).isFalse();
		assertThat(walker.matchAt(7, "meet")).isFalse();
	}

	@Test
	public void testSimpleAll()
	{
		String s = "Simple";
		StringWalker walker = new StringWalker(s);

		int c = 0;
		while (walker.hasMore())
		{
//			System.out.println(c);
			assertThat(walker.atLineEnd()).isEqualTo(c == s.length());
			assertThat(walker.atLineStart()).isEqualTo(c == 0);

			assertThat(walker.previous()).isEqualTo(c == 0 ? '\0' : s.charAt(c - 1));
			assertThat(walker.current()).isEqualTo(s.charAt(c));
			assertThat(walker.next()).isEqualTo(c == s.length() - 1 ? '\0' : s.charAt(c + 1));

			walker.forward();
			c++;
		}
		assertThat(c).isEqualTo(s.length());
	}

	@Test
	public void testUnixNewline()
	{
		String s = "Line\nBreak";
		StringWalker walker = new StringWalker(s);

		walker.forward(3);

		assertThat(walker.hasMore()).isTrue();
		assertThat(walker.atLineEnd()).isFalse();
		assertThat(walker.atLineStart()).isFalse();

		assertThat(walker.previous()).isEqualTo('n');
		assertThat(walker.current()).isEqualTo('e');
		assertThat(walker.next()).isEqualTo('\n');

		assertThat(walker.match('e', 'e')).isFalse();
		assertThat(walker.match("en")).isFalse();

		walker.forward();

		assertThat(walker.hasMore()).isTrue();
		assertThat(walker.atLineEnd()).isTrue();
		assertThat(walker.atLineStart()).isFalse();

		assertThat(walker.previous()).isEqualTo('e');
		assertThat(walker.current()).isEqualTo('\n');
		assertThat(walker.next()).isEqualTo('B');

		walker.forward();

		assertThat(walker.hasMore()).isTrue();
		assertThat(walker.atLineEnd()).isFalse();
		assertThat(walker.atLineStart()).isTrue();

		assertThat(walker.previous()).isEqualTo('\n');
		assertThat(walker.current()).isEqualTo('B');
		assertThat(walker.next()).isEqualTo('r');
	}

	@Test
	public void testWindowsNewline()
	{
		String s = "Line\r\nBreak";
		StringWalker walker = new StringWalker(s);

		walker.forward(3);

		assertThat(walker.hasMore()).isTrue();
		assertThat(walker.atLineEnd()).isFalse();
		assertThat(walker.atLineStart()).isFalse();

		assertThat(walker.previous()).isEqualTo('n');
		assertThat(walker.current()).isEqualTo('e');
		assertThat(walker.next()).isEqualTo('\r');

		assertThat(walker.match('e', 'e')).isFalse();
		assertThat(walker.match("en")).isFalse();

		walker.forward();

		assertThat(walker.hasMore()).isTrue();
		assertThat(walker.atLineEnd()).isTrue();
		assertThat(walker.atLineStart()).isFalse();

		assertThat(walker.previous()).isEqualTo('e');
		assertThat(walker.current()).isEqualTo('\r');
		assertThat(walker.next()).isEqualTo('B');

		walker.forward();

		assertThat(walker.hasMore()).isTrue();
		assertThat(walker.atLineEnd()).isFalse();
		assertThat(walker.atLineStart()).isTrue();

		assertThat(walker.previous()).isEqualTo('\r');
		assertThat(walker.current()).isEqualTo('B');
		assertThat(walker.next()).isEqualTo('r');
	}

	@Test
	public void testStartWithNewline()
	{
		String s = "\nLine Break";
		StringWalker walker = new StringWalker(s);

		assertThat(walker.hasMore()).isTrue();
		assertThat(walker.atLineEnd()).isTrue();
		assertThat(walker.atLineStart()).isTrue();

		walker.forward();

		assertThat(walker.hasMore()).isTrue();
		assertThat(walker.atLineEnd()).isFalse();
		assertThat(walker.atLineStart()).isTrue();
	}

	@Test
	public void testSkipline()
	{
		String s = "// Comment\nand new line";
		StringWalker walker = new StringWalker(s);

		assertThat(walker.match("//")).isTrue();

		walker.goToNextLine();

		assertThat(walker.hasMore()).isTrue();
		assertThat(walker.atLineEnd()).isFalse();
		assertThat(walker.atLineStart()).isTrue();
		assertThat(walker.current()).isEqualTo('a');

		walker.forward();

		assertThat(walker.hasMore()).isTrue();
		assertThat(walker.atLineEnd()).isFalse();
		assertThat(walker.atLineStart()).isFalse();
		assertThat(walker.current()).isEqualTo('n');

		walker.goToNextLine();

		assertThat(walker.hasMore()).isFalse();
		assertThat(walker.atLineEnd()).isTrue();
		assertThat(walker.atLineStart()).isFalse();
	}
}
