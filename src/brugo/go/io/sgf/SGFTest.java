package brugo.go.io.sgf;

import brugo.go.bo.tree.Game;

import java.io.File;

public class SGFTest {
  public static void main(String[] args) {
    SGFEncoder encoder = new SGFEncoder();
    SGFReader reader = new SGFReader();
    File file = new File("C:\\sgf\\gokifu.com\\f\\ixq-gokifu-20110318-Zhang_Yabo-Liu_Xing.sgf");
    SGFFileNode node = reader.readSGFNodeTree(file, null);
    Game game = reader.createGameFromNode(node);
    System.out.println(encoder.encodeGame(game));
  }
}
