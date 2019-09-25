package brugo.go.ui.javafx.goban;

import java.util.*;
import java.util.function.Consumer;

import brugo.go.bo.state.Status;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import brugo.go.bo.state.Intersection;
import brugo.go.bo.state.Position;

/**
 * A resizable goban component for javafx.
 * @see brugo.go.ui.javafx.goban.GobanComponentDemo
 */
public class GobanComponent extends AnchorPane {
  protected Status current;
  protected GobanDrawer gobanDrawer;
  protected Position position;
  protected GobanCanvas canvas;
  protected Map<Intersection, GobanDrawer.DrawPosition> drawPositionMap;
  protected Set<Intersection> selectedIntersections;
  protected Consumer<String> onChange;
  protected History history;

  public GobanComponent() {
    // create drawer
    gobanDrawer = new GobanDrawer();
    gobanDrawer.setUseIntegerDrawingPrecision(true);

    // create canvas to draw on
    canvas = new GobanCanvas();
    getChildren().add(canvas);

    history = new History();
  }

  public void setPosition(Position position, Status status) {
    if (this.position == position || current == status) return;

    this.position = position;
    this.current = status;

    canvas.redraw();
    updateInfo();

    if (history.isEmpty()) {
      history.saveStep(getStep());
    }
  }

  public void setSelectedIntersection(Intersection intersection) {
    Set<Intersection> set = null;
    if (intersection != null) {
      set = new HashSet<>();
      set.add(intersection);
    }
    setSelectedIntersections(set);
  }

  public void setSelectedIntersections(Set<Intersection> intersections) {
    if ((intersections == null) && (selectedIntersections == null)) return;
    if ((intersections != null) && intersections.equals(selectedIntersections)) return;

    selectedIntersections = intersections;
    canvas.redraw();
  }

  public Position getPosition() {
    return position;
  }

  public void onChange(Consumer<String> onChange) {
    this.onChange = onChange;
  }

  private class GobanCanvas extends javafx.scene.canvas.Canvas {

    public GobanCanvas() {
      GobanComponent wrapper = GobanComponent.this;
      setFocusTraversable(true);
      widthProperty().bind(wrapper.widthProperty());
      heightProperty().bind(wrapper.heightProperty());

      addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
        GobanDrawer.DrawPosition drawPosition = GobanDrawer.findDrawPosition(event, drawPositionMap);
        if (drawPosition == null) return;
        Intersection intersection = drawPosition.getIntersection();
        if (intersection != null) fireEvent(new GobanEvent(GobanEvent.GOBAN_HOVER, intersection));
      });

      addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
        GobanDrawer.DrawPosition drawPosition = GobanDrawer.findDrawPosition(event, drawPositionMap);
        if (drawPosition == null) return;
        Intersection intersection = drawPosition.getIntersection();
        if (intersection != null) fireEvent(new GobanEvent(GobanEvent.GOBAN_CLICKED, intersection));
      });

      addEventHandler(KeyEvent.KEY_PRESSED, event -> {
        if (KeyCode.LEFT.equals(event.getCode())) {
          history.goBack(step -> setPosition(step.getPosition(), step.getCurrent()));
        }

        if (KeyCode.RIGHT.equals(event.getCode())) {
          history.goForward(step -> setPosition(step.getPosition(), step.getCurrent()));
        }

        if (KeyCode.ESCAPE.equals(event.getCode())) {
          setPosition(position, current.getOpponentStatus());
          history.saveStep(getStep());
        }
      });

      addEventHandler(GobanEvent.GOBAN_CLICKED, event -> {
        Position newPostion = position.play(event.getIntersection(), current);
        if (newPostion != null) {
          setPosition(newPostion, current.getOpponentStatus());
          history.saveStep(getStep());
        }
      });
    }

    @Override
    public void resize(double v, double v2) {
      super.resize(v, v2);
      redraw();
    }

    public void redraw() {
      double fullHeightPx = getHeight();
      double fullWidthPx = getWidth();
      GraphicsContext gctx2D = getGraphicsContext2D();
      gctx2D.clearRect(0, 0, fullWidthPx, fullHeightPx);

      drawPositionMap = gobanDrawer.drawPosition(gctx2D, position, fullWidthPx, fullHeightPx, selectedIntersections);
    }
  }

  private Step getStep() {
    return new Step(current, position);
  }

  public void updateInfo() {
    onChange.accept(String.format("Current: %s | Captured: X:%s O:%s | Active: X:%s O:%s",
            current,
            position.getCapturedStonesCount(Status.BLACK),
            position.getCapturedStonesCount(Status.WHITE),
            position.getIntersectionCountByColor(Status.BLACK),
            position.getIntersectionCountByColor(Status.WHITE)));
  }


}
