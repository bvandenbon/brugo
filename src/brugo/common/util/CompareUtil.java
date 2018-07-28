package brugo.common.util;

public final class CompareUtil {
  private CompareUtil() {
  }

  public static <T> int compareTo(Comparable<T> obj1, T obj2) {
    if (obj1 == obj2) return 0;
    if (obj1 == null) return -1;
    if (obj2 == null) return 1;
    if (obj1.equals(obj2)) return 0;
    return (obj1.compareTo(obj2));
  }

  public static <T> int compareTo(Comparable<T>[] a1, T[] a2) {
    if (a1 == a2) return 0;
    if (a1 == null) return -1;
    if (a2 == null) return -1;
    if (a1.length < a2.length) return -1;
    if (a2.length < a1.length) return -1;

    int comp;
    for (int i = 0; i < a1.length; i++) {
      Comparable<T> e1 = a1[i];
      T e2 = a2[i];
      comp = compareTo(e1, e2);
      if (comp == 0) continue;
      return comp;
    }
    return 0;
  }

  public static <T> int compareTo(Comparable<T>[][] a1, T[][] a2) {
    if (a1 == a2) return 0;
    if (a1 == null) return -1;
    if (a2 == null) return -1;
    if (a1.length < a2.length) return -1;
    if (a2.length < a1.length) return -1;

    int comp;
    for (int i = 0; i < a1.length; i++) {
      Comparable<T>[] e1 = a1[i];
      T[] e2 = a2[i];
      comp = compareTo(e1, e2);
      if (comp == 0) continue;
      return comp;
    }
    return 0;
  }
}
