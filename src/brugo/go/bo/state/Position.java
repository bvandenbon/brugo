package brugo.go.bo.state;

import java.util.*;

import brugo.common.util.CompareChain;
import brugo.common.util.Pair;

@SuppressWarnings("unused")
public final class Position implements Comparable {
  private static int hash = 0;

  private double komi;
  private Status[][] matrix;
  private int size;
  private Map<Status, Integer> capturedStones = new LinkedHashMap<>();

  public Position(int size, double komi) {
    this.size = size;
    this.komi = komi;
    matrix = new Status[this.size][this.size];
    clear();
  }

  public Position(char[][] matrix, double komi) {
    size = matrix.length;
    this.matrix = new Status[size][size];
    this.komi = komi;
    char token;
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        token = matrix[i][j];
        this.matrix[i][j] = (token == 'X') ? Status.BLACK :
                            ((token == 'O') ? Status.WHITE :
                ((token == 'k') ? Status.EMPKO : Status.EMPTY));
      }
    }
  }

  public int getSize() {
    return size;
  }

  public double getKomi() {
    return komi;
  }

  public void setKomi(double komi) {
    this.komi = komi;
  }

  public void clear() {
    // clear
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        matrix[i][j] = Status.EMPTY;
      }
    }
    hash = 0;
  }

  public boolean isEmpty() {
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        if (matrix[i][j] != Status.EMPTY) return false;
      }
    }
    return true;
  }

  public int getCapturedStonesCount(Status color) {
    if (capturedStones.isEmpty()) return 0;
    Integer captures = capturedStones.get(color);
    return (captures == null) ? 0 : captures;
  }

  /**
   * color should be Status.BLACK or Status.WHITE
   */
  public void setCapturedStonesCount(Status color, int count) {
    if (!color.isOccupied()) return;
    capturedStones.put(color, count);
  }

  public List<Intersection> getOccupiedIntersectionList() {
    List<Intersection> intersectionList = new ArrayList<>(size * size);
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        if (matrix[i][j].isOccupied()) intersectionList.add(Intersection.valueOf(i, j));
      }
    }
    return (intersectionList.isEmpty()) ? null : intersectionList;
  }

  public List<Intersection> getIntersectionListByColor(Status color) {
    List<Intersection> intersectionList = new ArrayList<>(size * size);
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        if (color == matrix[i][j]) intersectionList.add(Intersection.valueOf(i, j));
      }
    }
    return (intersectionList.isEmpty()) ? null : intersectionList;
  }

  public int getIntersectionCountByColor(Status color) {
    if (color == null) return 0;

    int count = 0;
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        if (color.equals(matrix[i][j])) count++;
      }
    }
    return count;
  }

  public int getStoneCount() {
    int count = 0;
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        if (matrix[i][j].isOccupied()) count++;
      }
    }
    return count;
  }

  public List<Pair<Chain, Chain>> getChainInfoList() {
    List<Pair<Chain, Chain>> result = new ArrayList<>();

    Pair<Chain, Chain> chainInfo;
    Set<Intersection> processedStoneSet = new HashSet<>();
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        if (matrix[i][j].isEmpty()) continue;
        if (processedStoneSet.contains(Intersection.valueOf(i, j))) continue;

        chainInfo = getChainAndLiberties(Intersection.valueOf(i, j));
        result.add(chainInfo);
        processedStoneSet.addAll(chainInfo.getKey().getIntersectionSet());
      }
    }
    return result;
  }

  public Position addStoneList(Collection<Intersection> intersectionList, Status status) {
    // there shouldn't be null elements in this collection
    assert intersectionList == null || intersectionList.stream().noneMatch(Objects::isNull);

    Chain tmpChain = (intersectionList == null) ? null : new Chain(new TreeSet<>(intersectionList));
    return addStoneList(tmpChain, status);
  }

  public Position addStoneList(Chain newChain, Status color) {
    // copy position
    Position next = this.clone();

    // play move, (move null represents a pass)
    if (newChain != null) {
      for (Intersection move : newChain.getIntersectionSet()) next.setStatus(move, color);

      if (color.isOccupied())
      {
        Chain neighbourSet = newChain.getNeighbourSet();
        Set<Intersection> removeList = getDeadIntersections(neighbourSet, next, color);
        removeDeadStones(next, removeList);
      }
    }

    // clear all ko points to empty points
    // this even happens when newChain is null.
    List<Intersection> koList = next.getIntersectionListByColor(Status.EMPKO);
    if (koList != null) {
      for (Intersection ko : koList) next.setStatus(ko, Status.EMPTY);
    }

    return next;
  }

  /**
   * Creates the next position by adding a move and returns it.
   * Returns null for illegal moves or when no color is specified.
   * Returns a copy of the previous position if move is null
   */
  public Position play(Intersection move, Status color) {
    // move NULL is a pass.
    // if move is occupied or a ko, then return null --> illegal move.
    if (move != null) {
      if (color == null) return null;
      if (getStatus(move) != Status.EMPTY) return null;
    }

    // copy position
    Position next = this.clone();

    // play move, (move null represents a pass)
    if (move == null) return next;
    next.setStatus(move, color);
    Status opStatus = color.getOpponentStatus();

    // check removal
    Chain neighbourSet = move.getNeighbourSet();
    Set<Intersection> removeList = getDeadIntersections(neighbourSet, next, color);
    removeDeadStones(next, removeList);

    if (removeList == null) {
      // if nothing removed, make sure it's a legal move.
      Pair<Chain, Chain> chaindAndLiberties = next.getChainAndLiberties(move);
      if ((chaindAndLiberties == null) || chaindAndLiberties.getValue() == null) return null;
    }

    // clear all ko points to empty points
    List<Intersection> koList = next.getIntersectionListByColor(Status.EMPKO);
    if (koList != null) {
      for (Intersection ko : koList) next.setStatus(ko, Status.EMPTY);
    }

    // add a new ko point if necessary
    // in case exactly 1 stone was captured + it was surrounded by 4 stones + there is the possibility to capture back.
    if ((removeList != null) && (removeList.size() == 1)) {
      Intersection[] removedStones = removeList.toArray(new Intersection[1]);
      Intersection removedStone = removedStones[0];
      Chain neighbourChain = removedStone.getNeighbourSet();
      Set<Intersection> intersectionSet = neighbourChain.getIntersectionSet();
      int counter = 0;
      for (Intersection is : intersectionSet) {
        if (Status.EMPTY == next.getStatus(is)) counter++;
      }
      if (counter == 0) {
        neighbourChain = move.getNeighbourSet();
        intersectionSet = neighbourChain.getIntersectionSet();
        counter = 0;
        Status status;
        for (Intersection is : intersectionSet) {
          status = next.getStatus(is);
          if ((status != null) && (opStatus != status)) counter++;
        }
        if (counter == 1) next.setStatus(removedStone, Status.EMPKO);
      }
    }

    return next;
  }

  private void removeDeadStones(Position nextPosition, Set<Intersection> intersectionsToRemove) {
    if (intersectionsToRemove == null) return;

    // loop through the list of removed intersections
    Status removedStatus;
    for (Intersection is : intersectionsToRemove) {
      removedStatus = nextPosition.getStatus(is);

      // update status in stone matrix
      nextPosition.setStatus(is, Status.EMPTY);

      // increase count of captured stones
      if (removedStatus.isEmpty()) continue;
      Integer capturedSum = capturedStones.get(removedStatus);
      capturedSum = ((capturedSum == null) ? 0 : capturedSum) + intersectionsToRemove.size();
      nextPosition.capturedStones.put(removedStatus, capturedSum);
    }
  }

  /**
   * Each intersection will be be checked for liberties.
   * If they have no more liberties they will be returned in the list.
   * This list will be used later on to remove the stones.
   * <p/>
   * Some statusses are "immortal" due to the fact that they were just added by the player.
   */
  private static Set<Intersection> getDeadIntersections(Chain chainToValidate,
                                                        Position position, Status immortalStatus) {
    if (chainToValidate == null) return null;
    Status neighbourStatus;
    Pair<Chain, Chain> chaindAndLiberties;
    Set<Intersection> removeList = new HashSet<>();
    for (Intersection neighbour : chainToValidate.getIntersectionSet()) {
      neighbourStatus = position.getStatus(neighbour);
      if (neighbourStatus == null) continue;
      if (neighbourStatus.isEmpty()) continue;
      if (removeList.contains(neighbour)) continue;
      if (immortalStatus == neighbourStatus) continue;

      // check the liberties of the opponent's groups.
      // if they have no more liberties, then they will be marked for removal by putting them in the removeList.
      chaindAndLiberties = position.getChainAndLiberties(neighbour);
      if (chaindAndLiberties.getValue() == null) {
        removeList.addAll(chaindAndLiberties.getKey().getIntersectionSet());
      }
    }
    return (removeList.isEmpty()) ? null : removeList;
  }

  public Status getStatus(Intersection intersection) {
    return getStatus(intersection.getX(), intersection.getY());
  }

  public Status getStatus(int x, int y) {
    if ((x < 0) || (x >= size) || (y < 0) || (y >= size)) return null;
    return matrix[x][y];
  }

  public boolean setStatus(Intersection is, Status status) {
    return setStatus(is.getX(), is.getY(), status);
  }

  public boolean setStatus(int x, int y, Status status) {
    if ((x < 0) || (x >= size) || (y < 0) || (y >= size)) return false;
    matrix[x][y] = status;

    // reset hashcode
    hash = 0;

    return true;
  }

  public Pair<Chain, Chain> getChainAndLiberties(Intersection is) {
    if (is == null) return null;
    Status startStatus = getStatus(is);
    if ((startStatus == null) || !startStatus.isOccupied()) return null;

    // add first point to set
    List<Intersection> candidates = new ArrayList<>(size * size / 2);
    SortedSet<Intersection> liberties = new TreeSet<>();
    SortedSet<Intersection> matches = new TreeSet<>();
    matches.add(is);
    candidates.add(is);

    // this loop is a clever way to avoid recursive calls.
    // go through all neighbours, and add their neighbours to a candidate list.
    // process all neighbours till there are no more candidates.
    Chain neighbourSet;
    Status neighbourStatus;
    for (int i = 0; i < candidates.size(); i++) {
      neighbourSet = candidates.get(i).getNeighbourSet();
      for (Intersection neighbourIs : neighbourSet.getIntersectionSet()) {
        neighbourStatus = getStatus(neighbourIs);
        if (neighbourStatus == null) continue;
        if (neighbourStatus.isEmpty()) {
          liberties.add(neighbourIs);
          continue;
        }
        if (startStatus == neighbourStatus) {
          boolean isNewMatch = matches.add(neighbourIs);
          if (isNewMatch) candidates.add(neighbourIs);
        }
      }
    }
    return new Pair<>(new Chain(matches),
        ((liberties.isEmpty()) ? null : new Chain(liberties)));
  }

  public Pair<Chain, Chain> getGroupAndLiberties(Intersection is) {
    Status startStatus = getStatus(is);
    if (!startStatus.isOccupied()) return null;

    SortedSet<Intersection> stoneSet = new TreeSet<>();
    SortedSet<Intersection> libertySet = new TreeSet<>();

    Set<Intersection> queuedIntersections = new HashSet<>(size * size / 2);
    List<Pair<Chain, Chain>> queuedChainsAndLibs = new ArrayList<>(size * 2);

    Pair<Chain, Chain> chainAndLibs = getChainAndLiberties(is);
    Chain chain = chainAndLibs.getKey();
    queuedIntersections.addAll(chain.getIntersectionSet());
    queuedChainsAndLibs.add(chainAndLibs);

    Pair<Chain, Chain> pair;
    for (int i = 0; i < queuedChainsAndLibs.size(); i++) {
      pair = queuedChainsAndLibs.get(i);
      Chain groupStones = pair.getKey();
      Chain liberties = pair.getValue();
      stoneSet.addAll(groupStones.getIntersectionSet());
      libertySet.addAll(liberties.getIntersectionSet());

      Chain oneSpaceDistance = liberties.getNeighbourSetExcluding(groupStones);
      for (Intersection oneSpaceIntersection : oneSpaceDistance.getIntersectionSet()) {
        if (startStatus != getStatus(oneSpaceIntersection)) continue;

        // already in queue or already processed? then skip it.
        if (queuedIntersections.contains(oneSpaceIntersection)) continue;

        // add chain to the queue
        chainAndLibs = getChainAndLiberties(oneSpaceIntersection);
        chain = chainAndLibs.getKey();
        queuedIntersections.addAll(chain.getIntersectionSet());
        queuedChainsAndLibs.add(chainAndLibs);
      }
    }

    return new Pair<>(((stoneSet.isEmpty()) ? null : new Chain(stoneSet)),
        ((libertySet.isEmpty()) ? null : new Chain(libertySet)));
  }

  public Position rotateClockwise() {
    Position rotatedCopy = new Position(size, getKomi());
    rotatedCopy.setCapturedStonesCount(Status.BLACK, getCapturedStonesCount(Status.BLACK));
    rotatedCopy.setCapturedStonesCount(Status.WHITE, getCapturedStonesCount(Status.WHITE));

    for (int i = 0; i < size; i++)
      for (int j = 0; j < size; j++) {
        Status status = getStatus(i, j);
        Intersection rotated = Intersection.rotateClockwise(Intersection.valueOf(i, j), size);
        rotatedCopy.setStatus(rotated, status);
      }
    return rotatedCopy;
  }

  public Position inverseStatus() {
    // funny thing: to be mathematically correct, inverse komi as well.
    Position inversedCopy = new Position(size, -komi);

    // inverse captured stones count
    inversedCopy.capturedStones.put(Status.BLACK, getCapturedStonesCount(Status.WHITE));
    inversedCopy.capturedStones.put(Status.WHITE, getCapturedStonesCount(Status.BLACK));

    // inverse the stone colors on the board.
    for (int i = 0; i < size; i++)
      for (int j = 0; j < size; j++) {
        Status status = getStatus(i, j);
        inversedCopy.setStatus(i, j, status.getOpponentStatus());
      }
    return inversedCopy;
  }

  public Position mirror() {
    Position mirrorPosition = new Position(size, komi);
    mirrorPosition.capturedStones.put(Status.BLACK, getCapturedStonesCount(Status.BLACK));
    mirrorPosition.capturedStones.put(Status.WHITE, getCapturedStonesCount(Status.WHITE));

    // inverse the coordinates
    for (int i = 0; i < size; i++)
      for (int j = 0; j < size; j++) {
        mirrorPosition.setStatus(
            Intersection.valueOf(i, j).mirror(),
            getStatus(i, j));
      }
    return mirrorPosition;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Position)) return false;
    Position pos2 = (Position) obj;
    if (pos2.size != size) return false;
    if (!pos2.capturedStones.equals(capturedStones)) return false;
    if (pos2.komi != komi) return false;
    return Arrays.deepEquals(pos2.matrix, matrix);
  }

  @Override
  public int compareTo(Object o) {
    if (!(o instanceof Position)) return 1;
    Position p = (Position) o;

    if (p.size != size) return (size < p.size) ? -1 : 1;
    if (p.komi != komi) return (komi < p.komi) ? -1 : 1;

    int blackCount = getIntersectionCountByColor(Status.BLACK);
    int whiteCount = getIntersectionCountByColor(Status.WHITE);
    int blackCaptured = getCapturedStonesCount(Status.BLACK);
    int whiteCaptured = getCapturedStonesCount(Status.WHITE);
    int movesPlayed = blackCount + whiteCount + blackCaptured + whiteCaptured;

    int p_blackCount = p.getIntersectionCountByColor(Status.BLACK);
    int p_whiteCount = p.getIntersectionCountByColor(Status.WHITE);
    int p_blackCaptured = p.getCapturedStonesCount(Status.BLACK);
    int p_whiteCaptured = p.getCapturedStonesCount(Status.WHITE);
    int p_movesPlayed = p_blackCount + p_whiteCount + p_blackCaptured + p_whiteCaptured;

    return new CompareChain().
        compareTo(movesPlayed, p_movesPlayed).
        compareTo(blackCount, p_blackCount).
        compareTo(whiteCount, p_whiteCount).
        compareTo(blackCaptured, p_blackCaptured).
        compareTo(whiteCaptured, p_whiteCaptured).
        compareTo(matrix, p.matrix).
        getResult();
  }


  @Override
  public int hashCode() {
    if (hash == 0) calculateHash();
    return hash;
  }

  private synchronized void calculateHash() {
    // only uses the matrix in calculation of hashcode
    // the advantage is that the hashcode only needs to be cleared if the matrix changes
    int h = 0;
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        h = 7 * h + matrix[i][j].ordinal();
      }
    }
    hash = h;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();

    if (matrix != null) {
      Status status;
      for (int i = 0; i < size; i++) {
        builder.append("--");
      }
      builder.append("---\n");
      for (int i = 0; i < size; i++) {
        builder.append("|");
        for (int j = 0; j < size; j++) {
          status = matrix[j][i];
          builder.append(" ");
          builder.append(status.toChar());
        }
        builder.append(" |\n");
      }
      for (int i = 0; i < size; i++) {
        builder.append("--");
      }
      builder.append("---\n");
    }

    if (capturedStones != null) {
      Integer captures;
      for (Status color : capturedStones.keySet()) {
        captures = capturedStones.get(color);
        if (captures == null) continue;
        String msg = String.format("%s captured %d stones.\n", color.toString(), captures);
        builder.append(msg);
      }
    }

    return builder.toString();
  }

  @Override
  public Position clone() {
    // copy general properties
    Position next = new Position(size, komi);

    // copy matrix
    next.matrix = new Status[size][];
    for (int i = 0; i < size; i++) {
      next.matrix[i] = new Status[size];
      System.arraycopy(matrix[i], 0, next.matrix[i], 0, size);
    }

    // set captured stones count
    next.capturedStones.put(Status.BLACK, getCapturedStonesCount(Status.BLACK));
    next.capturedStones.put(Status.WHITE, getCapturedStonesCount(Status.WHITE));

    return next;
  }

  public Position cloneCorner(int nrOfIntersections) {
    // copy general properties
    Position next = new Position(size, komi);

    // copy matrix
    next.matrix = new Status[size][];
    for (int i = 0; i < nrOfIntersections; i++) {
      next.matrix[i] = new Status[size];
      System.arraycopy(matrix[i], 0, next.matrix[i], 0, nrOfIntersections);
      Arrays.fill(next.matrix[i], nrOfIntersections, size, Status.EMPTY);
    }
    for (int i = nrOfIntersections; i < size; i++) {
      next.matrix[i] = new Status[size];
      Arrays.fill(next.matrix[i], 0, size, Status.EMPTY);
    }

    // set captured stones count
    next.capturedStones.put(Status.BLACK, getCapturedStonesCount(Status.BLACK));
    next.capturedStones.put(Status.WHITE, getCapturedStonesCount(Status.WHITE));

    return next;
  }

  public Intersection getKoIntersection() {
    // todo maybe make a dedicated field for performance reasons.
    List<Intersection> intersectionList = getIntersectionListByColor(Status.EMPKO);
    if (intersectionList == null) return null;
    return intersectionList.get(0);
  }
}
