package brugo.go.ui.javafx.goban;

import javafx.beans.NamedArg;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.input.InputEvent;
import brugo.go.bo.state.Intersection;


public class GobanEvent extends InputEvent {
  /**
   * Common supertype for all goban event types.
   */
  public static final EventType<GobanEvent> ANY =
      new EventType<>(javafx.scene.input.InputEvent.ANY, "GOBAN");

  public static final EventType<GobanEvent> GOBAN_HOVER =
      new EventType<>(GobanEvent.ANY, "GOBAN_HOVER");

  public static final EventType<GobanEvent> GOBAN_CLICKED =
      new EventType<>(GobanEvent.ANY, "GOBAN_CLICKED");

  private Intersection intersection;

  public GobanEvent(
      @NamedArg("eventType") EventType<? extends GobanEvent> eventType,
      @NamedArg("intersection")Intersection intersection)
  {
    this(null, null, eventType, intersection);
  }

  public GobanEvent(
      @NamedArg("source") Object source, @NamedArg("target") EventTarget target,
      @NamedArg("eventType") EventType<? extends GobanEvent> eventType,
      @NamedArg("intersection")Intersection intersection)
  {
    super(source, target, eventType);
    this.intersection = intersection;
  }

  public Intersection getIntersection() {
    return intersection;
  }
}
