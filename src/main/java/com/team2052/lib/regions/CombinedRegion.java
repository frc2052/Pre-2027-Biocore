package com.team2052.lib.regions;

import lombok.Getter;
import lombok.Setter;
import org.wpilib.math.geometry.Translation2d;

public class CombinedRegion implements Region {
  @Getter @Setter private Region[] regions;

  public CombinedRegion(Region... regions) {
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
      if (region.isPointInRegion(point)) {
        return true;
      }
    }
    return false;
  }
}
