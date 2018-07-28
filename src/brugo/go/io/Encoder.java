package brugo.go.io;

import brugo.go.bo.tree.Game;
import brugo.go.bo.tree.PositionNode;

public interface Encoder
{
  String encodeGame(Game game);

  String encodePositionNode(PositionNode positionNode);
}
