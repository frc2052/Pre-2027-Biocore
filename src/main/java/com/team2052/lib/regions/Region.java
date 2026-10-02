package com.team2052.lib.regions;

import java.util.function.Supplier;
import org.wpilib.command3.Trigger;
import org.wpilib.math.geometry.Translation2d;

public abstract interface Region {

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
   * Creates a trigger that activates when the point supplied by the given supplier is inside the
   * region.
   *
   * @param pointSupplier The {@link Supplier} that provides the point to check.
   * @return The trigger that activates when the point is inside the region.
   */
  public default Trigger createTrigger(Supplier<Translation2d> pointSupplier) {
    return new Trigger(() -> isPointInRegion(pointSupplier.get()));
  }
}
