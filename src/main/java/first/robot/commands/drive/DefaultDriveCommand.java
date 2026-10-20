// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package first.robot.commands.drive;

import static org.wpilib.units.Units.*;

import com.ctre.phoenix6.swerve.SwerveModule;
import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;
import com.team2052.lib.commands.CommandBase;
import com.team2052.lib.commands.CommandBuilder;
import first.robot.mechanisms.drive.DrivetrainConstants;
import first.robot.mechanisms.drive.DrivetrainMechanism;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import org.wpilib.command3.Command;
import org.wpilib.math.util.MathUtil;

public class DefaultDriveCommand implements CommandBase {

  protected final DrivetrainMechanism drivetrain = DrivetrainMechanism.getInstance();

  private final DoubleSupplier xSupplier;
  private final DoubleSupplier ySupplier;
  private final DoubleSupplier rotationSupplier;
  private final BooleanSupplier fieldCentricSupplier;

  protected final double maxSpeed = DrivetrainConstants.DRIVE_MAX_SPEED.in(MetersPerSecond);
  protected final double maxAngularRate =
      DrivetrainConstants.DRIVE_MAX_ANGULAR_RATE.in(RadiansPerSecond);
  protected final double driveDeadband = DrivetrainConstants.DRIVE_DEADBAND;

  /** Field centric control method that is designed to be derived from joystick inputs */
  protected final SwerveRequest.FieldCentric driveNoHeading =
      new SwerveRequest.FieldCentric()
          .withDeadband(maxSpeed * driveDeadband) // Add a 5% deadband in open loop
          .withRotationalDeadband(maxAngularRate * driveDeadband)
          .withDriveRequestType(SwerveModule.DriveRequestType.Velocity);

  /**
   * Field centric control method that is derived from {@link
   * org.wpilib.math.kinematics.ChassisVelocities}
   */
  protected final SwerveRequest.ApplyFieldVelocity driveChassisVelocities =
      new SwerveRequest.ApplyFieldVelocity()
          .withDesaturateWheelVelocities(true)
          .withDriveRequestType(SwerveModule.DriveRequestType.Velocity);

  /** Robot centric control method */
  protected final SwerveRequest.RobotCentric robotCentricDrive =
      new SwerveRequest.RobotCentric()
          .withDeadband(maxSpeed * driveDeadband)
          .withRotationalDeadband(maxAngularRate * driveDeadband) // Add a 5% deadband
          .withDriveRequestType(DriveRequestType.Velocity)
          .withDesaturateWheelVelocities(true);

  /** Field centric control method that inputs a specific heading */
  protected SwerveRequest.FieldCentricFacingAngle driveWithHeading =
      new SwerveRequest.FieldCentricFacingAngle()
          .withDeadband(maxSpeed * driveDeadband)
          .withDriveRequestType(SwerveModule.DriveRequestType.Velocity);

  /**
   * @param xSupplier supplier for forward velocity.
   * @param ySupplier supplier for sideways velocity.
   * @param rotationSupplier supplier for angular velocity.
   * @param fieldCentricSupplier supplier for field centric control.
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
        coroutine -> {
          while (true) {
            drivetrain.setControl(getSwerveRequest());
            coroutine.yield();
          }
        },
        "Default Drive Command",
        drivetrain);
  }

  /**
   * Gets the x value that is used to generate the swerve request
   *
   * @return the x value
   */
  protected double getX() {
    return -MathUtil.applyDeadband(xSupplier.getAsDouble(), driveDeadband);
  }

  /**
   * Gets the y value that is used to generate the swerve request
   *
   * @return the y value
   */
  protected double getY() {
    return -MathUtil.applyDeadband(ySupplier.getAsDouble(), driveDeadband);
  }

  /**
   * Gets the rotation value that is used to generate the swerve request
   *
   * @return the rotation value
   */
  protected double getRotation() {
    return -MathUtil.applyDeadband(rotationSupplier.getAsDouble(), driveDeadband);
  }

  /**
   * Gets weather the robot is being driven via field centric or robot centric control.
   *
   * @return true if field centric, false if robot centric
   */
  protected boolean getFieldCentric() {
    return fieldCentricSupplier.getAsBoolean();
  }

  /**
   * Gets the swerve request based on the current control mode.
   *
   * @return the swerve request
   */
  protected SwerveRequest getSwerveRequest() {
    if (getFieldCentric()) {
      return driveNoHeading
          .withVelocityX(getX() * maxSpeed)
          .withVelocityY(getY() * maxSpeed)
          .withRotationalRate(getRotation() * maxAngularRate);
    } else {
      return robotCentricDrive
          .withVelocityX(getX() * maxSpeed)
          .withVelocityY(getY() * maxSpeed)
          .withRotationalRate(getRotation() * maxAngularRate);
    }
  }
}
