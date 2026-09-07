package com.team2052.lib.subsystems;

import java.util.ArrayList;
import java.util.List;
import org.wpilib.command3.Mechanism;

/**
 * The PeridicMechanism extends {@link Mechanism} to include two periodic functions, inputPeriodic
 * and outputPeriodic.
 */
public abstract class PeriodicMechanism implements Mechanism {

  public static List<PeriodicMechanism> mechanisms = new ArrayList<>();
  protected final String name;

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
    for (PeriodicMechanism mechanism : mechanisms) {
      mechanism.inputPeriodic();
    }
  }

  /**
   * Runs all mechanism outputPeriodic functions. needs to be called in Robot Periodic OR Registered
   * as a periodic function.
   */
  public static void runAllOutputPeriodics() {
    for (PeriodicMechanism mechanism : mechanisms) {
      mechanism.outputPeriodic();
    }
  }

  /**
   * Runs all mechanism loggingPeriodic functions. needs to be called in Robot Periodic OR
   * Registered as a periodic function.
   */
  public static void runAllLoggingPeriodics() {
    for (PeriodicMechanism mechanism : mechanisms) {
      mechanism.loggingPeriodic();
    }
  }

}
