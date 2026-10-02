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
