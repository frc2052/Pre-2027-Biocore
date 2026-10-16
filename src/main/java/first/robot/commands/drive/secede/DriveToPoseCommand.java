package first.robot.commands.drive.secede;

import static org.wpilib.units.Units.Meters;

import com.team2052.lib.helpers.MathHelpers;
import first.robot.commands.drive.PIDDriveCommand;
import first.robot.mechanisms.drive.DrivetrainConstants;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import org.wpilib.math.geometry.Pose2d;

public class DriveToPoseCommand extends PIDDriveCommand {

  protected final Supplier<Pose2d> targetPoseSupplier;

  public DriveToPoseCommand(
      DoubleSupplier xSupplier,
      DoubleSupplier ySupplier,
      DoubleSupplier rotationSupplier,
      BooleanSupplier fieldCentricSupplier,
      boolean endWhenWithinTolerance,
      Supplier<Pose2d> targetPoseSupplier) {
    super(
        xSupplier,
        ySupplier,
        rotationSupplier,
        fieldCentricSupplier,
        endWhenWithinTolerance,
        DrivetrainConstants.XY_PIDFF,
        DrivetrainConstants.ROTATION_PIDFF);
    this.targetPoseSupplier = targetPoseSupplier;
  }

  public DriveToPoseCommand(boolean endWhenWithinTolerance, Supplier<Pose2d> targetPoseSupplier) {
    this(
        () -> 0.0,
        () -> 0.0,
        () -> 0.0,
        () -> false,
        endWhenWithinTolerance,
        targetPoseSupplier);
  }

  @Override
  public boolean isWithinTolerance() {
    return MathHelpers.pose2dEpsilonEquals(
        getTargetPose(), robotPoseSupplier.get(), xyTolerance.in(Meters), rotationTolerance);
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
    return true;
  }

  @Override
  public Pose2d getTargetPose() {
    return targetPoseSupplier.get();
  }
}
