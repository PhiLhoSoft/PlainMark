package org.philhosoft.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * As GWT (as of 2.6 version) doesn't support Deque implementations, I make my own, simple and limited version...
 */
public class SimpleStack<T> implements Iterable<T> // Doesn't implement Collection which has lot of methods I don't use here
{
	List<T> stack = new ArrayList<T>();

	public void push(T item)
	{
		stack.add(item);
	}

	/**
	 * Pops an element from the stack.
     * Ie. it removes and returns the first (top) element of this stack.
     *
	 * @return the element at the front of this queue (the top of the stack)
     * @throws NoSuchElementException if the stack is empty
	 */
	public T pop()
	{
		T top = poll();
		if (top == null)
			throw new NoSuchElementException();
		return top;
	}

	/**
	 * Retrieves and removes the the first (top) element of this stack,
	 * or <tt>null</tt> if the stack is empty.
	 *
	 * @return the first element element of this stack,
	 *          or <tt>null</tt> if this stack is empty
	 */
	public T poll()
	{
		if (stack.isEmpty())
			return null;
		T top = peek();
		stack.remove(stack.size() - 1);
		return top;
	}

	/**
	 * Retrieves and removes the the last (bottom) element of this stack,
     * or <tt>null</tt> if the stack is empty.
     *
	 * @return the last element element of this stack,
     *          or <tt>null</tt> if this stack is empty
	 */
	public T pollLast()
	{
		if (stack.isEmpty())
			return null;
		T last = stack.get(0);
		stack.remove(0);
		return last;
	}

	public T peek()
	{
		if (stack.isEmpty())
			return null;
		return stack.get(stack.size() - 1);
	}

	public int size()
	{
		return stack.size();
	}

	public boolean isEmmpty()
	{
		return stack.isEmpty();
	}

	public void clear()
	{
		stack.clear();
	}

	@Override
	public Iterator<T> iterator()
	{
		return new StackIterator();
	}

	@Override
	public int hashCode()
	{
		return stack.hashCode();
	}
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;
		if (!(obj instanceof SimpleStack))
			return false;
		return stack.equals(((SimpleStack<T>) obj).stack);
	}
	@Override
	public String toString()
	{
		return stack.toString();
	}

	private class StackIterator implements Iterator<T>
	{
		private int cursor = stack.size() - 1;

		@Override
		public boolean hasNext()
		{
			return cursor >= 0;
		}

		@Override
		public T next()
		{
			return stack.get(cursor--);
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException(); // We said "simple and focused"! :-)
		}
	}
}
