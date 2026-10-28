package first.robot;

import com.team2052.lib.estimators.PositionEstimator;
import com.team2052.lib.util.RobotInfo;
import first.robot.mechanisms.drive.DrivetrainMechanism;
import first.robot.mechanisms.vision.VisionMechanism;
import first.robot.utils.FieldConstants;
import lombok.Getter;
import lombok.Setter;
import org.littletonrobotics.junction.Logger;
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

  public void robotStateLoggingPeriodic() {
    // Estimator Logging
    Logger.recordOutput("Robot State/Estimator/Pose/Value", estimator.getLastPose());
    Logger.recordOutput("Robot State/Estimator/Velocity/Value", estimator.getLastVelocity());
    Logger.recordOutput(
        "Robot State/Estimator/Acceleration/Value", estimator.getLastAcceleration());
    Logger.recordOutput(
        "Robot State/Estimator/Jerk/Value", estimator.getJerkPositionVector().toTransform2d());
    Logger.recordOutput(
        "Robot State/Estimator/Pose/Standard Deviation", estimator.getLastPoseStdDev());
    Logger.recordOutput(
        "Robot State/Estimator/Velocity/Standard Deviation", estimator.getLastVelocityStdDev());
    Logger.recordOutput(
        "Robot State/Estimator/Acceleration/Standard Deviation",
        estimator.getLastAccelerationStdDev());
    Logger.recordOutput(
        "Robot State/Estimator/Jerk/Standard Deviation", estimator.getLastJerkStdDev());

    // Wheel Velocity
    Logger.recordOutput("Robot State/Drivetrain/Wheel Velocity", wheelVelocity);

    // Auto
    if (RobotInfo.isDisabled()) {
      Logger.recordOutput("Robot State/Auto/Start Pose", autoStartPose);
    }
  }

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
