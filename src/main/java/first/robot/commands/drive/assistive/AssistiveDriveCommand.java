package first.robot.commands.drive.assistive;

import static org.wpilib.units.Units.MetersPerSecond;
import static org.wpilib.units.Units.RadiansPerSecond;

import com.team2052.lib.helpers.MathHelpers;
import com.team2052.lib.util.PIDFFConfiguration;
import first.robot.commands.drive.PIDDriveCommand;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

public abstract class AssistiveDriveCommand extends PIDDriveCommand {

  public AssistiveDriveCommand(
      DoubleSupplier xSupplier,
      DoubleSupplier ySupplier,
      DoubleSupplier rotationSupplier,
      BooleanSupplier fieldCentricSupplier,
      boolean endWhenWithinTolerance,
      PIDFFConfiguration xyPidff,
      PIDFFConfiguration rotationPidff) {
    super(
        xSupplier,
        ySupplier,
        rotationSupplier,
        fieldCentricSupplier,
        endWhenWithinTolerance,
        xyPidff,
        rotationPidff);
  }

  @Override
  public double getX() {
    if (!isOverridingX()) {
      return super.getX();
    }
    double pid = getXPID(getTargetPose().getMeasureX()).in(MetersPerSecond) / maxSpeed;
    double controller = super.getControllerX();
    return MathHelpers.clamp(controller + pid, -1, 1);
  }

  @Override
  public double getY() {
    if (!isOverridingY()) {
      return super.getY();
    }
    double pid = getYPID(getTargetPose().getMeasureY()).in(MetersPerSecond) / maxSpeed;
    double controller = super.getControllerY();
    return MathHelpers.clamp(controller + pid, -1, 1);
  }

  @Override
  public double getRotation() {
    if (!isOverridingRotation()) {
      return super.getRotation();
    }
    double pid =
        getRotationPID(getTargetPose().getRotation().getMeasure()).in(RadiansPerSecond)
            / maxAngularRate;
    double controller = super.getControllerRotation();
    return MathHelpers.clamp(controller + pid, -1, 1);
  }
}
