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
	private List<T> stack = new ArrayList<T>();

    /**
     * Pushes an element onto this stack.
     * Ie, adds it on the top of of the stack, as first element.
     *
     * @param item  the item to push
     * @throws NullPointerException if the specified element is null
     */
	public void push(T item)
	{
		if (item == null)
			throw new NullPointerException();
		stack.add(item);
	}

	/**
	 * Pops an element from this stack.
     * Ie. it removes and returns the first (top) element of this stack.
     *
	 * @return the element at the front of this queue (the top of this stack)
     * @throws NoSuchElementException if this stack is empty
	 */
	public T pop()
	{
		T top = poll();
		if (top == null)
			throw new NoSuchElementException();
		return top;
	}

	/**
	 * Retrieves and removes the first (top) element of this stack,
	 * or <tt>null</tt> if this stack is empty.
	 *
	 * @return the first element of this stack,
	 *         or <tt>null</tt> if this stack is empty
	 */
	public T poll()
	{
		if (stack.isEmpty())
			return null;
		T top = stack.remove(stack.size() - 1);
		return top;
	}

	/**
	 * Retrieves and removes the last (bottom) element of this stack,
	 * or <tt>null</tt> if this stack is empty.
	 *
	 * @return the last element of this stack,
	 *         or <tt>null</tt> if this stack is empty
	 */
	public T pollLast()
	{
		if (stack.isEmpty())
			return null;
		T last = stack.remove(0);
		return last;
	}

	/**
	 * Retrieves and removes the nth element of this stack, starting from the first (top / head).
     * Returns <tt>null</tt> if the position is negative or is deeper than this stack.
     *
	 * @return the nth element of this stack,
     *         or <tt>null</tt> if this stack is not big enough
	 */
	public T pollAt(int position)
	{
		if (position < 0 || position > stack.size() - 1)
			return null;
		T element = stack.remove(stack.size() - 1 - position);
		return element;
	}

	/**
	 * Retrieves and removes the first (top) element of this stack,
	 * or <tt>null</tt> if this stack is empty.
	 *
	 * @return the first element of this stack,
	 *         or <tt>null</tt> if this stack is empty
	 */
	public T peek()
	{
		if (stack.isEmpty())
			return null;
		return stack.get(stack.size() - 1);
	}

	/**
	 * Retrieves the last (bottom) element of this stack,
     * or <tt>null</tt> if this stack is empty.
     *
	 * @return the last element of this stack,
     *         or <tt>null</tt> if this stack is not big enough
	 */
	public T peekLast()
	{
		if (stack.isEmpty())
			return null;
		return stack.get(0);
	}
	/**
	 * Retrieves the nth element of this stack, starting from the first (top / head).
     * Returns <tt>null</tt> if the position is negative or is deeper than this stack.
     *
	 * @return the nth element of this stack,
     *         or <tt>null</tt> if this stack is empty
	 */
	public T peekAt(int position)
	{
		if (position < 0 || position > stack.size() - 1)
			return null;
		return stack.get(stack.size() - 1 - position);
	}

	public int size()
	{
		return stack.size();
	}

	public boolean isEmpty()
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
			if (!hasNext())
				throw new NoSuchElementException();
			return stack.get(cursor--);
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException(); // We said "simple and focused"! :-)
		}
	}
}
