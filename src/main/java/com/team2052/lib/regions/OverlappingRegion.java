package com.team2052.lib.regions;

import org.wpilib.math.geometry.Translation2d;

import lombok.Getter;
import lombok.Setter;

public class OverlappingRegion implements Region {
  @Getter @Setter private Region[] regions;

  public OverlappingRegion(Region... regions) {
    this.regions = regions;
  }

  public void addRegion(Region region) {
    Region[] newRegions = new Region[regions.length + 1];
    System.arraycopy(regions, 0, newRegions, 0, regions.length);
    newRegions[regions.length] = region;
    regions = newRegions;
  }

  @Override
  public boolean isPointInRegion(Translation2d point) {
    for (Region region : regions) {
      if (region.isPointOutsideRegion(point)) {
        return false;
      }
    }
    return true;
  }
}
