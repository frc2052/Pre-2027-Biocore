package com.team2052.lib.subsystems;

import java.util.ArrayList;
import java.util.List;
import org.wpilib.command3.Mechanism;

/**
 * The PeridicMechanism extends {@link Mechanism} to include two periodic functions, inputPeriodic
 * and outputPeriodic.
 */
public abstract class PeriodicMechanism extends Mechanism {

  public static List<PeriodicMechanism> mechanisms = new ArrayList<>();

  public PeriodicMechanism(String name) {
    super(name);
    mechanisms.add(this);
  }

  public abstract void inputPeriodic();

  public abstract void outputPeriodic();

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
}
