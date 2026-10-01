package first.robot.mechanisms.drive;

import static org.wpilib.units.Units.Seconds;

import com.ctre.phoenix6.Utils;
import com.ctre.phoenix6.swerve.SwerveRequest;
import com.team2052.lib.estimators.PositionEstimator.OdometryMeasurement;
import com.team2052.lib.estimators.PositionEstimator.PoseMeasurement;
import com.team2052.lib.util.RobotInfo;
import com.team2052.lib.vision.StandardDeviationCalculator;
import first.robot.RobotState;
import first.robot.mechanisms.drive.ctre.generated.TunerConstants.TunerSwerveDrivetrain;
import java.util.function.Supplier;
import org.wpilib.command3.Command;
import org.wpilib.command3.Mechanism;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.kinematics.ChassisVelocities;
import org.wpilib.math.linalg.VecBuilder;
import org.wpilib.math.linalg.Vector;
import org.wpilib.math.numbers.N3;
import org.wpilib.system.Timer;

public class DrivetrainMechanism extends TunerSwerveDrivetrain implements Mechanism {

  private final SwerveRequest.ApplyRobotVelocity autoRequest =
      new SwerveRequest.ApplyRobotVelocity();

  private RobotState robotState = RobotState.getInstance();

  private boolean hasAppliedOperatorPerspective = false;

  private static DrivetrainMechanism INSTANCE;

  public static DrivetrainMechanism getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new DrivetrainMechanism();
    }

    return INSTANCE;
  }

  private DrivetrainMechanism() {
    super(
        DrivetrainConstants.TUNER_DRIVETRAIN_CONSTANTS.getDrivetrainConstants(),
        100,
        DrivetrainConstants.ODOMETRY_STDDEV,
        VecBuilder.fill(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE),
        DrivetrainConstants.TUNER_DRIVETRAIN_CONSTANTS.getModuleConstants());
    configureAutoBuilder();
  }

  private void configureAutoBuilder() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'configureAutoBuilder'");
  }

  /**
   * Command that continually applies {@link SwerveRequest} to the drivetrain
   *
   * @param requestSupplier supplier of {@link SwerveRequest}
   * @return The {@link Command}
   */
  public Command applyRequest(Supplier<SwerveRequest> requestSupplier) {
    return run(coroutine -> {
          while (true) {
            setControl(requestSupplier.get());
            coroutine.yield();
          }
        })
        .named("Apply Swerve Request");
  }

  /** Stops the drivetrain */
  public void stop() {
    setControl(new SwerveRequest.ApplyRobotVelocity().withVelocity(new ChassisVelocities()));
  }

  /**
   * {@link Command} that stops the drivetrain
   *
   * @return The {@link Command}
   */
  public Command stopDrivetrain() {
    return run(coroutine -> stop()).named("Stop Drivetrain");
  }

  /**
   * Gets the {@link OdometryMeasurement} of the wheel odometry.
   *
   * @return The {@link OdometryMeasurement}
   */
  public OdometryMeasurement getWheelOdometryMeasurement() {
    SwerveDriveState state = getStateCopy();
    Vector<N3> stddev =
        StandardDeviationCalculator.getWheelOdometryStdDev(
            DrivetrainConstants.XY_STDDEV_COEFFICIENT,
            DrivetrainConstants.ANGLE_STDDEV_COEFFICIENT,
            state,
            getRotation3d(),
            DrivetrainConstants.ODOMETRY_STDDEV);

    PoseMeasurement measurement =
        new PoseMeasurement(
            state.Pose,
            stddev,
            Seconds.of(Utils.getCurrentTimeSeconds() - state.Timestamp),
            Seconds.of(Timer.getMonotonicTimestamp()));
    return new OdometryMeasurement(measurement, "Wheel Odometry");
  }

  public ChassisVelocities getChassisVelocities() {
    return getStateCopy().Velocity;
  }

  public void inputPeriodic() {}

  public void loggingPeriodic() {}

  public void outputPeriodic() {
    // Check if we haven't applied the operator perspective in drivetrain yer
    if (!hasAppliedOperatorPerspective || RobotInfo.isDisabled()) {
      setOperatorForwardDirection(
          RobotInfo.isRedAlliance() ? Rotation2d.fromDegrees(180) : Rotation2d.fromDegrees(0));
      hasAppliedOperatorPerspective = true;
    }
  }
}
