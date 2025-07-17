package structures;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class CustomLinkedList<E> implements CustomList<E> {
  private Node<E> head;
  private Node<E> tail;
  private int size;

  public CustomLinkedList() {
    head = null;
    tail = null;
    size = 0;
  }

  private static class Node<E> {
    E item;
    Node<E> next;
    Node<E> prev;

    Node(Node<E> prev, E element, Node<E> next) {
      this.item = element;
      this.next = next;
      this.prev = prev;
    }
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public boolean isEmpty() {
    return size == 0;
  }

  @Override
  public boolean add(E e) {
    linkLast(e);
    return true;
  }

  @Override
  public void add(int index, E element) {
    if (index == size) {
      linkLast(element);
    } else {
      linkBefore(element, getNode(index));
    }
  }

  @Override
  public E get(int index) {
    return getNode(index).item;
  }

  @Override
  public E set(int index, E element) {
    Node<E> node = getNode(index);
    E oldValue = node.item;
    node.item = element;
    return oldValue;
  }

  @Override
  public E remove(int index) {
    return unlink(getNode(index));
  }

  @Override
  public boolean remove(Object o) {
    for (Node<E> node = head; node != null; node = node.next) {
      if (o == null ? node.item == null : o.equals(node.item)) {
        unlink(node);
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean contains(Object o) {
    return indexOf(o) >= 0;
  }

  @Override
  public int indexOf(Object o) {
    int index = 0;
    for (Node<E> node = head; node != null; node = node.next) {
      if (o == null ? node.item == null : o.equals(node.item)) {
        return index;
      }
      index++;
    }
    return -1;
  }

  @Override
  public void clear() {
    for (Node<E> node = head; node != null;) {
      Node<E> next = node.next;
      node.item = null;
      node.next = null;
      node.prev = null;
      node = next;
    }
    head = tail = null;
    size = 0;
  }

  @Override
  public void sort(Comparator<? super E> c) {
    throw new UnsupportedOperationException("Sort not implemented for LinkedList");
  }

  @Override
  public CustomList<E> subList(int fromIndex, int toIndex) {
    throw new UnsupportedOperationException("subList not implemented for LinkedList");
  }

  @Override
  public boolean addAll(CustomList<? extends E> c) {
    for (E e : c) {
      add(e);
    }
    return !c.isEmpty();
  }

  @Override
  public Iterator<E> iterator() {
    return new Iterator<>() {
      private Node<E> current = head;

      @Override
      public boolean hasNext() {
        return current != null;
      }

      @Override
      public E next() {
        if (!hasNext())
          throw new NoSuchElementException();
        E item = current.item;
        current = current.next;
        return item;
      }
    };
  }

  private void linkLast(E e) {
    Node<E> newNode = new Node<>(tail, e, null);
    if (tail == null) {
      head = newNode;
    } else {
      tail.next = newNode;
    }
    tail = newNode;
    size++;
  }

  private void linkBefore(E e, Node<E> successor) {
    Node<E> predecessor = successor.prev;
    Node<E> newNode = new Node<>(predecessor, e, successor);
    successor.prev = newNode;
    if (predecessor == null) {
      head = newNode;
    } else {
      predecessor.next = newNode;
    }
    size++;
  }

  private E unlink(Node<E> node) {
    E element = node.item;
    Node<E> prev = node.prev;
    Node<E> next = node.next;

    if (prev == null) {
      head = next;
    } else {
      prev.next = next;
    }

    if (next == null) {
      tail = prev;
    } else {
      next.prev = prev;
    }

    node.item = null;
    node.prev = null;
    node.next = null;
    size--;
    return element;
  }

  private Node<E> getNode(int index) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    Node<E> current;
    if (index < (size >> 1)) {
      current = head;
      for (int i = 0; i < index; i++) {
        current = current.next;
      }
    } else {
      current = tail;
      for (int i = size - 1; i > index; i--) {
        current = current.prev;
      }
    }
    return current;
  }
}