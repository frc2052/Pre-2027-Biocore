package com.team2052.lib.mechanisms;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.littletonrobotics.junction.Logger;
import org.wpilib.command3.Mechanism;
import org.wpilib.system.Timer;

/**
 * The PeriodicMechanism implements {@link Mechanism} to include three periodic functions,
 * inputPeriodic, loggingPeriodic, and outputPeriodic.
 */
public abstract class PeriodicMechanism implements Mechanism {

  public static List<PeriodicMechanism> mechanisms = new ArrayList<>();
  protected final String name;
  @Getter protected boolean logTiming = false;

  public PeriodicMechanism(String name) {
    this.name = name;
    mechanisms.add(this);
  }

  @Override
  public String getName() {
    return name;
  }

  /* Please note that the recommended (but not required) order is: input, log, output */

  public abstract void inputPeriodic();

  public abstract void outputPeriodic();

  public abstract void loggingPeriodic();

  /**
   * Runs all mechanism inputPeriodic functions. needs to be called in Robot Periodic OR Registered
   * as a periodic function.
   */
  public static void runAllInputPeriodics() {
    double inputStartTime = Timer.getMonotonicTimestamp();
    for (PeriodicMechanism mechanism : mechanisms) {
      double startTime = Timer.getMonotonicTimestamp();
      mechanism.inputPeriodic();
      double endTime = Timer.getMonotonicTimestamp();
      if (mechanism.isLogTiming()) {
        Logger.recordOutput(
            "Mechanisms/Periodic/" + mechanism.getName() + "/Input Time (ms)",
            (endTime - startTime) * 1000);
      }
    }
    double inputEndTie = Timer.getMonotonicTimestamp();
    Logger.recordOutput(
        "Mechanisms/Periodic/Input Time (ms)", (inputEndTie - inputStartTime) * 1000);
  }

  /**
   * Runs all mechanism outputPeriodic functions. needs to be called in Robot Periodic OR Registered
   * as a periodic function.
   */
  public static void runAllOutputPeriodics() {
    double outputStartTime = Timer.getMonotonicTimestamp();
    for (PeriodicMechanism mechanism : mechanisms) {
      double startTime = Timer.getMonotonicTimestamp();
      mechanism.outputPeriodic();
      double endTime = Timer.getMonotonicTimestamp();
      if (mechanism.isLogTiming()) {
        Logger.recordOutput(
            "Mechanisms/Periodic/" + mechanism.getName() + "/Output Time (ms)",
            (endTime - startTime) * 1000);
      }
    }
    double outputEndTime = Timer.getMonotonicTimestamp();
    Logger.recordOutput(
        "Mechanisms/Periodic/Output Time (ms)", (outputEndTime - outputStartTime) * 1000);
  }

  /**
   * Runs all mechanism loggingPeriodic functions. needs to be called in Robot Periodic OR
   * Registered as a periodic function.
   */
  public static void runAllLoggingPeriodics() {
    double loggingStartTime = Timer.getMonotonicTimestamp();
    for (PeriodicMechanism mechanism : mechanisms) {
      double startTime = Timer.getMonotonicTimestamp();
      mechanism.loggingPeriodic();
      double endTime = Timer.getMonotonicTimestamp();
      if (mechanism.isLogTiming()) {
        Logger.recordOutput(
            "Mechanisms/Periodic/" + mechanism.getName() + "/Logging Time (ms)",
            (endTime - startTime) * 1000);
      }
    }
    double loggingEndTime = Timer.getMonotonicTimestamp();
    Logger.recordOutput(
        "Mechanisms/Periodic/Logging Time (ms)", (loggingEndTime - loggingStartTime) * 1000);
  }
}
