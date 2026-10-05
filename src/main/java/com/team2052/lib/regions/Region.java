package com.team2052.lib.regions;

import java.util.function.Supplier;
import org.wpilib.command3.Trigger;
import org.wpilib.math.geometry.Translation2d;

public abstract interface Region {

  /**
   * Checks if the given point is inside the region.
   *
   * @param point The point to check.
   * @return True if the point is inside the region, false otherwise.
   */
  public abstract boolean isPointInRegion(Translation2d point);

  /**
   * Returns true if the point is outside the region.
   *
   * @param point The point to check.
   * @return True if the point is outside the region.
   */
  public default boolean isPointOutsideRegion(Translation2d point) {
    return !isPointInRegion(point);
  }

  /**
   * Gets an inverse of this region where all points inside become outside and vice versa.
   *
   * @return The inverse region.
   */
  public default Region inverse() {
    return point -> isPointOutsideRegion(point);
  }

  /**
   * Creates a new region that represents the union of this region and the given regions.
   *
   * @param regions to union with this region.
   * @return The new region representing the union of this region and the given regions.
   */
  public default Region unionWith(Region... regions) {
    return new CombinedRegion(regions);
  }

  /**
   * Creates a new region that represents the intersection of this region and the given regions.
   *
   * @param regions to intersect with this region.
   * @return The new region representing the intersection of this region and the given regions.
   */
  public default Region intersectionWith(Region... regions) {
    return new OverlappingRegion(regions);
  }

  /**
   * Creates a new region that represents the difference of this region and the given regions.
   *
   * @param regions to subtract from this region.
   * @return The new region representing the difference of this region and the given regions.
   */
  public default Region differenceWith(Region... regions) {
    if (regions.length == 0) {
      return this;
    }

    Region result = this;

    for (int i = 0; i < regions.length; i++) {
      OverlappingRegion overlap = new OverlappingRegion(result, regions[i]);
      CombinedRegion combine = new CombinedRegion(result, regions[i]);
      result = new OverlappingRegion(overlap.inverse(), combine);
    }

    return result;
  }

  /**
   * Creates a trigger that activates when the point supplied by the given supplier is inside the
   * region.
   *
   * @param pointSupplier The {@link Supplier} that provides the point to check.
   * @return The trigger that activates when the point is inside the region.
   */
  public default Trigger createTrigger(Supplier<Translation2d> pointSupplier) {
    return new Trigger(() -> isPointInRegion(pointSupplier.get()));
  }

  /**
   * Gets the inverse of the given region.
   *
   * @param region The region to get the inverse of.
   * @return The inverse of the given region.
   */
  public static Region inverse(Region region) {
    return region.inverse();
  }

  /**
   * Gets the union of the given regions. This region will contain all points that are inside at
   * least one of the given regions.
   *
   * @param regions The regions to union.
   * @return The union of the given regions.
   */
  public static Region union(Region... regions) {
    return new CombinedRegion(regions);
  }

  /**
   * Gets the intersection of the given regions. This region will contain all points that are inside
   * all of the given regions.
   *
   * @param regions The regions to intersect.
   * @return The intersection of the given regions.
   */
  public static Region intersection(Region... regions) {
    return new OverlappingRegion(regions);
  }

  /**
   * Gets the difference of the given regions. This region will contain all points that are inside
   * at only one of the given regions.
   *
   * @param regions to get the difference of.
   * @return The difference of the given regions.
   */
  public static Region difference(Region... regions) {
    if (regions.length == 0) {
      return point -> false;
    }

    Region result = regions[0];

    for (int i = 1; i < regions.length; i++) {
      OverlappingRegion overlap = new OverlappingRegion(result, regions[i]);
      CombinedRegion combine = new CombinedRegion(result, regions[i]);
      result = new OverlappingRegion(overlap.inverse(), combine);
    }

    return result;
  }
}
