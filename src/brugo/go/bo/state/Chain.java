package brugo.go.bo.state;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * The Chain is an area of the board or a collections of intersections.
 * One of the reasons for using this class (as apposed to a simple collection), is that it is Comparable.
 * Internally the intersections are sorted, which makes it more efficient to compare them.
 */
public class Chain {
  private SortedSet<Intersection> intersections;

  public Chain(SortedSet<Intersection> intersections) {
    this.intersections = intersections;
  }

  public SortedSet<Intersection> getIntersectionSet() {
    return intersections;
  }

  public int count() {
    return intersections.size();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Chain)) return false;
    Chain area = (Chain) obj;
    if (area.count() != count()) return false;

    Iterator iterator = intersections.iterator();
    Iterator iterator2 = ((Chain) obj).intersections.iterator();
    for (; iterator.hasNext(); ) {
      if (!((iterator2.next()).equals(iterator.next()))) return false;
    }
    return true;
  }

  public Chain getNeighbourSet() {
    SortedSet<Intersection> fullSet = new TreeSet<>();
    if (intersections == null) return null;

    Chain neighbourSet;
    for (Intersection is : intersections) {
      neighbourSet = is.getNeighbourSet();
      for (Intersection neighbour : neighbourSet.intersections) {
        // don't include the intersections of this set
        if (!intersections.contains(neighbour)) fullSet.add(neighbour);
      }
    }
    return new Chain(fullSet);
  }

  public Chain getNeighbourSetExcluding(Chain excludeChain) {
    SortedSet<Intersection> fullSet = new TreeSet<>();
    if (intersections == null) return null;

    Chain neighbourSet;
    for (Intersection is : intersections) {
      neighbourSet = is.getNeighbourSet();
      for (Intersection neighbour : neighbourSet.intersections) {
        // don't include the intersections of this set
        if (intersections.contains(neighbour)) continue;
        if ((excludeChain != null) &&
            (excludeChain.intersections != null) &&
            excludeChain.intersections.contains(neighbour)) continue;

        fullSet.add(neighbour);
      }
    }
    return new Chain(fullSet);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (Intersection is : intersections) {
      builder.append(is.toString());
    }
    return builder.toString();
  }
}
