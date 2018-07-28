package brugo.go.io.sgf;

import java.util.ArrayList;
import java.util.List;

public class SGFFileNode extends SGFNode
{
  private String filename;
  private List<String> warningList;

  public SGFFileNode(String filename) {
    super(null);
    this.filename = filename;
  }

  public void addWarning(String warning) {
    if (warningList == null) warningList = new ArrayList<>(20);
    warningList.add(warning);
  }

  public List<String> getWarningList() {
    return ((warningList == null) || (warningList.isEmpty())) ? null : warningList;
  }

  public String getFilename() {
    return filename;
  }
}
