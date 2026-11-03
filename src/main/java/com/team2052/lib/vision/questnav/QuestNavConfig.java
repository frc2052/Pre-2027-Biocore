package com.team2052.lib.vision.questnav;

import org.wpilib.math.geometry.Transform3d;

public class QuestNavConfig {

  public static final QuestNavConfig DEFAULT = new QuestNavConfig();

  public String name = "QuestNav";
  public Transform3d questPose = new Transform3d();
  public double xyCoefficient = 0.1;
  public double angleCoefficient = 20 * Math.PI / 180;
}
