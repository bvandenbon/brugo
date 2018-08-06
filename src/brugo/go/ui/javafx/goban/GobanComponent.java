package brugo.go.ui.javafx.goban;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import brugo.go.bo.state.Intersection;
import brugo.go.bo.state.Position;

/**
 * A resizable goban component for javafx.
 * @see brugo.go.ui.javafx.goban.GobanComponentDemo
 */
public class GobanComponent extends AnchorPane {
  protected GobanDrawer gobanDrawer;
  protected Position position;
  protected GobanCanvas canvas;
  protected Map<Intersection, GobanDrawer.DrawPosition> drawPositionMap;
  protected Set<Intersection> selectedIntersections;

  public GobanComponent() {
    // create drawer
    gobanDrawer = new GobanDrawer();
    gobanDrawer.setUseIntegerDrawingPrecision(true);

    // create canvas to draw on
    canvas = new GobanCanvas();
    getChildren().add(canvas);
  }

  public void setPosition(Position position) {
    if (this.position == position) return;

    this.position = position;
    canvas.redraw();
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

  private class GobanCanvas extends javafx.scene.canvas.Canvas {

    public GobanCanvas() {
      GobanComponent wrapper = GobanComponent.this;
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
}
