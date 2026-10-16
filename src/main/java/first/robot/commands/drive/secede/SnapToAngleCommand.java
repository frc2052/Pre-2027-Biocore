package first.robot.commands.drive.secede;

import com.team2052.lib.helpers.MathHelpers;
import first.robot.commands.drive.PIDDriveCommand;
import first.robot.mechanisms.drive.DrivetrainConstants;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Rotation2d;

public class SnapToAngleCommand extends PIDDriveCommand {

  protected final Supplier<Rotation2d> targetAngleSupplier;

  public SnapToAngleCommand(
      DoubleSupplier xSupplier,
      DoubleSupplier ySupplier,
      DoubleSupplier rotationSupplier,
      BooleanSupplier fieldCentricSupplier,
      boolean endWhenWithinTolerance,
      Supplier<Rotation2d> targetAngleSupplier) {
    super(
        xSupplier,
        ySupplier,
        rotationSupplier,
        fieldCentricSupplier,
        endWhenWithinTolerance,
        DrivetrainConstants.XY_PIDFF,
        DrivetrainConstants.ROTATION_PIDFF);
    this.targetAngleSupplier = targetAngleSupplier;
  }

  public SnapToAngleCommand(
      DoubleSupplier xSupplier,
      DoubleSupplier ySupplier,
      BooleanSupplier fieldCentricSupplier,
      boolean endWhenWithinTolerance,
      Supplier<Rotation2d> targetAngleSupplier) {
    this(
        xSupplier,
        ySupplier,
        () -> 0.0,
        fieldCentricSupplier,
        endWhenWithinTolerance,
        targetAngleSupplier);
  }

  @Override
  public boolean isWithinTolerance() {
    return MathHelpers.angleEpsilonEquals(
        targetAngleSupplier.get().getMeasure(),
        robotPoseSupplier.get().getRotation().getMeasure(),
        rotationTolerance);
  }

  @Override
  public boolean isOverridingX() {
    return false;
  }

  @Override
  public boolean isOverridingY() {
    return false;
  }

  @Override
  public boolean isOverridingRotation() {
    return true;
  }

  @Override
  public Pose2d getTargetPose() {
    return new Pose2d(robotPoseSupplier.get().getTranslation(), targetAngleSupplier.get());
  }
}
