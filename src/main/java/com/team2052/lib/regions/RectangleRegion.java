// Copyright (c) 2026 FRC Team 2052 - KnightKrawler
// https://github.com/frc2052/Pre-2027-Biocore
//
// Use of this source code is governed by an MIT-style
// license that can be found in the LICENSE file at
// the root directory of this project.

package com.team2052.lib.regions;

import lombok.Getter;
import lombok.Setter;
import org.wpilib.math.geometry.Translation2d;
import org.wpilib.math.shape.Rectangle2d;

public class RectangleRegion implements Region {
  @Getter @Setter private Rectangle2d region;

  public RectangleRegion(Rectangle2d region) {
    this.region = region;
  }

  public RectangleRegion(Translation2d cornerA, Translation2d cornerB) {
    this.region = new Rectangle2d(cornerA, cornerB);
  }

  @Override
  public boolean isPointInRegion(Translation2d point) {
    return region.contains(point);
  }
}
