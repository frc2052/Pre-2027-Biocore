package first.robot;

import com.team2052.lib.estimators.PositionEstimator;
import first.robot.mechanisms.drive.DrivetrainMechanism;
import first.robot.mechanisms.vision.VisionMechanism;
import first.robot.utils.FieldConstants;
import lombok.Getter;
import lombok.Setter;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.kinematics.ChassisVelocities;

public class RobotState {
  private static RobotState INSTANCE;

  @Getter
  private PositionEstimator estimator =
      new PositionEstimator(FieldConstants.FieldRegions.VALID_ROBOT_POSE_REGION);

  @Getter private Pose2d autoStartPose = new Pose2d();
  @Getter @Setter private ChassisVelocities wheelVelocity = new ChassisVelocities();

  public static RobotState getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new RobotState();
    }
    return INSTANCE;
  }

  private RobotState() {}

  public Pose2d getRobotPose() {
    return estimator.getLastPose();
  }

  public void robotStateInputPeriodic() {}

  public void robotStateLoggingPeriodic() {}

  public void seedAutoStartPose(Pose2d pose) {
    autoStartPose = pose;
    seedPose(pose);
  }

  public void seedPose(Pose2d pose) {
    estimator.seedAtRest(pose);
    estimator.resetOdometries();
    DrivetrainMechanism.getInstance().resetPose(pose);
    VisionMechanism.getInstance().seedRobotPose(pose);
  }
}
