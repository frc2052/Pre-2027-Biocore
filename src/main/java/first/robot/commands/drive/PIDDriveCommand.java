package first.robot.commands.drive;

import static org.wpilib.units.Units.*;

import com.team2052.lib.controllers.PIDFFController;
import com.team2052.lib.util.PIDFFConfiguration;
import first.robot.RobotState;
import first.robot.mechanisms.drive.DrivetrainConstants;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import org.wpilib.command3.Command;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.units.measure.Angle;
import org.wpilib.units.measure.AngularVelocity;
import org.wpilib.units.measure.Distance;
import org.wpilib.units.measure.LinearVelocity;

public abstract class PIDDriveCommand extends DefaultDriveCommand {

  protected final Distance xyTolerance;
  protected final Angle rotationTolerance;
  protected final boolean endWhenWithinTolerance;

  protected final PIDFFController xyPidff;
  protected final PIDFFController rotationPidff;

  protected final Supplier<Pose2d> robotPoseSupplier = RobotState.getInstance()::getRobotPose;

  public PIDDriveCommand(
      DoubleSupplier xSupplier,
      DoubleSupplier ySupplier,
      DoubleSupplier rotationSupplier,
      BooleanSupplier fieldCentricSupplier,
      boolean endWhenWithinTolerance,
      PIDFFConfiguration xyPidff,
      PIDFFConfiguration rotationPidff) {
    super(xSupplier, ySupplier, rotationSupplier, fieldCentricSupplier);
    this.xyTolerance = DrivetrainConstants.XY_TOLERANCE;
    this.rotationTolerance = DrivetrainConstants.ROTATION_TOLERANCE;
    this.endWhenWithinTolerance = endWhenWithinTolerance;
    this.xyPidff = new PIDFFController(xyPidff);
    this.rotationPidff = new PIDFFController(rotationPidff);
  }

  @Override
  public Command create() {
    return super.create()
        .until(() -> endWhenWithinTolerance && isWithinTolerance())
        .named("PID Drive Command");
  }

  @Override
  public double getX() {
    if (!isOverridingX()) {
      return super.getX();
    }
    return getXPID(getTargetPose().getMeasureX()).in(MetersPerSecond) / maxSpeed;
  }

  @Override
  public double getY() {
    if (!isOverridingY()) {
      return super.getY();
    }
    return getYPID(getTargetPose().getMeasureY()).in(MetersPerSecond) / maxSpeed;
  }

  @Override
  public double getRotation() {
    if (!isOverridingRotation()) {
      return super.getRotation();
    }
    return getRotationPID(getTargetPose().getRotation().getMeasure()).in(RadiansPerSecond)
        / maxAngularRate;
  }

  public abstract boolean isWithinTolerance();

  public abstract boolean isOverridingX();

  public abstract boolean isOverridingY();

  public abstract boolean isOverridingRotation();

  public abstract Pose2d getTargetPose();

  public LinearVelocity getXPID(Distance targetX) {
    return MetersPerSecond.of(
        xyPidff.calculate(robotPoseSupplier.get().getMeasureX().in(Meters), targetX.in(Meters)));
  }

  public LinearVelocity getXPID(double targetX) {
    return getXPID(Meters.of(targetX));
  }

  public LinearVelocity getYPID(Distance targetY) {
    return MetersPerSecond.of(
        xyPidff.calculate(robotPoseSupplier.get().getMeasureY().in(Meters), targetY.in(Meters)));
  }

  public LinearVelocity getYPID(double targetY) {
    return getYPID(Meters.of(targetY));
  }

  public AngularVelocity getRotationPID(Angle targetRotation) {
    return RadiansPerSecond.of(
        rotationPidff.calculate(
            robotPoseSupplier.get().getRotation().getRadians(), targetRotation.in(Radians)));
  }

  public AngularVelocity getRotationRadiansPID(double targetRotation) {
    return getRotationPID(Radians.of(targetRotation));
  }

  public AngularVelocity getRotationDegreesPID(double targetRotation) {
    return getRotationPID(Degrees.of(targetRotation));
  }

  public double getControllerX() {
    return super.getX();
  }

  public double getControllerY() {
    return super.getY();
  }

  public double getControllerRotation() {
    return super.getRotation();
  }
}
