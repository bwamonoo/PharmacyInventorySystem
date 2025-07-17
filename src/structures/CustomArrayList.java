package structures;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class CustomArrayList<E> implements CustomList<E> {
  private static final int DEFAULT_CAPACITY = 10;
  private E[] elements;
  private int size;

  public CustomArrayList() {
    this(DEFAULT_CAPACITY);
  }

  @SuppressWarnings("unchecked")
  public CustomArrayList(CustomList<? extends E> source) {
    elements = (E[]) new Object[source.size()];
    for (int i = 0; i < source.size(); i++) {
      elements[i] = source.get(i);
    }
    size = source.size();
  }

  @SuppressWarnings("unchecked")
  public CustomArrayList(int initialCapacity) {
    elements = (E[]) new Object[initialCapacity];
    size = 0;
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
    ensureCapacity();
    elements[size++] = e;
    return true;
  }

  @Override
  public void add(int index, E element) {
    rangeCheckForAdd(index);
    ensureCapacity();
    System.arraycopy(elements, index, elements, index + 1, size - index);
    elements[index] = element;
    size++;
  }

  @Override
  public E get(int index) {
    rangeCheck(index);
    return elements[index];
  }

  @Override
  public E set(int index, E element) {
    rangeCheck(index);
    E oldValue = elements[index];
    elements[index] = element;
    return oldValue;
  }

  @Override
  public E remove(int index) {
    rangeCheck(index);
    E removed = elements[index];
    int numMoved = size - index - 1;
    if (numMoved > 0) {
      System.arraycopy(elements, index + 1, elements, index, numMoved);
    }
    elements[--size] = null;
    return removed;
  }

  @Override
  public boolean remove(Object o) {
    int index = indexOf(o);
    if (index >= 0) {
      remove(index);
      return true;
    }
    return false;
  }

  @Override
  public boolean contains(Object o) {
    return indexOf(o) >= 0;
  }

  @Override
  public int indexOf(Object o) {
    for (int i = 0; i < size; i++) {
      if (o == null ? elements[i] == null : o.equals(elements[i])) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public void clear() {
    for (int i = 0; i < size; i++) {
      elements[i] = null;
    }
    size = 0;
  }

  @Override
  public void sort(Comparator<? super E> c) {
    mergeSort(0, size - 1, c);
  }

  private void mergeSort(int low, int high, Comparator<? super E> c) {
    if (low < high) {
      int mid = (low + high) >>> 1;
      mergeSort(low, mid, c);
      mergeSort(mid + 1, high, c);
      merge(low, mid, high, c);
    }
  }

  private void merge(int low, int mid, int high, Comparator<? super E> c) {
    @SuppressWarnings("unchecked")
    E[] temp = (E[]) new Object[high - low + 1];
    int i = low, j = mid + 1, k = 0;

    while (i <= mid && j <= high) {
      if (c.compare(elements[i], elements[j]) <= 0) {
        temp[k++] = elements[i++];
      } else {
        temp[k++] = elements[j++];
      }
    }

    while (i <= mid)
      temp[k++] = elements[i++];
    while (j <= high)
      temp[k++] = elements[j++];

    System.arraycopy(temp, 0, elements, low, temp.length);
  }

  @Override
  public CustomList<E> subList(int fromIndex, int toIndex) {
    subListRangeCheck(fromIndex, toIndex);
    CustomArrayList<E> sublist = new CustomArrayList<>(toIndex - fromIndex);
    System.arraycopy(elements, fromIndex, sublist.elements, 0, toIndex - fromIndex);
    sublist.size = toIndex - fromIndex;
    return sublist;
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
      private int cursor = 0;

      @Override
      public boolean hasNext() {
        return cursor < size;
      }

      @Override
      public E next() {
        if (!hasNext())
          throw new NoSuchElementException();
        return elements[cursor++];
      }
    };
  }

  private void ensureCapacity() {
    if (size == elements.length) {
      int newCapacity = elements.length * 2;
      @SuppressWarnings("unchecked")
      E[] newArray = (E[]) new Object[newCapacity];
      System.arraycopy(elements, 0, newArray, 0, size);
      elements = newArray;
    }
  }

  private void rangeCheck(int index) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }
  }

  private void rangeCheckForAdd(int index) {
    if (index < 0 || index > size) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }
  }

  private void subListRangeCheck(int fromIndex, int toIndex) {
    if (fromIndex < 0 || toIndex > size || fromIndex > toIndex) {
      throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public String toString() {
    if (size == 0) {
      return "[]";
    }
    StringBuilder sb = new StringBuilder();
    sb.append('[');
    // append first element
    sb.append(elements[0]);
    // append remaining with comma+space
    for (int i = 1; i < size; i++) {
      sb.append(", ").append(elements[i]);
    }
    sb.append(']');
    return sb.toString();
  }

}