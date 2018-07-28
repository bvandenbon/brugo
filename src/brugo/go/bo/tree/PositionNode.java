package brugo.go.bo.tree;

import java.util.ArrayList;
import java.util.List;

import brugo.go.bo.state.Move;
import brugo.go.bo.state.Position;

public class PositionNode {
  protected Position position;
  protected Move move;
  protected String comments;

  // next and previous node
  protected PositionNode previous;
  protected List<PositionNode> nextPositionNodeList;

  public PositionNode(PositionNode previousNode, Position position, Move move) {
    previous = previousNode;
    this.position = position;
    this.move = move;
  }

  public Position getPosition() {
    return position;
  }

  public Move getMove() {
    return move;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public String getComments() {
    return comments;
  }

  public PositionNode getPreviousPositionNode() {
    return previous;
  }

  public PositionNode addNextPosition(Position position, Move move) {
    PositionNode next = null;
    if (nextPositionNodeList == null) {
      nextPositionNodeList = new ArrayList<>(1);
    } else {
      // check: maybe the node already exists
      next = getNextPositionNode(position);
    }

    // if the node doesn't exist yet, create it
    if (next == null) {
      next = new PositionNode(this, position, move);
      nextPositionNodeList.add(next);
    }
    return next;
  }

  public boolean removeNextPositionNode(PositionNode positionNode) {
    if (!nextPositionNodeList.contains(positionNode)) return false;
    nextPositionNodeList.remove(positionNode);
    if (nextPositionNodeList.isEmpty()) nextPositionNodeList = null;
    return true;
  }

  public List<PositionNode> getNextPositionNodeList() {
    return nextPositionNodeList;
  }

  public PositionNode getNextPositionNode(Position position) {
    if (nextPositionNodeList == null) return null;

    for (PositionNode positionNode : nextPositionNodeList) {
      if (positionNode.position == null) continue;
      if (positionNode.position.equals(position)) {
        return positionNode;
      }
    }
    return null;
  }

  public boolean hasVariations() {
    if (nextPositionNodeList == null) return false;
    return (nextPositionNodeList.size() > 1);
  }

  public PositionNode getLastPositionNode() {
    PositionNode node = this;

    // recursively move through nodes.
    // protection against circular references --> max = 999 nodes.
    int i = 0;
    while (i++ < 999) {
      if (node.nextPositionNodeList == null) return node;
      node = node.nextPositionNodeList.get(0);
    }
    return null;
  }

  public int getMoveNumber() {
    PositionNode node = this;

    // safety, don't try more than 999 moves.
    int moveCount = 0;
    for (int i = 0; i < 999; i++) {
      if (node.previous == null) return moveCount;
      node = node.previous;

      Move move = node.getMove();
      if ((move != null) && (move.getColor() != null)) moveCount++;
    }
    return -1;
  }

  @Override
  public String toString() {
    if (move != null) return move.toString();
    return "...";
  }

  public PositionNode jump(int numberOfMoves) {
    PositionNode node = this;
    for (int i = 0; i < numberOfMoves; i++) {
      if (node.getNextPositionNodeList() == null) return null;
      node = node.getNextPositionNodeList().get(0);
    }
    return node;
  }


  /**
   * Clones all moves from this point on (recursively)
   */
  public PositionNode cloneTillEnd() {
    PositionNode node = new PositionNode(null, position, move);
    if (nextPositionNodeList != null)
      for (PositionNode nextNode : nextPositionNodeList) {
        PositionNode clonedNextNode = nextNode.cloneTillEnd();
        if (node.nextPositionNodeList == null) node.nextPositionNodeList = new ArrayList<>(1);
        node.nextPositionNodeList.add(clonedNextNode);
        clonedNextNode.previous = node;
      }
    return node;
  }

  /**
   * Auto generated
   */
  @Override
  public int hashCode() {
    int result = position != null ? position.hashCode() : 0;
    result = 31 * result + (move != null ? move.hashCode() : 0);
    result = 31 * result + (comments != null ? comments.hashCode() : 0);
    result = 31 * result + (previous != null ? previous.hashCode() : 0);
    result = 31 * result + (nextPositionNodeList != null ? nextPositionNodeList.hashCode() : 0);
    return result;
  }

  /**
   * Auto generated
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    PositionNode that = (PositionNode) obj;
    if (comments != null ? !comments.equals(that.comments) : that.comments != null) return false;
    if (move != null ? !move.equals(that.move) : that.move != null) return false;
    if (previous != null ? !previous.equals(that.previous) : that.previous != null) return false;
    if (nextPositionNodeList != null ? !nextPositionNodeList.equals(that.nextPositionNodeList) : that.nextPositionNodeList != null)
      return false;
    if (position != null ? !position.equals(that.position) : that.position != null) return false;
    return true;
  }
}
