// Copyright (c) 2026 FRC Team 2052 - KnightKrawler
// https://github.com/frc2052/Pre-2027-Biocore
//
// Use of this source code is governed by an MIT-style
// license that can be found in the LICENSE file at
// the root directory of this project.

package com.team2052.lib.regions;

import com.team2052.lib.geometry.Polygon;
import lombok.Getter;
import org.wpilib.math.geometry.Translation2d;

public class PolygonRegion implements Region {
  @Getter private Polygon polygon;

  public PolygonRegion(Translation2d... vertices) {
    this.polygon = new Polygon(vertices);
  }

  public PolygonRegion(Polygon polygon) {
    this.polygon = polygon;
  }

  @Override
  public boolean isPointInRegion(Translation2d point) {
    return polygon.isPointInPolygon(point);
  }
}
