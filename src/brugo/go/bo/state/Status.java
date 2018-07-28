package brugo.go.bo.state;

public enum Status {
  BLACK(1), // black stone
  WHITE(2), // white stone
  EMPTY(0), // empty space, no stone
  EMPKO(-1); // ko, forbidden spot

  private int value;

  Status(int pValue) {
    value = pValue;
  }

  public boolean isOccupied() {
    return value > 0;
  }

  public boolean isEmpty() {
    return value <= 0;
  }

  public Status getOpponentStatus() {
    if (value == BLACK.value) return WHITE;
    if (value == WHITE.value) return BLACK;
    return this;
  }

  public static Status fromChar(char character) {
    if (character == 'X') return BLACK;
    if (character == 'O') return WHITE;
    if (character == '.') return EMPTY;
    if (character == 'k') return EMPKO;
    return null;
  }

  public char toChar() {
    if (this == BLACK) return 'X';
    if (this == WHITE) return 'O';
    if (this == EMPTY) return '.';
    if (this == EMPKO) return 'k';
    return '?';
  }

  @Override
  public String toString() {
    return String.valueOf(toChar());
  }
}
