package brugo.go.ui.javafx.goban;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Oleksandr_Baglai on 2019-09-26.
 */
public class History {
  protected List<Step> history = new LinkedList<>();
  protected int historyIndex;

  public boolean isEmpty() {
    return history.isEmpty();
  }

  public void saveStep(Step step) {
    if (!history.isEmpty() && historyIndex != history.size() - 1) {
      history = new LinkedList<>(history.subList(0, historyIndex + 1));
    }
    history.add(step);
    historyIndex = history.size() - 1;
  }

  public void goBack(Consumer<Step> onLoad) {
    if (historyIndex == 0) return;

    historyIndex--;

    Step step = history.get(historyIndex);
    onLoad.accept(step);
  }

  public void goForward(Consumer<Step> onLoad) {
    if (historyIndex == history.size() - 1) return;

    historyIndex++;

    Step step = history.get(historyIndex);
    onLoad.accept(step);
  }

}
