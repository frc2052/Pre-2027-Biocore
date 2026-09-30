package com.team2052.lib.util;

import static org.wpilib.units.Units.Seconds;

import lombok.Getter;
import org.wpilib.driverstation.Alliance;
import org.wpilib.driverstation.MatchState;
import org.wpilib.driverstation.MatchType;
import org.wpilib.driverstation.RobotState;
import org.wpilib.hardware.hal.AllianceStationID;
import org.wpilib.hardware.hal.RobotMode;
import org.wpilib.units.measure.Time;

public class RobotInfo {

  /**
   * @return Is the robot on the red alliance.
   */
  public static boolean isRedAlliance() {
    var alliance = MatchState.getAlliance();
    if (alliance.isEmpty()) return false;
    return alliance.get().equals(Alliance.RED);
  }

  /**
   * @return Is the robot on the blue alliance.
   */
  public static boolean isBlueAlliance() {
    return !isRedAlliance();
  }

  /**
   * Get the driverstation currently in use.
   *
   * @return The {@link AllianceStationID} of the driverstation.
   */
  public static AllianceStationID getDriverStation() {
    int id = MatchState.getLocation().orElseGet(() -> 0);
    if (id == 0) return AllianceStationID.UNKNOWN;
    if (isRedAlliance()) {
      switch (id) {
        case 1:
          return AllianceStationID.BLUE_1;
        case 2:
          return AllianceStationID.BLUE_2;
        case 3:
          return AllianceStationID.BLUE_3;
        default:
          return AllianceStationID.UNKNOWN;
      }
    } else {
      switch (id) {
        case 1:
          return AllianceStationID.RED_1;
        case 2:
          return AllianceStationID.RED_2;
        case 3:
          return AllianceStationID.RED_3;
        default:
          return AllianceStationID.UNKNOWN;
      }
    }
  }

  /**
   * Get the input game data.
   *
   * @return a clean, non optional game data where if the {@link MatchState} returns an optional,
   *     this returns an empty string.
   */
  public static String getGameData() {
    var data = MatchState.getGameData();
    if (data.isEmpty()) return "";
    return data.get();
  }

  /**
   * Checks if in an actual match via checking if connected to an FMS system.
   *
   * @return true if connected, false otherwise.
   */
  public static boolean isInMatch() {
    return RobotState.isFMSAttached();
  }

  /**
   * Is the driverstation connected.
   *
   * @return Is it connected,
   */
  public static boolean isDriverStationConnected() {
    return RobotState.isDSAttached();
  }

  /**
   * Gets the current {@link OperationState} of the robot.
   *
   * @return The {@link OperationState} of the robot.
   */
  public static OperationState getOperationState() {
    if (RobotState.isEStopped()) return OperationState.E_STOPPED;
    return OperationState.fromMode(RobotState.getRobotMode(), RobotState.isEnabled());
  }

  /**
   * Gets the name of the event. Will be an empty string if not at an event.
   *
   * @return The name of the event.
   */
  public static String getEventName() {
    return MatchState.getEventName();
  }

  /**
   * Gets the match number as shown by FMS.
   *
   * @return The match number.
   */
  public static int getMatchNumber() {
    return MatchState.getMatchNumber();
  }

  /**
   * Gets the replay number as shown by FMS.
   *
   * @return the replay number.
   */
  public static int getReplayNumber() {
    return MatchState.getReplayNumber();
  }

  /**
   * Gets the time left in this phase of the match, auto or teleop. This number is not official and
   * will count in integer steps when connected to the real field.
   *
   * @return The time left in this phase of the match.
   */
  public static Time getMatchTime() {
    return Seconds.of(MatchState.getMatchTime());
  }

  /**
   * Gets the type of match being played.
   *
   * @return The {@link MatchType} of the current match.
   */
  public static MatchType getMatchType() {
    return MatchState.getMatchType();
  }

  /**
   * Checks if the robot is enabled.
   *
   * @return True if enabled.
   */
  public static boolean isEnabled() {
    return RobotState.isEnabled();
  }

  /**
   * Checks if the robot is disabled.
   *
   * @return True if disabled.
   */
  public static boolean isDisabled() {
    return RobotState.isDisabled();
  }

  public enum OperationState {
    AUTO_DISABLED(RobotMode.AUTONOMOUS, false),
    AUTO_ENABLED(RobotMode.AUTONOMOUS, true),
    TELEOP_DISABLED(RobotMode.TELEOPERATED, false),
    TELEOP_ENABLED(RobotMode.TELEOPERATED, true),
    UTILITY_DISABLED(RobotMode.UTILITY, false),
    UTILITY_ENABLED(RobotMode.UTILITY, true),
    E_STOPPED(RobotMode.UNKNOWN, false),
    UNKNOWN(RobotMode.UNKNOWN, false);

    @Getter private final RobotMode robotMode;
    @Getter private final boolean enabled;

    private OperationState(RobotMode mode, boolean enabled) {
      this.robotMode = mode;
      this.enabled = enabled;
    }

    public static OperationState fromMode(RobotMode mode, boolean enabled) {
      switch (mode) {
        case AUTONOMOUS:
          if (enabled) {
            return OperationState.AUTO_ENABLED;
          } else {
            return OperationState.AUTO_DISABLED;
          }
        case TELEOPERATED:
          if (enabled) {
            return OperationState.TELEOP_ENABLED;
          } else {
            return OperationState.TELEOP_DISABLED;
          }
        case UTILITY:
          if (enabled) {
            return OperationState.UTILITY_ENABLED;
          } else {
            return OperationState.UTILITY_DISABLED;
          }
        case UNKNOWN:
          return OperationState.UNKNOWN;
        default:
          return OperationState.UNKNOWN;
      }
    }
  }
}
