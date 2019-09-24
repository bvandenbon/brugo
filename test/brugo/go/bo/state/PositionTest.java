package brugo.go.bo.state;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Created by Oleksandr_Baglai on 2019-09-24.
 */
public class PositionTest {

  private Position position;

  @BeforeTest
  public void setup() {
    // given
    position = new Position(6, 6.5);
  }

  @Test
  public void shouldPrint_whenEmptyBoard() {
    // then
    assertB("---------------\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "---------------\n");
  }

  @Test
  public void shouldPrint_whenSomeSteps() {
    // when
    position = position.play(Intersection.valueOf(4, 4), Status.BLACK);
    position = position.play(Intersection.valueOf(3, 3), Status.WHITE);
    position = position.play(Intersection.valueOf(2, 2), Status.BLACK);

    // then
    assertB("---------------\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . X . . . |\n" +
            "| . . . O . . |\n" +
            "| . . . . X . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 0 stones.\n" +
            "O captured 0 stones.\n");
  }

  @Test
  public void shouldRemoved_whenNoLiberty () {
    // given
    position = position.play(Intersection.valueOf(3, 2), Status.BLACK);
    position = position.play(Intersection.valueOf(3, 4), Status.BLACK);
    position = position.play(Intersection.valueOf(2, 3), Status.BLACK);
    position = position.play(Intersection.valueOf(3, 3), Status.WHITE);

    assertB("---------------\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . X . . |\n" +
            "| . . X O X . |\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 0 stones.\n" +
            "O captured 0 stones.\n");

    // when
    position = position.play(Intersection.valueOf(4, 3), Status.BLACK);

    // then
    assertB("---------------\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . X . . |\n" +
            "| . . X . X . |\n" +
            "| . . . X . . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 0 stones.\n" +
            "O captured 1 stones.\n");
  }

  private void assertB(String expected) {
    assertEquals(position.toString(),
            expected);
  }
}