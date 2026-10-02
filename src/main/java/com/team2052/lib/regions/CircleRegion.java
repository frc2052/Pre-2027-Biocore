package com.team2052.lib.regions;

import static org.wpilib.units.Units.Meters;

import lombok.Getter;
import lombok.Setter;
import org.wpilib.math.geometry.Translation2d;
import org.wpilib.units.measure.Distance;

public class CircleRegion implements Region {
  @Getter @Setter private Translation2d center;
  @Getter @Setter private Distance radius;

  public CircleRegion(Translation2d center, Distance radius) {
    this.center = center;
    this.radius = radius;
  }

  @Override
  public boolean isPointInRegion(Translation2d point) {
    return center.getDistance(point) <= radius.in(Meters);
  }
}
