package brugo.go.bo.state;

public class Move {
  Intersection point;
  Status color;

  public Move(Intersection point, Status color) {
    this.point = point;
    this.color = color;
  }

  public Intersection getPoint() {
    return point;
  }

  public Status getColor() {
    return color;
  }

  public Move invertColor() {
    return new Move(point, color.getOpponentStatus());
  }

  public Move mirror() {
    return new Move(point.mirror(), color);
  }

  public Move flip(int boardsize, boolean flipX, boolean flipY) {
    return new Move(point.flip(boardsize, flipX, flipY), color);
  }

  public int getX() {
    return point.getX();
  }

  public int getY() {
    return point.getY();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Move move = (Move) o;

    if (color != move.color) return false;
    if ((point != null) ? !point.equals(move.point) : (move.point != null)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = point != null ? point.hashCode() : 0;
    result = 31 * result + (color != null ? color.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    if (color == Status.BLACK) return "B" + point.toString();
    if (color == Status.WHITE) return "W" + point.toString();
    return "?" + point.toString();
  }
}
