package brugo.go.bo.state;

import java.util.ArrayList;
import java.util.List;

public class PositionUtil
{
  public List<Intersection> getOneSpaceEyeSet(Position position, Status status)
  {
    int size = position.getSize();

    List<Intersection> intersectionList = new ArrayList<>(size * size);
    Chain neighbourChain;
    for (int i = 0; i < size; i++)
    {
      boardLoop:
      for (int j = 0; j < size; j++)
      {
        if (!position.getStatus(i, j).isEmpty()) continue;

        neighbourChain = Intersection.valueOf(i, j).getNeighbourSet();
        if (neighbourChain == null) continue;

        // all 4 neighbours have to match color
        for (Intersection neighbour : neighbourChain.getIntersectionSet())
        {
          Status neightbourStatus = position.getStatus(neighbour);
          if ((neightbourStatus != null) && (neightbourStatus != status)) continue boardLoop;
        }
        intersectionList.add(Intersection.valueOf(i, j));
      }
    }
    return (intersectionList.isEmpty()) ? null : intersectionList;
  }
}
