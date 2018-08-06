package brugo.go.ui.javafx.goban;

import java.util.*;

import brugo.go.bo.state.Intersection;
import brugo.go.bo.state.Position;
import brugo.go.bo.state.Status;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * Draws a goban on top of a GraphicsContext
 */
public class GobanDrawer {
  protected static final Paint COLOR_BOARD = Paint.valueOf("#efbd20");
  protected static final Paint COLOR_GRID_LINES = Paint.valueOf("#464637");
  protected static final Paint COLOR_STONE_CIRCLE = Paint.valueOf("black");
  protected static final Paint COLOR_MARKER = Paint.valueOf("crimson");

  protected boolean useIntegerDrawingPrecision = true;

  public void setUseIntegerDrawingPrecision(boolean useIntegerDrawingPrecision) {
    this.useIntegerDrawingPrecision = useIntegerDrawingPrecision;
  }

  protected List<Intersection> calculateDotList(int boardSize) {
    List<Intersection> dotList = new ArrayList<>(9);

    // always put a dot in the center, for odd sizes.
    if ((boardSize % 2) == 1) {
      dotList.add(Intersection.valueOf(boardSize / 2, boardSize / 2));
    }

    // no additional dots necessary for really small boards.
    if (boardSize < 8) return dotList;

    // for boards smaller than 15 use dots on the 3-3 points.
    int low = (boardSize < 13) ? 2 : 3;
    int high = boardSize - 1 - low;
    dotList.add(Intersection.valueOf(low, low));
    dotList.add(Intersection.valueOf(low, high));
    dotList.add(Intersection.valueOf(high, high));
    dotList.add(Intersection.valueOf(high, low));

    // for big boards also put a dot in the middle of the sides
    if (((boardSize % 2) == 1) && (boardSize >= 17)) {
      int middle = boardSize / 2;
      dotList.add(Intersection.valueOf(low, middle));
      dotList.add(Intersection.valueOf(high, middle));
      dotList.add(Intersection.valueOf(middle, low));
      dotList.add(Intersection.valueOf(middle, high));
    }

    return dotList;
  }

  public Map<Intersection, DrawPosition> drawPosition(GraphicsContext gctx2D, Position position, double fullWidthPx, double fullHeightPx,
                                                      Set<Intersection> highlightedIntersections) {
    // draw board background
    gctx2D.setFill(COLOR_BOARD);
    gctx2D.fillRect(0, 0, fullWidthPx, fullHeightPx);

    if (position == null) return null;
    int boardSize = position.getSize();
    if (boardSize <= 0) return null;

    // which one is smallest, width or height ?
    double intersxHeightPx = fullHeightPx / boardSize;
    double intersxWidthPx = fullWidthPx / boardSize;
    double smallestDimensionSize = Math.min(intersxHeightPx, intersxWidthPx);
    if (useIntegerDrawingPrecision) smallestDimensionSize = Math.floor(smallestDimensionSize);

    // make sure the board is square.
    intersxHeightPx = smallestDimensionSize;
    intersxWidthPx = smallestDimensionSize;

    drawGrid(gctx2D, boardSize, intersxWidthPx, intersxHeightPx);
    return drawIntersections(gctx2D, position, intersxWidthPx, intersxHeightPx, highlightedIntersections);
  }

  protected void drawGrid(GraphicsContext gctx2D, int boardSize, double intersxWidthPx, double intersxHeightPx) {
    double defLineWidth = intersxHeightPx / 27;

    gctx2D.setStroke(COLOR_GRID_LINES);
    double lineWidth;
    for (int i = 0; i < boardSize; i++) {
      lineWidth = Math.round(defLineWidth);
      if ((i == 0) || (i == boardSize - 1)) lineWidth = Math.round(defLineWidth * 2);
      gctx2D.setLineWidth(lineWidth);

      // draw horizontal and vertical line.
      gctx2D.strokeLine(intersxWidthPx * (i + 0.5),
          intersxHeightPx / 2,
          intersxWidthPx * (i + 0.5),
          intersxHeightPx * (boardSize - 0.5));
      gctx2D.strokeLine(intersxWidthPx / 2,
          intersxHeightPx * (i + 0.5),
          intersxWidthPx * (boardSize - 0.5),
          intersxHeightPx * (i + 0.5));
    }

    List<Intersection> dotList = calculateDotList(boardSize);
    if (dotList != null) {
      for (Intersection is : dotList) {
        drawDot(gctx2D, intersxWidthPx * is.getX(), intersxHeightPx * is.getY(), intersxWidthPx, intersxHeightPx, defLineWidth);
      }
    }
  }

  protected Map<Intersection, DrawPosition> drawIntersections(GraphicsContext gctx2D, Position position,
                                                              double intersxWidthPx, double intersxHeightPx,
                                                              Set<Intersection> highlightedIntersections) {
    if (position == null) return null;

    Map<Intersection, DrawPosition> feedbackMap = new HashMap<>();

    int boardSize = position.getSize();
    for (int i = 0; i < boardSize; i++) {
      for (int j = 0; j < boardSize; j++) {

        Intersection intersection = Intersection.valueOf(i, j);
        Status status = position.getStatus(intersection);
        double offsetXPx = intersxWidthPx * i;
        double offsetYPx = intersxHeightPx * j;

        boolean isHighlighted = (highlightedIntersections != null) && highlightedIntersections.contains(intersection);
        drawIntersection(gctx2D, status, offsetXPx, offsetYPx, intersxWidthPx, intersxHeightPx, isHighlighted);

        DrawPosition drawPosition = new DrawPosition(intersection, new Rectangle(offsetXPx, offsetYPx, intersxWidthPx, intersxHeightPx));
        feedbackMap.put(intersection, drawPosition);
      }
    }

    return feedbackMap;
  }

  protected void drawIntersection(GraphicsContext gctx2D, Status pStatus,
                                double offsetXPx, double offsetYPx, double intersxWidthPx, double intersxHeightPx,
                                  boolean isHighlighted) {
    switch (pStatus) {
      case EMPTY:
        break;
      case BLACK:
        drawStone(gctx2D, offsetXPx, offsetYPx, intersxWidthPx, intersxHeightPx, Status.BLACK);
        break;
      case WHITE:
        drawStone(gctx2D, offsetXPx, offsetYPx, intersxWidthPx, intersxHeightPx, Status.WHITE);
        break;
      case EMPKO:
        drawKo(gctx2D, offsetXPx, offsetYPx, intersxWidthPx, intersxHeightPx);
        break;
    }

    if (isHighlighted)
    {
      drawMarker(gctx2D, offsetXPx, offsetYPx, intersxWidthPx, intersxHeightPx);
    }
  }

  protected void drawDot(GraphicsContext gctx2D, double x, double y, double width, double height, double defaultLineWidth) {
    double dotWidth = (defaultLineWidth * 2.5);
    gctx2D.setFill(COLOR_GRID_LINES);
    double marginWidth = width / 2 - dotWidth;
    double marginHeight = height / 2 - dotWidth;
    gctx2D.fillOval(x + marginWidth, y + marginHeight, width - 2 * marginWidth, height - 2 * marginHeight);
  }

  protected void drawStone(GraphicsContext gctx2D, double x, double y, double width, double height, Status pStatus) {
    double borderWidth = width / 16;
    gctx2D.setFill((pStatus == Status.BLACK) ? Color.BLACK : Color.WHITE);
    gctx2D.fillOval(x, y, width, height);
    gctx2D.setLineWidth(borderWidth);
    gctx2D.setStroke(COLOR_STONE_CIRCLE);
    gctx2D.strokeOval(x, y, width - borderWidth / 2, height - borderWidth / 2);
  }

  protected void drawKo(GraphicsContext gctx2D, double x, double y, double width, double height) {
    gctx2D.setStroke(COLOR_GRID_LINES);
    double marginWidth = width / 6;
    double marginHeight = height / 6;
    gctx2D.strokeRect(x + marginWidth, y + marginHeight, width - 2 * marginWidth, height - 2 * marginHeight);
  }

  private void drawMarker(GraphicsContext gctx2D, double x, double y, double width, double height) {
    double markerWidth = (width * 0.25);
    gctx2D.setFill(COLOR_MARKER);
    double marginWidth = width / 2 - markerWidth;
    double marginHeight = height / 2 - markerWidth;
    gctx2D.fillOval(x + marginWidth, y + marginHeight, width - 2 * marginWidth, height - 2 * marginHeight);
  }

  public static GobanDrawer.DrawPosition findDrawPosition(MouseEvent event, Map<Intersection, GobanDrawer.DrawPosition> drawPositionMap)
  {
    if (drawPositionMap == null) return null;
    double x = event.getX();
    double y = event.getY();

    // find collisions of mouse with drawn goban intersection.
    Collection<DrawPosition> drawPositions = drawPositionMap.values();
    for (GobanDrawer.DrawPosition drawPosition : drawPositions)
    {
      if (drawPosition.getRectangle().contains(x, y)) return drawPosition;
    }
    return null;
  }

  public static class DrawPosition
  {
    private Intersection intersection;
    private Rectangle rectangle;

    public DrawPosition(Intersection intersection, Rectangle rectangle) {
      this.intersection = intersection;
      this.rectangle = rectangle;
    }

    public Intersection getIntersection() {
      return intersection;
    }

    public Rectangle getRectangle() {
      return rectangle;
    }
  }
}
