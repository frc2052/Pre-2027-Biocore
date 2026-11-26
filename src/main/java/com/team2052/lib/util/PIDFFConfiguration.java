// Copyright (c) 2026 FRC Team 2052 - KnightKrawler
// https://github.com/frc2052/Pre-2027-Biocore
//
// Use of this source code is governed by an MIT-style
// license that can be found in the LICENSE file at
// the root directory of this project.

package com.team2052.lib.util;

public class PIDFFConfiguration {

  public final PIDConfiguration pid;
  public final FeedForwardConfiguration ff;

  public PIDFFConfiguration(PIDConfiguration pid, FeedForwardConfiguration ff) {
    this.pid = pid;
    this.ff = ff;
  }
}
