package first.robot.commands.drive.bounding;

import static org.wpilib.units.Units.Meters;

import com.team2052.lib.geometry.LineSegment;
import first.robot.commands.drive.PIDDriveCommand;
import first.robot.mechanisms.drive.DrivetrainConstants;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Translation2d;

public class BoundLineDriveCommand extends PIDDriveCommand {

  protected final Supplier<LineSegment> targetLineSupplier;

  public BoundLineDriveCommand(
      DoubleSupplier xSupplier,
      DoubleSupplier ySupplier,
      DoubleSupplier rotationSupplier,
      BooleanSupplier fieldCentricSupplier,
      Supplier<LineSegment> targetLineSupplier) {
    super(
        xSupplier,
        ySupplier,
        rotationSupplier,
        fieldCentricSupplier,
        false,
        DrivetrainConstants.XY_PIDFF,
        DrivetrainConstants.ROTATION_PIDFF);
    this.targetLineSupplier = targetLineSupplier;
  }

  @Override
  public double getX() {
    LineSegment line = targetLineSupplier.get();
    double pid = super.getX();
    double controllerX = super.getControllerX();
    double controllerY = super.getControllerY();

    double dx = line.getEnd().getX() - line.getStart().getX();
    double dy = line.getEnd().getY() - line.getStart().getY();

    // if line is perfectly vertical, the controllerX will never be used so return the PID output.
    if (dx == 0) return pid;
    // if line is perfectly flat, the controllerY will never be used so return normal controllerX +
    // PID.
    if (dy == 0) return pid + controllerX;

    double lineSlope = dy / dx;
    double controllerSlope = controllerY / controllerX;

    if (controllerSlope >= lineSlope) {
      // Controllers Y is too large, return the PID output + controllerX, will be fixed in getY()
      return pid + controllerX;
    } else {
      // Controllers X is too large, scale it back to at the ration of controllerY
      return pid + (controllerY * dx / dy);
    }
  }

  @Override
  public double getY() {
    LineSegment line = targetLineSupplier.get();
    double pid = super.getX();
    double controllerX = super.getControllerX();
    double controllerY = super.getControllerY();

    double dx = line.getEnd().getX() - line.getStart().getX();
    double dy = line.getEnd().getY() - line.getStart().getY();

    // if line is perfectly vertical, the controllerX will never be used so return normal
    // controllerY + PID.
    if (dx == 0) return pid + controllerY;
    // if line is perfectly flat, the controllerY will never be used so return the PID output.
    if (dy == 0) return pid;

    double lineSlope = dy / dx;
    double controllerSlope = controllerY / controllerX;

    if (controllerSlope >= lineSlope) {
      // Controllers Y is too large, scale it back to at the ration of controllerX
      return pid + (controllerX * dy / dx);
    } else {
      // Controllers X is too large, return the PID output + controllerY, will be fixed in getX()
      return pid + controllerY;
    }
  }

  @Override
  public boolean isWithinTolerance() {
    return targetLineSupplier
        .get()
        .epsilonIsPointOnLine(robotPoseSupplier.get().getTranslation(), xyTolerance.in(Meters));
  }

  @Override
  public boolean isOverridingX() {
    return true;
  }

  @Override
  public boolean isOverridingY() {
    return true;
  }

  @Override
  public boolean isOverridingRotation() {
    return false;
  }

  @Override
  public Pose2d getTargetPose() {
    Translation2d closestPointOnSegment =
        targetLineSupplier.get().getClosestPointOnSegment(robotPoseSupplier.get().getTranslation());
    return new Pose2d(closestPointOnSegment, robotPoseSupplier.get().getRotation());
  }
}
