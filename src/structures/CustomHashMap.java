package structures;

public class CustomHashMap<K, V> implements CustomMap<K, V> {
  private static final int DEFAULT_CAPACITY = 16;
  private static final float LOAD_FACTOR = 0.75f;
  private Node<K, V>[] table;
  private int size;

  public CustomHashMap() {
    table = new Node[DEFAULT_CAPACITY];
    size = 0;
  }

  private static class Node<K, V> {
    final K key;
    V value;
    Node<K, V> next;

    Node(K key, V value, Node<K, V> next) {
      this.key = key;
      this.value = value;
      this.next = next;
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
  public boolean containsKey(K key) {
    return getNode(key) != null;
  }

  @Override
  public V get(K key) {
    Node<K, V> node = getNode(key);
    return node == null ? null : node.value;
  }

  @Override
  public V put(K key, V value) {
    int index = getIndex(key);
    Node<K, V> node = table[index];

    // update existing value
    while (node != null) {
      if (key == null ? node.key == null : key.equals(node.key)) {
        V oldValue = node.value;
        node.value = value;
        return oldValue;
      }
      node = node.next;
    }

    // add new node
    table[index] = new Node<>(key, value, table[index]);
    size++;

    // check resize
    if ((float) size / table.length > LOAD_FACTOR) {
      resize();
    }

    return null;
  }

  @Override
  public V remove(K key) {
    int index = getIndex(key);
    Node<K, V> node = table[index];
    Node<K, V> prev = null;

    while (node != null) {
      if (key == null ? node.key == null : key.equals(node.key)) {
        if (prev == null) {
          table[index] = node.next;
        } else {
          prev.next = node.next;
        }
        size--;
        return node.value;
      }
      prev = node;
      node = node.next;
    }
    return null;
  }

  @Override
  public void clear() {
    for (int i = 0; i < table.length; i++) {
      table[i] = null;
    }
    size = 0;
  }

  @Override
  public CustomList<K> keySet() {
    CustomArrayList<K> keys = new CustomArrayList<>();
    for (Node<K, V> node : table) {
      while (node != null) {
        keys.add(node.key);
        node = node.next;
      }
    }
    return keys;
  }

  @Override
  public CustomList<V> values() {
    CustomArrayList<V> values = new CustomArrayList<>();
    for (Node<K, V> node : table) {
      while (node != null) {
        values.add(node.value);
        node = node.next;
      }
    }
    return values;
  }

  private Node<K, V> getNode(K key) {
    int index = getIndex(key);
    Node<K, V> node = table[index];
    while (node != null) {
      if (key == null ? node.key == null : key.equals(node.key)) {
        return node;
      }
      node = node.next;
    }
    return null;
  }

  private int getIndex(K key) {
    if (key == null)
      return 0;
    int hash = key.hashCode();
    return (hash & 0x7FFFFFFF) % table.length;
  }

  @SuppressWarnings("unchecked")
  private void resize() {
    Node<K, V>[] oldTable = table;
    table = new Node[table.length * 2];
    size = 0;

    for (Node<K, V> node : oldTable) {
      while (node != null) {
        put(node.key, node.value);
        node = node.next;
      }
    }
  }

  @Override
  public V getOrDefault(K key, V defaultValue) {
    Node<K, V> node = getNode(key);
    return node != null ? node.value : defaultValue;
  }

}