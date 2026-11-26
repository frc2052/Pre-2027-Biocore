// Copyright (c) 2026 FRC Team 2052 - KnightKrawler
// https://github.com/frc2052/Pre-2027-Biocore
//
// Use of this source code is governed by an MIT-style
// license that can be found in the LICENSE file at
// the root directory of this project.

package com.team2052.lib.util;

/** An iterative boolean latch that delays the transition from false to true. */
public class DelayedBoolean {
  private boolean lastVal;
  private double transitionTimestamp;
  private final double mDelay;

  /**
   * Creates a new DelayedBoolean latch.
   *
   * @param timestamp The current timestamp in seconds.
   * @param delay The delay in seconds before the latch transitions from false to true.
   */
  public DelayedBoolean(double timestamp, double delay) {
    transitionTimestamp = timestamp;
    lastVal = false;
    mDelay = delay;
  }

  /**
   * Updates the latch with a new value and returns the current state of the latch.
   * @param timestamp The current timestamp in seconds.
   * @param value The new boolean value to update the latch with.
   * @return The current state of the latch after the update.
   */
  public boolean update(double timestamp, boolean value) {
    boolean result = false;

    if (value && !lastVal) {
      transitionTimestamp = timestamp;
    }

    // If we are still true and we have transitioned.
    if (value && (timestamp - transitionTimestamp > mDelay)) {
      result = true;
    }

    lastVal = value;
    return result;
  }
}
