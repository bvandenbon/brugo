package brugo.go.bo.tree;

import brugo.go.bo.state.Intersection;
import brugo.go.bo.state.Move;

public final class Game {
  private String filename;
  private PositionNode root;

  private String rules;
  private String playerBlack;
  private String playerWhite;
  private String whiteRank;
  private String blackRank;
  private String event;
  private String date;
  private String result;

  public Game(PositionNode rootNode) {
    root = rootNode;
  }

  public PositionNode getRootNode() {
    return root;
  }

  public void setRootNode(PositionNode rootNode) {
    root = rootNode;
  }

  public void setPlayerWhite(String playerWhite) {
    this.playerWhite = playerWhite;
  }

  public void setPlayerBlack(String playerBlack) {
    this.playerBlack = playerBlack;
  }

  public void setRules(String rules) {
    this.rules = rules;
  }

  public String getRules() {
    return rules;
  }

  public String getPlayerBlack() {
    return playerBlack;
  }

  public String getPlayerWhite() {
    return playerWhite;
  }

  public String getWhiteRank() {
    return whiteRank;
  }

  public void setWhiteRank(String whiteRank) {
    this.whiteRank = whiteRank;
  }

  public String getBlackRank() {
    return blackRank;
  }

  public void setBlackRank(String blackRank) {
    this.blackRank = blackRank;
  }

  public String getEvent() {
    return event;
  }

  public void setEvent(String event) {
    this.event = event;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getMoveHash() {
    return getMoveHash(20, 6);
  }

  /**
   * calculates a kind of hashcode by taking the normalized value of {@code times} jumps of size {@code step}.
   */
  public String getMoveHash(int step, int times) {
    StringBuilder builder = new StringBuilder();

    // make jumps
    PositionNode node = getRootNode();
    for (int i = 0; i < times; i++) {
      node = (node == null) ? null : node.jump(step);
      if (node == null) break;

      Move move = node.getMove();
      if (move == null) continue;

      // normalize played intersection, to avoid rotation and mirroring.
      // number of possible positions gets roughly divided by 8.
      int size = node.getPosition().getSize();
      int x = move.getPoint().getX();
      int y = move.getPoint().getY();
      if (x > (size - 1) / 2) x = size - x;
      if (y > (size - 1) / 2) y = size - y;
      Intersection is = Intersection.valueOf((x > y) ? x : y, (y > x) ? x : y);

      // convert intersection to a string (sgf style)
      String aa = "" + ((char) ('a' + is.getX())) + ((char) ('a' + is.getY()));
      builder.append(aa);
    }

    return builder.toString();
  }

  public Game deepClone() {
    PositionNode clonedRoot = (root == null) ? null : root.cloneTillEnd();
    Game game = new Game(clonedRoot);
    game.filename = filename;
    game.rules = rules;
    game.playerBlack = playerBlack;
    game.playerWhite = playerWhite;
    game.whiteRank = whiteRank;
    game.blackRank = blackRank;
    game.event = event;
    game.date = date;
    game.result = result;
    return game;
  }

  public Game mergeMeta(Game game) {
    Game result = deepClone();

    String addedRules = game.getRules();
    String addedPlayerBlack = game.getPlayerBlack();
    String addedPlayerWhite = game.getPlayerWhite();
    String addedWhiteRank = game.getWhiteRank();
    String addedBlackRank = game.getBlackRank();
    String addedEvent = game.getEvent();
    String addedDate = game.getDate();
    String addedResult = game.getResult();

    String currentRules = result.getRules();
    String currentPlayerBlack = result.getPlayerBlack();
    String currentPlayerWhite = result.getPlayerWhite();
    String currentWhiteRank = result.getWhiteRank();
    String currentBlackRank = result.getBlackRank();
    String currentEvent = result.getEvent();
    String currentDate = result.getDate();
    String currentResult = result.getResult();

    if (currentRules != null && addedRules != null && !currentRules.contains(addedRules))
      result.setRules(currentRules + "; " + addedRules);
    if (currentPlayerBlack != null && addedPlayerBlack != null && !currentPlayerBlack.contains(addedPlayerBlack))
      result.setPlayerBlack(currentPlayerBlack + "; " + addedPlayerBlack);
    if (currentPlayerWhite != null && addedPlayerWhite != null && !currentPlayerWhite.contains(addedPlayerWhite))
      result.setPlayerWhite(currentPlayerWhite + "; " + addedPlayerWhite);
    if (currentWhiteRank != null && addedWhiteRank != null && !currentWhiteRank.contains(addedWhiteRank))
      result.setWhiteRank(currentWhiteRank + "; " + addedWhiteRank);
    if (currentBlackRank != null && addedBlackRank != null && !currentBlackRank.contains(addedBlackRank))
      result.setBlackRank(currentBlackRank + "; " + addedBlackRank);
    if (currentEvent != null && addedEvent != null && !currentEvent.contains(addedEvent))
      result.setEvent(currentEvent + "; " + addedEvent);
    if (currentDate != null && addedDate != null && !currentDate.contains(addedDate))
      result.setDate(currentDate + "; " + addedDate);
    if (currentResult != null && addedResult != null && !currentResult.contains(addedResult))
      result.setResult(currentResult + "; " + addedResult);

    if (currentRules == null) game.setRules(addedRules);
    if (currentPlayerBlack == null) game.setPlayerBlack(addedPlayerBlack);
    if (currentPlayerWhite == null) game.setPlayerWhite(addedPlayerWhite);
    if (currentWhiteRank == null) game.setWhiteRank(addedWhiteRank);
    if (currentBlackRank == null) game.setBlackRank(addedBlackRank);
    if (currentEvent == null) game.setEvent(addedEvent);
    if (currentDate == null) game.setDate(addedDate);
    if (currentResult == null) game.setResult(addedResult);

    return result;
  }
}
