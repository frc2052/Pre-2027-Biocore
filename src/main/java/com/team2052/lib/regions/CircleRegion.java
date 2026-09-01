package com.team2052.lib.regions;

import static org.wpilib.units.Units.Meters;

import org.wpilib.math.geometry.Translation2d;
import org.wpilib.units.measure.Distance;

public class CircleRegion implements Region {
  private final Translation2d center;
  private final Distance radius;

  public CircleRegion(Translation2d center, Distance radius) {
    this.center = center;
    this.radius = radius;
  }

  @Override
  public boolean isPointInRegion(Translation2d point) {
    return center.getDistance(point) <= radius.in(Meters);
  }
}
