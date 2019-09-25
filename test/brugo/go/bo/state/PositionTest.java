package brugo.go.bo.state;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.testng.Assert.*;

/**
 * Created by Oleksandr_Baglai on 2019-09-24.
 */
public class PositionTest {

  private Position position;

  @BeforeMethod
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
    position = position.play(Intersection.valueOf(4, 3), Status.BLACK);
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
    position = position.play(Intersection.valueOf(3, 4), Status.BLACK);

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

  @Test
  public void shouldInitField_whenUsedTestFramework() {
    // given
    givenBd("---------------\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . X . . |\n" +
            "| . . X O X . |\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 1 stones.\n" +
            "O captured 5 stones.\n");

    assertB("---------------\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . X . . |\n" +
            "| . . X O X . |\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 1 stones.\n" +
            "O captured 5 stones.\n");

    // when
    position = position.play(Intersection.valueOf(3, 4), Status.BLACK);

    // then
    assertB("---------------\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . X . . |\n" +
            "| . . X . X . |\n" +
            "| . . . X . . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 1 stones.\n" +
            "O captured 6 stones.\n");
  }

  @Test
  public void shouldRemoved_whenRegionHasNoLiberty() {
    // given
    givenBd("---------------\n" +
            "| . . . . . . |\n" +
            "| . . X X . . |\n" +
            "| . X O O X . |\n" +
            "| . X O O X . |\n" +
            "| . . X . . . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 0 stones.\n" +
            "O captured 0 stones.\n");

    // when
    position = position.play(Intersection.valueOf(3, 4), Status.BLACK);

    // then
    assertB("---------------\n" +
            "| . . . . . . |\n" +
            "| . . X X . . |\n" +
            "| . X . . X . |\n" +
            "| . X . . X . |\n" +
            "| . . X X . . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 0 stones.\n" +
            "O captured 4 stones.\n");
  }

  @Test
  public void shouldRemoved_whenRegionHasNoLiberty_withBorder() {
    // given
    givenBd("---------------\n" +
            "| . . . . . . |\n" +
            "| . . . . X X |\n" +
            "| . . . X O O |\n" +
            "| . . . X O O |\n" +
            "| . . . . X . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 0 stones.\n" +
            "O captured 0 stones.\n");

    // when
    position = position.play(Intersection.valueOf(5, 4), Status.BLACK);

    // then
    assertB("---------------\n" +
            "| . . . . . . |\n" +
            "| . . . . X X |\n" +
            "| . . . X . . |\n" +
            "| . . . X . . |\n" +
            "| . . . . X X |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 0 stones.\n" +
            "O captured 4 stones.\n");
  }

  @Test
  public void shouldNotRemoved_whenRegionHasLiberty_withBorder() {
    // given
    givenBd("---------------\n" +
            "| . . . . . . |\n" +
            "| . . . . X X |\n" +
            "| . . . X O O |\n" +
            "| . . . X O O |\n" +
            "| . . . . X . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 0 stones.\n" +
            "O captured 0 stones.\n");

    // when
    position = position.play(Intersection.valueOf(4, 5), Status.BLACK);

    // then
    assertB("---------------\n" +
            "| . . . . . . |\n" +
            "| . . . . X X |\n" +
            "| . . . X O O |\n" +
            "| . . . X O O |\n" +
            "| . . . . X . |\n" +
            "| . . . . X . |\n" +
            "---------------\n" +
            "X captured 0 stones.\n" +
            "O captured 0 stones.\n");
  }

  private void givenBd(String board) {
    List<String> lines = new LinkedList<>(Arrays.asList(board.split("\n")));
    int white = getCaptured(lines);
    int black = getCaptured(lines);

    List<String> field = clean(lines);

    position = new Position(6, 6.5);
    position.setCapturedStonesCount(Status.BLACK, black);
    position.setCapturedStonesCount(Status.WHITE, white);

    given(field);
  }

  private List<String> clean(List<String> lines) {
    lines.remove(0);
    lines.remove(lines.size() - 1);
    return lines.stream()
            .map(line -> line.replaceAll("[| \n]", ""))
            .collect(toList());
  }

  private int getCaptured(List<String> lines) {
    return Integer.valueOf(lines.remove(lines.size() - 1).split(" ")[2]);
  }

  private void given(List<String> field) {
    for (int y = 0; y < field.size(); y++) {
      String row = field.get(y);
      for (int x = 0; x < row.length(); x++) {
        switch (row.charAt(x)) {
          case 'X' :
            position = position.play(Intersection.valueOf(x, y), Status.BLACK);
            break;
          case 'O' :
            position = position.play(Intersection.valueOf(x, y), Status.WHITE);
            break;
        }

      }
    }
  }

  private void assertB(String expected) {
    assertEquals(position.toString(),
            expected);
  }
}