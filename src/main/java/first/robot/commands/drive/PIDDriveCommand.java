package first.robot.commands.drive;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import org.wpilib.units.measure.Distance;

import com.ctre.phoenix6.swerve.SwerveModule;
import com.ctre.phoenix6.swerve.SwerveRequest;
import com.team2052.lib.util.PIDFFConfiguration;

import first.robot.RobotState;
import first.robot.mechanisms.drive.DrivetrainConstants;

import org.wpilib.command3.Command;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.units.measure.Angle;

public abstract class PIDDriveCommand extends DefaultDriveCommand {

    protected final SwerveRequest.FieldCentricFacingAngle drive =
      new SwerveRequest.FieldCentricFacingAngle()
          .withDeadband(super.maxSpeed * driveDeadband)
          .withDriveRequestType(SwerveModule.DriveRequestType.Velocity);

    protected final Distance xyTolerance;
    protected final Angle rotationTolerance;
    protected final boolean endWhenWithinTolerance;

    protected final PIDFFConfiguration xyPidff;
    protected final PIDFFConfiguration rotationPidff;

    protected final Supplier<Pose2d> robotPoseSupplier = RobotState.getInstance()::getRobotPose;

    public PIDDriveCommand(DoubleSupplier xSupplier, DoubleSupplier ySupplier, DoubleSupplier rotationSupplier, BooleanSupplier fieldCentricSupplier, boolean endWhenWithinTolerance, PIDFFConfiguration xyPidff, PIDFFConfiguration rotationPidff) {
        super(xSupplier, ySupplier, rotationSupplier, fieldCentricSupplier);
        this.xyTolerance = DrivetrainConstants.XY_TOLERANCE;
        this.rotationTolerance = DrivetrainConstants.ROTATION_TOLERANCE;
        this.endWhenWithinTolerance = endWhenWithinTolerance;
        this.xyPidff = xyPidff;
        this.rotationPidff = rotationPidff;
    }

    @Override
    public Command create() {
        return super.create().until(() -> isWithinTolerance() && endWhenWithinTolerance).named("PID Drive Command");
    }

    public abstract boolean isWithinTolerance();

    public abstract boolean isOverridingX();

    public abstract boolean isOverridingY();

    public abstract boolean isOverridingRotation();

    public abstract Pose2d getTargetPose();

    public double getXPID() {
        
    }

}
