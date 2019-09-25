package brugo.go.ui.javafx.goban;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import brugo.go.bo.state.Intersection;
import brugo.go.bo.state.Position;
import brugo.go.bo.state.Status;

/**
 * A demo for the use of a GobanComponent.
 */
public class GobanComponentDemo extends Application {

  @Override
  public void start(Stage primaryStage) {
    AnchorPane rootPane = new AnchorPane();

    // create position
    Position pos = new Position(19, 6.5);
    pos = pos.play(Intersection.valueOf(16, 3), Status.BLACK);
    pos = pos.play(Intersection.valueOf(3, 2), Status.WHITE);
    pos = pos.play(Intersection.valueOf(15, 16), Status.BLACK);
    pos = pos.play(Intersection.valueOf(14, 3), Status.WHITE);
    pos = pos.play(Intersection.valueOf(2, 15), Status.BLACK);
    pos = pos.play(Intersection.valueOf(15, 14), Status.WHITE);

    // create goban component
    GobanComponent gobanComponent = new GobanComponent();
    gobanComponent.onChange(title -> primaryStage.setTitle("Goban Demo | " + title));

    // show position in goban component
    gobanComponent.setPosition(pos, Status.BLACK);

    // add component to pane and link to the 4 borders.
    rootPane.getChildren().add(gobanComponent);
    AnchorPane.setTopAnchor(gobanComponent, 0d);
    AnchorPane.setLeftAnchor(gobanComponent, 0d);
    AnchorPane.setRightAnchor(gobanComponent, 0d);
    AnchorPane.setBottomAnchor(gobanComponent, 0d);

    primaryStage.setScene(new Scene(rootPane, 500, 500));
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
