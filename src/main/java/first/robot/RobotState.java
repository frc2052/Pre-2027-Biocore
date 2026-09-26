package first.robot;

import com.team2052.lib.estimators.PositionEstimator;
import lombok.Getter;

public class RobotState {
  private static RobotState INSTANCE;

  @Getter private PositionEstimator estimator = new PositionEstimator();

  public static RobotState getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new RobotState();
    }
    return INSTANCE;
  }

  private RobotState() {}

  public void robotStateInputPeriodic() {}

  public void robotStateLoggingPeriodic() {}
}
