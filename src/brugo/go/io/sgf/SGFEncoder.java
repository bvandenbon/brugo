package brugo.go.io.sgf;

import java.util.List;

import brugo.go.bo.state.Intersection;
import brugo.go.bo.state.Move;
import brugo.go.bo.state.Position;
import brugo.go.bo.state.Status;
import brugo.go.bo.tree.Game;
import brugo.go.bo.tree.PositionNode;
import brugo.go.io.Encoder;

public class SGFEncoder implements Encoder
{
  @Override
  public String encodeGame(Game game)
  {
    return toSGF(game);
  }

  private String toSGF(Game game) {
    return toSGF(game, true, null);
  }

  private String toSGF(Game game, boolean includeBranches, String charsetName) {
    StringBuilder builder = new StringBuilder();

    PositionNode root = game.getRootNode();
    Position parentPosition = root.getPosition();
    if (parentPosition == null) return "";
    int boardSize = parentPosition.getSize();
    double komi = parentPosition.getKomi();

    String rules = game.getRules();
    String playerBlack = game.getPlayerBlack();
    String playerWhite = game.getPlayerWhite();
    String blackRank = game.getBlackRank();
    String whiteRank = game.getWhiteRank();
    String date = game.getDate();
    String result = game.getResult();
    String event = game.getEvent();

    // create header
    builder.append("(;FF[4]GM[1]SZ[").append(boardSize).append("]");
    if (charsetName != null) builder.append("CA[").append(charsetName).append("]");
    if (rules != null) builder.append("RU[").append(rules).append("]");
    if (playerBlack != null) builder.append("PB[").append(playerBlack).append("]");
    if (playerWhite != null) builder.append("PW[").append(playerWhite).append("]");
    if (blackRank != null) builder.append("BR[").append(blackRank).append("]");
    if (whiteRank != null) builder.append("WR[").append(whiteRank).append("]");
    if (date != null) builder.append("DT[").append(date).append("]");
    if (result != null) builder.append("RE[").append(result).append("]");
    if (event != null) builder.append("EV[").append(event).append("]");
    if (komi != 0d) builder.append("KO[").append(komi).append("]");

    builder.append(encodePositionNode(root));

    builder.append(")\n");
    return builder.toString();
  }

  @Override
  public String encodePositionNode(PositionNode positionNode)
  {
    return toSGF(positionNode, true);
  }

  private String toSGF(PositionNode positionNode, boolean includeStartPosition) {
    return toSGF(positionNode, includeStartPosition, true);
  }

  private String toSGF(PositionNode positionNode, boolean includeStartPosition, boolean includeBranches) {
    StringBuilder sb = new StringBuilder();

    if (includeStartPosition) {
      appendStartPosition(positionNode, sb);
    }

    appendMove(positionNode, sb);
    appendChildNodes(positionNode, includeBranches, sb);

    return sb.toString();
  }

  private void appendChildNodes(PositionNode positionNode, boolean includeBranches, StringBuilder sb)
  {
    // Also add all child nodes inside this node.
    List<PositionNode> continuationList = positionNode.getNextPositionNodeList();
    if (continuationList != null) {
      // intend if multiple variations
      int toIndent = sb.toString().length();
      String intendPrefix = "\n" + new String(new char[toIndent]).replace("\0", " ");
      boolean hasMultipleContinuations = (continuationList.size() > 1);
      if (!hasMultipleContinuations || !includeBranches) intendPrefix = "";

      // loop through played moves
      for (PositionNode childLink : continuationList) {
        sb.append(intendPrefix);

        // only add braces if there are multiple continuations
        if (hasMultipleContinuations && includeBranches) sb.append("(");
        sb.append(toSGF(childLink, false, includeBranches).replaceAll("\n", intendPrefix));
        if (hasMultipleContinuations && includeBranches) sb.append(")");

        if (!includeBranches) break;
      }

      if (hasMultipleContinuations && includeBranches) sb.append("\n");
    }
  }

  private void appendStartPosition(PositionNode positionNode, StringBuilder sb)
  {
    Position position = positionNode.getPosition();
    if (!position.isEmpty()) {
      sb.append(";");

      List<Intersection> blackStoneList = position.getIntersectionListByColor(Status.BLACK);
      if (blackStoneList != null) {
        sb.append("AB");
        for (Intersection intersection : blackStoneList) {
          sb.append("[").append(toSGF(intersection)).append("]");
        }
      }

      List<Intersection> whiteStoneList = position.getIntersectionListByColor(Status.WHITE);
      if (whiteStoneList != null) {
        sb.append("AW");
        for (Intersection intersection : whiteStoneList) {
          sb.append("[").append(toSGF(intersection)).append("]");
        }
      }
    }
  }

  private void appendMove(PositionNode positionNode, StringBuilder sb)
  {
    // Add a ;W[ab] or ;B[ab] tag
    Move move = positionNode.getMove();
    if ((move == null) || (move.getPoint() == null)) return;

    String comments = positionNode.getComments();
    sb.append(toSGF(move));

    // add comments C[...]
    if (comments != null) {
      String escapedComments = comments;
      escapedComments = escapedComments.replaceAll("\\\\|\\[|\\]|\\(|\\)|\\:|\\;", "\\\\$0");
      sb.append("C[").append(escapedComments).append("]");

    }
  }

  /**
   * Returns a ;W[ab] or ;B[ab] tag
   */
  private String toSGF(Move move)
  {
    String sgfColor = (move.getColor() == Status.BLACK) ? "B" : "W";
    Intersection point = move.getPoint();
    return ";" + sgfColor + "[" + ((point == null) ? null : toSGF(point)) + "]";
  }


  public String toSGF(Intersection intersection) {
    return "" + ((char) ('a' + intersection.getX())) + ((char) ('a' + intersection.getY()));
  }

  public Intersection fromSGF(String coordinate) {
    if (coordinate == null) return null;
    if (coordinate.length() != 2) return null;
    char left = coordinate.charAt(0);
    char right = coordinate.charAt(1);
    return Intersection.valueOf(left - 'a', right - 'a');
  }
}
