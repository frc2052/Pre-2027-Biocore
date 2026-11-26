// Copyright (c) 2026 FRC Team 2052 - KnightKrawler
// https://github.com/frc2052/Pre-2027-Biocore
//
// Use of this source code is governed by an MIT-style
// license that can be found in the LICENSE file at
// the root directory of this project.

package com.team2052.lib.util;

public class PIDConfiguration {

  public final double kP;
  public final double kI;
  public final double kD;

  public final double maxAcc;
  public final double maxVel;

  public PIDConfiguration(double kP, double kI, double kD, double maxVel, double maxAcc) {
    this.kP = kP;
    this.kI = kI;
    this.kD = kD;
    this.maxVel = maxVel;
    this.maxAcc = maxAcc;
  }
}
