// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package first.robot.commands.drive;

import static org.wpilib.units.Units.*;

import com.ctre.phoenix6.swerve.SwerveModule;
import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.team2052.lib.commands.CommandBase;
import com.team2052.lib.commands.CommandBuilder;
import com.ctre.phoenix6.swerve.SwerveRequest;

import first.robot.mechanisms.drive.DrivetrainConstants;
import first.robot.mechanisms.drive.DrivetrainMechanism;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import org.wpilib.command3.Command;
import org.wpilib.math.util.MathUtil;

public class DefaultDriveCommand implements CommandBase{

    protected final DrivetrainMechanism drivetrainMechanism = DrivetrainMechanism.getInstance();

    private final DoubleSupplier xSupplier;
    private final DoubleSupplier ySupplier;
    private final DoubleSupplier rotationSupplier;
    private final BooleanSupplier fieldCentricSupplier;

    protected final double maxSpeed = DrivetrainConstants.DRIVE_MAX_SPEED.in(MetersPerSecond);
    protected final double maxAngularRate = DrivetrainConstants.DRIVE_MAX_ANGULAR_RATE.in(RadiansPerSecond);
    protected final double driveDeadband = DrivetrainConstants.DRIVE_DEADBAND;

    protected final SwerveRequest.FieldCentric driveNoHeading = new SwerveRequest.FieldCentric()
            .withDeadband(maxSpeed * driveDeadband) // Add a 5% deadband in open loop
            .withRotationalDeadband(maxAngularRate * driveDeadband)
            .withDriveRequestType(SwerveModule.DriveRequestType.Velocity);

    protected final SwerveRequest.ApplyFieldVelocity driveChassisVelocities = new SwerveRequest.ApplyFieldVelocity()
            .withDesaturateWheelVelocities(true)
            .withDriveRequestType(SwerveModule.DriveRequestType.Velocity);

    protected final SwerveRequest.RobotCentric robotCentricDrive = new SwerveRequest.RobotCentric()
            .withDeadband(maxSpeed * driveDeadband)
            .withRotationalDeadband(maxAngularRate * driveDeadband) // Add a 5% deadband
            .withDriveRequestType(DriveRequestType.Velocity)
            .withDesaturateWheelVelocities(true);

    protected SwerveRequest.FieldCentricFacingAngle driveWithHeading = new SwerveRequest.FieldCentricFacingAngle()
            .withDeadband(maxSpeed * driveDeadband)
            .withDriveRequestType(SwerveModule.DriveRequestType.Velocity);

    /**
     * @param xSupplier        supplier for forward velocity.
     * @param ySupplier        supplier for sideways velocity.
     * @param rotationSupplier supplier for angular velocity.
     */
    public DefaultDriveCommand(
            DoubleSupplier xSupplier,
            DoubleSupplier ySupplier,
            DoubleSupplier rotationSupplier,
            BooleanSupplier fieldCentricSupplier) {

        this.xSupplier = xSupplier;
        this.ySupplier = ySupplier;
        this.rotationSupplier = rotationSupplier;
        this.fieldCentricSupplier = fieldCentricSupplier;

        driveWithHeading.HeadingController.setPID(2.5, 0.0, 0.0);
        driveWithHeading.HeadingController.enableContinuousInput(-Math.PI, Math.PI);
    }

    @Override
    public Command create() {
        return CommandBuilder.create(
                drivetrainMechanism,
                "Default Drive Command",
                coroutine -> {
                    while (true) {
                        drivetrainMechanism.setControl(getSwerveRequest());
                        coroutine.yield();
                    }
                });
    }

    // @Override
    // public void initialize() {}

    protected double getX() {
        return -MathUtil.applyDeadband(xSupplier.getAsDouble(), driveDeadband);
    }

    protected double getY() {
        return -MathUtil.applyDeadband(ySupplier.getAsDouble(), driveDeadband);
    }

    protected double getRotation() {
        return -MathUtil.applyDeadband(rotationSupplier.getAsDouble(), driveDeadband);
    }

    protected boolean getFieldCentric() {
        return fieldCentricSupplier.getAsBoolean();
    }

    protected SwerveRequest getSwerveRequest() {
        if (getFieldCentric()) {
            return driveNoHeading
                    .withVelocityX(getX() * maxSpeed)
                    .withVelocityY(getY() * maxSpeed)
                    .withRotationalRate(getRotation() * maxAngularRate);
        } else {
            return robotCentricDrive
                    .withVelocityX(getX())
                    .withVelocityY(getY())
                    .withRotationalRate(getRotation());
        }
    }
}