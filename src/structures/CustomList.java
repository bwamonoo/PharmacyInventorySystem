package structures;

import java.util.Comparator;

public interface CustomList<E> extends Iterable<E> {
  int size();

  boolean isEmpty();

  boolean add(E e);

  void add(int index, E element);

  E get(int index);

  E set(int index, E element);

  E remove(int index);

  boolean remove(Object o);

  boolean contains(Object o);

  int indexOf(Object o);

  void clear();

  void sort(Comparator<? super E> c);

  CustomList<E> subList(int fromIndex, int toIndex);

  boolean addAll(CustomList<? extends E> c);
}