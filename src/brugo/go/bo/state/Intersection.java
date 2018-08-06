package brugo.go.bo.state;

import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import brugo.go.io.jaxb.IntersectionSGFJavaTypeAdapter;

/**
 * An intersection represents a spot on the board where 2 lines cross.
 * A 19x19 board has 361 intersections, and there is a cache that reuses 361 instances for these positions.
 * Also intersections that lay just one intersection out of range of the board are cached.
 */
@XmlRootElement(name = "intersection")
@XmlAccessorType(XmlAccessType.NONE)
@XmlJavaTypeAdapter(IntersectionSGFJavaTypeAdapter.class)
public final class Intersection implements Comparable<Intersection> {
  private static int cacheSize = 19;
  private static Intersection[][] instances = new Intersection[cacheSize + 2][cacheSize + 2];

  @XmlElement(name = "x")
  private int x;
  @XmlElement(name = "y")
  private int y;

  private Intersection() {
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  private Intersection(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public static Intersection valueOf(int x, int y) {
    if ((x < -1) || (x > cacheSize) || (y < -1) || (y > cacheSize)) {
      System.err.println("requesting point [" + x + "][" + y + "], most likely a bug or error.");
      return new Intersection(x, y);
    }
    Intersection uniqueInstance = instances[x + 1][y + 1];
    if (uniqueInstance == null) {
      uniqueInstance = new Intersection(x, y);
      instances[x + 1][y + 1] = uniqueInstance;
    }
    return uniqueInstance;
  }

  @SuppressWarnings("SuspiciousNameCombination")
  public Intersection mirror() {
    return Intersection.valueOf(y, x);
  }

  public Intersection flip(int boardSize, boolean flipX, boolean flipY) {
    if (!flipX && !flipY) return this;

    return Intersection.valueOf((flipX) ? (boardSize - 1) - x : x,
        (flipY) ? (boardSize - 1) - y : y);
  }

  public static Collection<Intersection> mirror(Collection<Intersection> intersectionList) {
    if (intersectionList == null) return null;
    Set<Intersection> mirroredSet = new TreeSet<>();
    for (Intersection intersection : intersectionList) {
      mirroredSet.add(intersection.mirror());
    }
    return mirroredSet;
  }

  public static Collection<Intersection> flip(Collection<Intersection> intersectionList, int boardSize, boolean flipX, boolean flipY) {
    if (!flipX && !flipY) return intersectionList;

    if (intersectionList == null) return null;
    Set<Intersection> flippedSet = new TreeSet<>();
    for (Intersection intersection : intersectionList) {
      flippedSet.add(intersection.flip(boardSize, flipX, flipY));
    }
    return flippedSet;
  }

  public Chain getNeighbourSet() {
    SortedSet<Intersection> set = new TreeSet<>();
    set.add(Intersection.valueOf(x + 1, y));
    set.add(Intersection.valueOf(x, y + 1));
    set.add(Intersection.valueOf(x - 1, y));
    set.add(Intersection.valueOf(x, y - 1));
    return new Chain(set);
  }

  @SuppressWarnings("SuspiciousNameCombination")
  public static Intersection rotateClockwise(Intersection intersection, int boardSize) {
    return Intersection.valueOf(
        (intersection.y * -1) + (boardSize - 1),
        intersection.x);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Intersection)) return false;
    if (obj == this) return true;
    Intersection is = (Intersection) obj;
    return (is.x == x) && (is.y == y);
  }

  @Override
  public String toString() {
    return "[" + (x + 1) + "," + (y + 1) + "]";
  }

  public int compareTo(Intersection intersection) {
    if (intersection == null) return -1;
    if (x != intersection.x) return (x < intersection.x) ? -1 : 1;
    if (y == intersection.y) return 0;
    return (y < intersection.y) ? -1 : 1;
  }
}
