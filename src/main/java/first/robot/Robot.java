// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package first.robot;

import com.team2052.lib.subsystems.PeriodicMechanism;
import com.team2052.lib.util.RobotInfo;
import first.robot.mechanisms.drive.DrivetrainMechanism;
import first.robot.utils.FieldConstants;
import org.wpilib.driverstation.DriverStationDisplay;
import org.wpilib.fields.Fields;
import org.wpilib.framework.OpModeRobot;

/**
 * The methods in this class are called automatically as described in the OpModeRobot documentation.
 * OpMode classes anywhere in the package (or sub-packages) where this class is located are
 * automatically registered to display in the Driver Station. If you change the name of this class
 * or the package after creating this project, you must also update the Main.java file in the
 * project.
 */
public class Robot extends OpModeRobot {
  private final RobotContainer robotContainer = RobotContainer.getInstance();
  private String currentOpMode;

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  public Robot() {
    currentOpMode = super.getOpMode();
    Fields loadingConstants = FieldConstants.FIELD;
    DriverStationDisplay.addKeyedLine(
        "Loading Constants",
        "Loading field constants to parse JSON: " + loadingConstants.toString());
  }

  /** This function is called exactly once when the DS first connects. */
  @Override
  public void driverStationConnected() {
    if (RobotInfo.isInMatch()) {
      String matchName = RobotInfo.getMatchType().toString();
      matchName = matchName.substring(0, 1) + matchName.substring(1).toLowerCase();
      DriverStationDisplay.addKeyedLine(
          "Connection",
          "Welcome to "
              + matchName
              + " Match "
              + RobotInfo.getMatchNumber()
              + " at The "
              + RobotInfo.getEventName()
              + "!");
    } else {
      DriverStationDisplay.addKeyedLine(
          "Connection", "You are now connected to [2027 Biocore Robot Name].");
    }
  }

  /**
   * This function is called periodically anytime when no opmode is selected, including when the
   * Driver Station is disconnected.
   */
  @Override
  public void nonePeriodic() {}

  @Override
  public void robotPeriodic() {
    String opMode = super.getOpMode();

    if (!opMode.equals(currentOpMode)) {
      robotContainer.configureBindings(opMode);
      currentOpMode = opMode;
    }

    DrivetrainMechanism drivetrain = DrivetrainMechanism.getInstance();

    drivetrain.inputPeriodic();
    PeriodicMechanism.runAllInputPeriodics();
    RobotState.getInstance().robotStateInputPeriodic();
    drivetrain.loggingPeriodic();
    PeriodicMechanism.runAllLoggingPeriodics();
    RobotState.getInstance().robotStateLoggingPeriodic();
    drivetrain.outputPeriodic();
    PeriodicMechanism.runAllOutputPeriodics();
  }
}
