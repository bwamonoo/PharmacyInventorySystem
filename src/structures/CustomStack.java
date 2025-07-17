package structures;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class CustomStack<E> implements Iterable<E> {
  private final CustomArrayList<E> list = new CustomArrayList<>();

  public void push(E item) {
    list.add(item);
  }

  public E pop() {
    if (isEmpty())
      return null;
    return list.remove(list.size() - 1);
  }

  public E peek() {
    if (isEmpty())
      return null;
    return list.get(list.size() - 1);
  }

  public boolean isEmpty() {
    return list.isEmpty();
  }

  public int size() {
    return list.size();
  }

  public E get(int index) {
    if (index < 0 || index >= list.size()) {
      throw new IndexOutOfBoundsException();
    }
    return list.get(index);
  }

  @Override
  public Iterator<E> iterator() {
    return new StackIterator();
  }

  private class StackIterator implements Iterator<E> {
    private int currentIndex = list.size() - 1; // Start from top of stack

    @Override
    public boolean hasNext() {
      return currentIndex >= 0;
    }

    @Override
    public E next() {
      if (!hasNext())
        throw new NoSuchElementException("No more elements in stack");
      return list.get(currentIndex--); // Return and move down the stack
    }
  }
}