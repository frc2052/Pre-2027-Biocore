package first.robot.commands.drive.assistive;

import static org.wpilib.units.Units.Meters;

import com.team2052.lib.helpers.MathHelpers;
import first.robot.mechanisms.drive.DrivetrainConstants;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Translation2d;

public class AssistiveDriveToTranslationCommand extends AssistiveDriveCommand {

  protected final Supplier<Translation2d> targetTranslationSupplier;

  public AssistiveDriveToTranslationCommand(
      DoubleSupplier xSupplier,
      DoubleSupplier ySupplier,
      DoubleSupplier rotationSupplier,
      BooleanSupplier fieldCentricSupplier,
      boolean endWhenWithinTolerance,
      Supplier<Translation2d> targetTranslationSupplier) {
    super(
        xSupplier,
        ySupplier,
        rotationSupplier,
        fieldCentricSupplier,
        endWhenWithinTolerance,
        DrivetrainConstants.XY_PIDFF,
        DrivetrainConstants.ROTATION_PIDFF);
    this.targetTranslationSupplier = targetTranslationSupplier;
  }

  @Override
  public boolean isWithinTolerance() {
    return MathHelpers.translation2dEpsilonEquals(
        targetTranslationSupplier.get(),
        robotPoseSupplier.get().getTranslation(),
        xyTolerance.in(Meters));
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
    return new Pose2d(targetTranslationSupplier.get(), robotPoseSupplier.get().getRotation());
  }
}
