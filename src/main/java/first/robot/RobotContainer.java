package first.robot;

import com.team2052.lib.input.JoinedCommandJoystick;
import com.team2052.lib.input.T16000MJoystick;
import org.wpilib.driverstation.DriverStationErrors;
import org.wpilib.driverstation.internal.DriverStationBackend;

public class RobotContainer {
  private static RobotContainer INSTNACE;

  public final T16000MJoystick translationJoystick = new T16000MJoystick(0);
  public final T16000MJoystick rotationJoystick = new T16000MJoystick(1);

  public final JoinedCommandJoystick secondaryPanel = new JoinedCommandJoystick(2);

  public static RobotContainer getInstance() {
    if (INSTNACE == null) {
      INSTNACE = new RobotContainer();
    }
    return INSTNACE;
  }

  private RobotContainer() {
    configureBindings("None");
  }

  /* IMPORTANT NOTE: When changing opModes, default commands and commands not bound to joystick buttons will NOT reset. If it is important that a certain command is not in effect, please take steps to within the opmode or binding */

  public void configureBindings(String opMode) {

    if (!DriverStationBackend.isJoystickConnected(0)) {
      DriverStationErrors.reportWarning(
          "Translation Joystick not connected or not assigned to port 0", false);
    }

    if (!DriverStationBackend.isJoystickConnected(1)) {
      DriverStationErrors.reportWarning(
          "Rotation Joystick not connected or not assigned to port 1", false);
    }

    if (!DriverStationBackend.isJoystickConnected(2)) {
      DriverStationErrors.reportWarning(
          "Secondart Panel not connected or not assigned to port 2", false);
    }

    translationJoystick.clearAllButtonBindings();
    rotationJoystick.clearAllButtonBindings();
    secondaryPanel.clearAllButtonBindings();

    defaultBinds();
    if (opMode.contains("Teleop")) {
      teleopBinds();
    } else if (opMode.contains("Auto")) {
      autoBinds();
    } else if (opMode.equals("Testing")) {
      testingBinds();
    }
  }

  private void teleopBinds() {
    // Should be setup for use in matches
  }

  private void autoBinds() {
    // Should remain an empty method outside of test based commands
  }

  private void testingBinds() {
    // Should be used as a way to do incremental testing of individual mechanisms
  }

  /** Always binded, no matter the active OpMode */
  private void defaultBinds() {}
}
