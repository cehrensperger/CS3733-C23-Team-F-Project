package edu.wpi.FlashyFrogs.PathFinding;

import edu.wpi.FlashyFrogs.ORM.Node;
import java.util.List;
import lombok.NonNull;
import org.hibernate.Session;

public interface IFindPath {
  public List<Node> findPath(
      @NonNull Node start, @NonNull Node end, @NonNull Boolean accessible, @NonNull Session session)
      throws Exception;
}
