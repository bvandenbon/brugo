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

  @Test
  public void shouldCantMakeAMove_whenSuicidalStep() {
    // given
    givenBd("---------------\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . X . . |\n" +
            "| . . X . X . |\n" +
            "| . . . X . . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 0 stones.\n" +
            "O captured 0 stones.\n");

    // when
    Position newPosition = position.play(Intersection.valueOf(3, 3), Status.WHITE);

    // then
    assertEquals(newPosition, null);

    assertB("---------------\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . X . . |\n" +
            "| . . X . X . |\n" +
            "| . . . X . . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 0 stones.\n" +
            "O captured 0 stones.\n");
  }

  @Test
  public void shouldCantMakeAMove_whenKoSituation_caseArround() {
    // given
    givenBd("---------------\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . X O . |\n" +
            "| . . X . X O |\n" +
            "| . . . X O . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 0 stones.\n" +
            "O captured 0 stones.\n");

    // when
    position = position.play(Intersection.valueOf(3, 3), Status.WHITE);

    // then
    assertB("---------------\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . X O . |\n" +
            "| . . X O k O |\n" +
            "| . . . X O . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 1 stones.\n" +
            "O captured 0 stones.\n");

    // when (forbidden)
    Position newPosition = position.play(Intersection.valueOf(4, 3), Status.BLACK);

    // then
    assertEquals(newPosition, null);

    assertB("---------------\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . X O . |\n" +
            "| . . X O k O |\n" +
            "| . . . X O . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 1 stones.\n" +
            "O captured 0 stones.\n");
  }

  @Test
  public void shouldCantMakeAMove_whenKoSituation_caseWithBoarder1() {
    // given
    givenBd("---------------\n" +
            "| . . . . . . |\n" +
            "| . . . . . O |\n" +
            "| . . . . O X |\n" +
            "| . . . . X . |\n" +
            "| . . . . . X |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 0 stones.\n" +
            "O captured 0 stones.\n");

    // when
    position = position.play(Intersection.valueOf(5, 3), Status.WHITE);

    // then
    assertB("---------------\n" +
            "| . . . . . . |\n" +
            "| . . . . . O |\n" +
            "| . . . . O k |\n" +
            "| . . . . X O |\n" +
            "| . . . . . X |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 1 stones.\n" +
            "O captured 0 stones.\n");

    // when (forbidden)
    Position newPosition = position.play(Intersection.valueOf(5, 2), Status.BLACK);

    // then
    assertEquals(newPosition, null);

    assertB("---------------\n" +
            "| . . . . . . |\n" +
            "| . . . . . O |\n" +
            "| . . . . O k |\n" +
            "| . . . . X O |\n" +
            "| . . . . . X |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 1 stones.\n" +
            "O captured 0 stones.\n");
  }

  @Test
  public void shouldCantMakeAMove_whenKoSituation_caseWithBoarder2() {
    // given
    givenBd("---------------\n" +
            "| . . . . . . |\n" +
            "| . . . . X X |\n" +
            "| . . . O O X |\n" +
            "| . . . O . O |\n" +
            "| . . . . O X |\n" +
            "| . . . . . X |\n" +
            "---------------\n" +
            "X captured 0 stones.\n" +
            "O captured 0 stones.\n");

    // when
    position = position.play(Intersection.valueOf(4, 3), Status.BLACK);

    // then
    assertB("---------------\n" +
            "| . . . . . . |\n" +
            "| . . . . X X |\n" +
            "| . . . O O X |\n" +
            "| . . . O X k |\n" +
            "| . . . . O X |\n" +
            "| . . . . . X |\n" +
            "---------------\n" +
            "X captured 0 stones.\n" +
            "O captured 1 stones.\n");

    // when (forbidden)
    Position newPosition = position.play(Intersection.valueOf(5, 3), Status.WHITE);

    // then
    assertEquals(newPosition, null);

    assertB("---------------\n" +
            "| . . . . . . |\n" +
            "| . . . . X X |\n" +
            "| . . . O O X |\n" +
            "| . . . O X k |\n" +
            "| . . . . O X |\n" +
            "| . . . . . X |\n" +
            "---------------\n" +
            "X captured 0 stones.\n" +
            "O captured 1 stones.\n");
  }

  @Test
  public void shouldCantMakeAMove_whenKoSituation_caseWithCorner() {
    // given
    givenBd("---------------\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . . X X |\n" +
            "| . . . O X O |\n" +
            "| . . . O O . |\n" +
            "---------------\n" +
            "X captured 0 stones.\n" +
            "O captured 0 stones.\n");

    // when
    position = position.play(Intersection.valueOf(5, 5), Status.BLACK);

    // then
    assertB("---------------\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . . X X |\n" +
            "| . . . O X k |\n" +
            "| . . . O O X |\n" +
            "---------------\n" +
            "X captured 0 stones.\n" +
            "O captured 1 stones.\n");

    // when (forbidden)
    Position newPosition = position.play(Intersection.valueOf(5, 4), Status.WHITE);

    // then
    assertEquals(newPosition, null);

    assertB("---------------\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . . X X |\n" +
            "| . . . O X k |\n" +
            "| . . . O O X |\n" +
            "---------------\n" +
            "X captured 0 stones.\n" +
            "O captured 1 stones.\n");
  }

  @Test
  public void shouldCanMakeAMove_whenNotKoSituation1() {
    // given
    givenBd("---------------\n" +
            "| . X O O . O |\n" +
            "| . . X X O . |\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 0 stones.\n" +
            "O captured 0 stones.\n");

    // when
    position = position.play(Intersection.valueOf(4, 0), Status.BLACK);

    // then
    assertB("---------------\n" +
            "| . X . . X O |\n" +
            "| . . X X O . |\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 0 stones.\n" +
            "O captured 2 stones.\n");

    // when
    position = position.play(Intersection.valueOf(3, 0), Status.WHITE);

    assertB("---------------\n" +
            "| . X . O . O |\n" +
            "| . . X X O . |\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 1 stones.\n" +
            "O captured 2 stones.\n");
  }

  @Test
  public void shouldCanMakeAMove_whenNotKoSituation2() {
    // given
    givenBd("---------------\n" +
            "| . X O . X O |\n" +
            "| . . X O O . |\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 0 stones.\n" +
            "O captured 0 stones.\n");

    // when
    position = position.play(Intersection.valueOf(3, 0), Status.BLACK);

    // then
    assertB("---------------\n" +
            "| . X . X X O |\n" +
            "| . . X O O . |\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 0 stones.\n" +
            "O captured 1 stones.\n");

    // when
    position = position.play(Intersection.valueOf(2, 0), Status.WHITE);

    assertB("---------------\n" +
            "| . X O . . O |\n" +
            "| . . X O O . |\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 2 stones.\n" +
            "O captured 1 stones.\n");

    // when
    position = position.play(Intersection.valueOf(3, 0), Status.BLACK);

    assertB("---------------\n" +
            "| . X . X . O |\n" +
            "| . . X O O . |\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 2 stones.\n" +
            "O captured 2 stones.\n");
  }

  @Test
  public void shouldCantMakeAMove_whenRegionWillLostLiberty() {
    // given
    givenBd("---------------\n" +
            "| . . . . . . |\n" +
            "| . . X X . . |\n" +
            "| . X O O X . |\n" +
            "| . X O . X . |\n" +
            "| . . X X . . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 0 stones.\n" +
            "O captured 0 stones.\n");

    // when
    Position newPosition = position.play(Intersection.valueOf(3, 3), Status.WHITE);

    // then
    assertEquals(newPosition, null);

    assertB("---------------\n" +
            "| . . . . . . |\n" +
            "| . . X X . . |\n" +
            "| . X O O X . |\n" +
            "| . X O . X . |\n" +
            "| . . X X . . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 0 stones.\n" +
            "O captured 0 stones.\n");
  }

  @Test
  public void shouldAllowSuicidalStep_whenRegionWillLostLiberty() {
    // given
    givenBd("---------------\n" +
            "| X X X X X X |\n" +
            "| X X X X X X |\n" +
            "| X X X O X X |\n" +
            "| X X O . O X |\n" +
            "| X X X X X X |\n" +
            "| X X X X X X |\n" +
            "---------------\n" +
            "X captured 0 stones.\n" +
            "O captured 0 stones.\n");

    // when
    position = position.play(Intersection.valueOf(3, 3), Status.WHITE);

    // then
    assertB("---------------\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . O . . |\n" +
            "| . . O O O . |\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 32 stones.\n" +
            "O captured 0 stones.\n");
  }

  @Test
  public void shouldRemoved_whenRegionHasNoLiberty_caseAlreadySurrounded() {
    // given
    givenBd("---------------\n" +
            "| . . . . . . |\n" +
            "| . . X X . . |\n" +
            "| . X O O X . |\n" +
            "| . X O . X . |\n" +
            "| . . X X . . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 0 stones.\n" +
            "O captured 0 stones.\n");

    // when
    position = position.play(Intersection.valueOf(3, 3), Status.BLACK);

    assertB("---------------\n" +
            "| . . . . . . |\n" +
            "| . . X X . . |\n" +
            "| . X . . X . |\n" +
            "| . X . X X . |\n" +
            "| . . X X . . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 0 stones.\n" +
            "O captured 3 stones.\n");
  }

  @Test
  public void shouldLongKo() {
    // given
    givenBd("-------------------\n" +
            "| . X O . O X O . |\n" +
            "| . X O O X X O . |\n" +
            "| . X O . O X O . |\n" +
            "| . X O O X X O . |\n" +
            "| . X O X . X O . |\n" +
            "| . X O O X X O . |\n" +
            "| . X X X O O O . |\n" +
            "| . . . . . . . . |\n" +
            "-------------------\n" +
            "X captured 0 stones.\n" +
            "O captured 0 stones.\n");

    // when
    position = position.play(Intersection.valueOf(3, 0), Status.BLACK);

    String step1 =
            "---------------\n" +
            "| . X O X k X |\n" +
            "| . X O O X X |\n" +
            "| . X O . O X |\n" +
            "| . X O O X X |\n" +
            "| . X O X . X |\n" +
            "| . X O O X X |\n" +
            "---------------\n";

    assertB(step1 +
            "X captured 0 stones.\n" +
            "O captured 1 stones.\n");

    // when
    position = position.play(Intersection.valueOf(4, 4), Status.WHITE);

    String step2 =
            "---------------\n" +
            "| . X O X . X |\n" +
            "| . X O O X X |\n" +
            "| . X O . O X |\n" +
            "| . X O O X X |\n" +
            "| . X O k O X |\n" +
            "| . X O O X X |\n" +
            "---------------\n";

    assertB(step2 +
            "X captured 1 stones.\n" +
            "O captured 1 stones.\n");

    // when
    position = position.play(Intersection.valueOf(3, 2), Status.BLACK);

    String step3 =
            "---------------\n" +
            "| . X O X . X |\n" +
            "| . X O O X X |\n" +
            "| . X O X k X |\n" +
            "| . X O O X X |\n" +
            "| . X O . O X |\n" +
            "| . X O O X X |\n" +
            "---------------\n";

    assertB(step3 +
            "X captured 1 stones.\n" +
            "O captured 2 stones.\n");

    // when
    position = position.play(Intersection.valueOf(4, 0), Status.WHITE);

    String step4 =
            "---------------\n" +
            "| . X O k O X |\n" +
            "| . X O O X X |\n" +
            "| . X O X . X |\n" +
            "| . X O O X X |\n" +
            "| . X O . O X |\n" +
            "| . X O O X X |\n" +
            "---------------\n";

    assertB(step4 +
            "X captured 2 stones.\n" +
            "O captured 2 stones.\n");

    // when
    position = position.play(Intersection.valueOf(3, 4), Status.BLACK);

    String step5 =
            "---------------\n" +
            "| . X O . O X |\n" +
            "| . X O O X X |\n" +
            "| . X O X . X |\n" +
            "| . X O O X X |\n" +
            "| . X O X k X |\n" +
            "| . X O O X X |\n" +
            "---------------\n";

    assertB(step5 +
            "X captured 2 stones.\n" +
            "O captured 3 stones.\n");

    // when
    position = position.play(Intersection.valueOf(4, 2), Status.WHITE);

    String step6 =
            "---------------\n" +
            "| . X O . O X |\n" +
            "| . X O O X X |\n" +
            "| . X O k O X |\n" +
            "| . X O O X X |\n" +
            "| . X O X . X |\n" +
            "| . X O O X X |\n" +
            "---------------\n";

    assertB(step6 +
            "X captured 3 stones.\n" +
            "O captured 3 stones.\n");

    // ------------ once again --------------

    // when
    position = position.play(Intersection.valueOf(3, 0), Status.BLACK);

    assertB(step1 +
            "X captured 3 stones.\n" +
            "O captured 4 stones.\n");

    // when
    position = position.play(Intersection.valueOf(4, 4), Status.WHITE);

    assertB(step2 +
            "X captured 4 stones.\n" +
            "O captured 4 stones.\n");

    // when
    position = position.play(Intersection.valueOf(3, 2), Status.BLACK);

    assertB(step3 +
            "X captured 4 stones.\n" +
            "O captured 5 stones.\n");

    // when
    position = position.play(Intersection.valueOf(4, 0), Status.WHITE);

    assertB(step4 +
            "X captured 5 stones.\n" +
            "O captured 5 stones.\n");

    // when
    position = position.play(Intersection.valueOf(3, 4), Status.BLACK);

    assertB(step5 +
            "X captured 5 stones.\n" +
            "O captured 6 stones.\n");

    // when
    position = position.play(Intersection.valueOf(4, 2), Status.WHITE);

    assertB(step6 +
            "X captured 6 stones.\n" +
            "O captured 6 stones.\n");
  }

  @Test
  public void shouldCanMakeAMove_whenKoSituation_afterOneSep() {
    // given
    givenBd("---------------\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . X O . |\n" +
            "| . . X . X O |\n" +
            "| . . . X O . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 0 stones.\n" +
            "O captured 0 stones.\n");

    // when
    position = position.play(Intersection.valueOf(3, 3), Status.WHITE);

    // then
    assertB("---------------\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . X O . |\n" +
            "| . . X O k O |\n" +
            "| . . . X O . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 1 stones.\n" +
            "O captured 0 stones.\n");

    // then (forbidden)
    assertEquals(position.play(Intersection.valueOf(4, 3), Status.BLACK), null);

    // when
    position = position.play(Intersection.valueOf(0, 0), Status.BLACK);

    // then
    assertB("---------------\n" +
            "| X . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . X O . |\n" +
            "| . . X O . O |\n" +
            "| . . . X O . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 1 stones.\n" +
            "O captured 0 stones.\n");

    // when (allowed)
    position = position.play(Intersection.valueOf(4, 3), Status.BLACK);

    // then
    assertB("---------------\n" +
            "| X . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . X O . |\n" +
            "| . . X k X O |\n" +
            "| . . . X O . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 1 stones.\n" +
            "O captured 1 stones.\n");
  }

  @Test
  public void shouldCantMakeAMove_whenKoSituation_evenIfOpponentSkip() {
    // given
    givenBd("---------------\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . X O . |\n" +
            "| . . X . X O |\n" +
            "| . . . X O . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 0 stones.\n" +
            "O captured 0 stones.\n");

    // when
    position = position.play(Intersection.valueOf(3, 3), Status.WHITE);

    // then
    assertB("---------------\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . X O . |\n" +
            "| . . X O k O |\n" +
            "| . . . X O . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 1 stones.\n" +
            "O captured 0 stones.\n");

    // then (forbidden for everyotne)
    Position newPosition1 = position.play(Intersection.valueOf(4, 3), Status.WHITE); // TODO why this is real?
    Position newPosition2 = position.play(Intersection.valueOf(4, 3), Status.BLACK);

    // then
    assertEquals(newPosition1, null);
    assertEquals(newPosition2, null);

    assertB("---------------\n" +
            "| . . . . . . |\n" +
            "| . . . . . . |\n" +
            "| . . . X O . |\n" +
            "| . . X O k O |\n" +
            "| . . . X O . |\n" +
            "| . . . . . . |\n" +
            "---------------\n" +
            "X captured 1 stones.\n" +
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
            position.setStatus(Intersection.valueOf(x, y), Status.BLACK);
            break;
          case 'O' :
            position.setStatus(Intersection.valueOf(x, y), Status.WHITE);
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
