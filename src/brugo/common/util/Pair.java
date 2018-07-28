package brugo.common.util;


import java.io.Serializable;
import java.util.*;

public class Pair<K, V> implements Serializable {
  private K key;
  private V value;

  public Pair(K pKey, V pValue) {
    value = pValue;
    key = pKey;
  }

  public final K getKey() {
    return key;
  }

  public final K setKey(K pNewKey) {
    K oldKey = key;
    key = pNewKey;
    return oldKey;
  }

  public final V getValue() {
    return value;
  }

  public final V setValue(V pNewValue) {
    V oldValue = value;
    value = pNewValue;
    return oldValue;
  }

  @Override
  public final boolean equals(Object obj) {
    if (!(obj instanceof Pair)) return false;
    Pair e = (Pair) obj;
    Object k1 = getKey();
    Object k2 = e.getKey();
    if ((k1 == k2) || ((k1 != null) && k1.equals(k2))) {
      Object v1 = getValue();
      Object v2 = e.getValue();
      if ((v1 == v2) || ((v1 != null) && v1.equals(v2))) return true;
    }
    return false;
  }

  @Override
  public final int hashCode() {
    return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
  }

  @Override
  public final String toString() {
    return getKey() + "=" + getValue();
  }

  public static <S, T> Set<S> getKeySet(Collection<Pair<S, T>> entries) {
    if (entries == null) return null;
    Set<S> keySet = new HashSet<>(entries.size());

    for (Pair<S, T> entry : entries) {
      if (entry.getValue() == null) continue;
      keySet.add(entry.getKey());
    }
    return (keySet.isEmpty()) ? null : keySet;
  }

  public static <S, T> List<T> getValueList(Collection<Pair<S, T>> entries) {
    if (entries == null) return null;
    List<T> valueList = new ArrayList<>(entries.size());

    for (Pair<S, T> entry : entries) {
      if (entry.getValue() == null) continue;
      valueList.add(entry.getValue());
    }
    return (valueList.isEmpty()) ? null : valueList;
  }
}