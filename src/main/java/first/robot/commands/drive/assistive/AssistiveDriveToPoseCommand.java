package first.robot.commands.drive.assistive;

import static org.wpilib.units.Units.Meters;

import com.team2052.lib.helpers.MathHelpers;
import first.robot.mechanisms.drive.DrivetrainConstants;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import org.wpilib.math.geometry.Pose2d;

public class AssistiveDriveToPoseCommand extends AssistiveDriveCommand {

  protected final Supplier<Pose2d> targetPoseSupplier;

  public AssistiveDriveToPoseCommand(
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
