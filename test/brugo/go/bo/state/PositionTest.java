package brugo.go.bo.state;

import org.testng.annotations.BeforeTest;

import static org.testng.Assert.*;

/**
 * Created by Oleksandr_Baglai on 2019-09-24.
 */
public class PositionTest {
  @BeforeTest
  public void setup() {
    Position pos = new Position(6, 6.5);
  }
}