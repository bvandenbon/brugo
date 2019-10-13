package brugo.go.ui.javafx.goban;

import brugo.go.bo.state.Position;
import brugo.go.bo.state.Status;

/**
 * Created by Oleksandr_Baglai on 2019-09-25.
 */
public class Step {
  private Status current;
  private Position position;

  public Step(Status current, Position position) {
    this.current = current;
    this.position = position;
  }

  public Status getCurrent() {
    return current;
  }

  public Position getPosition() {
    return position;
  }
}
