// Copyright (c) 2026 FRC Team 2052 - KnightKrawler
// https://github.com/frc2052/Pre-2027-Biocore
//
// Use of this source code is governed by an MIT-style
// license that can be found in the LICENSE file at
// the root directory of this project.

package com.team2052.lib.vision.questnav;

import org.wpilib.math.geometry.Transform3d;

public class QuestNavConfig {

  public static final QuestNavConfig DEFAULT = new QuestNavConfig();

  public String name = "QuestNav";
  public Transform3d questPose = new Transform3d();
  public double xyCoefficient = 0.1;
  public double angleCoefficient = 20 * Math.PI / 180;
}
