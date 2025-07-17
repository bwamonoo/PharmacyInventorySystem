package structures;

public interface CustomMap<K, V> {
  int size();

  boolean isEmpty();

  boolean containsKey(K key);

  V get(K key);

  V put(K key, V value);

  V remove(K key);

  void clear();

  CustomList<K> keySet();

  CustomList<V> values();

  V getOrDefault(K key, V defaultValue);
}