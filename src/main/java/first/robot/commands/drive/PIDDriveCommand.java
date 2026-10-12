package first.robot.commands.drive;

import static org.wpilib.units.Units.*;

import com.ctre.phoenix6.swerve.SwerveModule;
import com.ctre.phoenix6.swerve.SwerveRequest;
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
import org.wpilib.units.measure.Distance;

public abstract class PIDDriveCommand extends DefaultDriveCommand {

  protected final SwerveRequest.FieldCentricFacingAngle drive =
      new SwerveRequest.FieldCentricFacingAngle()
          .withDeadband(super.maxSpeed * driveDeadband)
          .withDriveRequestType(SwerveModule.DriveRequestType.Velocity);

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
    return getXPID(getTargetPose().getMeasureX());
  }

  @Override
  public double getY() {
    if (!isOverridingY()) {
      return super.getY();
    }
    return getYPID(getTargetPose().getMeasureY());
  }

  @Override
  public double getRotation() {
    if (!isOverridingRotation()) {
      return super.getRotation();
    }
    return getRotationPID(getTargetPose().getRotation().getMeasure());
  }

  public abstract boolean isWithinTolerance();

  public abstract boolean isOverridingX();

  public abstract boolean isOverridingY();

  public abstract boolean isOverridingRotation();

  public abstract Pose2d getTargetPose();

  public double getXPID(Distance targetX) {
    return xyPidff.calculate(robotPoseSupplier.get().getMeasureX().in(Meters), targetX.in(Meters));
  }

  public double getXPID(double targetX) {
    return getXPID(Meters.of(targetX));
  }

  public double getYPID(Distance targetY) {
    return xyPidff.calculate(robotPoseSupplier.get().getMeasureY().in(Meters), targetY.in(Meters));
  }

  public double getYPID(double targetY) {
    return getYPID(Meters.of(targetY));
  }

  public double getRotationPID(Angle targetRotation) {
    return rotationPidff.calculate(
        robotPoseSupplier.get().getRotation().getRadians(), targetRotation.in(Radians));
  }

  public double getRotationRadiansPID(double targetRotation) {
    return getRotationPID(Radians.of(targetRotation));
  }

  public double getRotationDegreesPID(double targetRotation) {
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
