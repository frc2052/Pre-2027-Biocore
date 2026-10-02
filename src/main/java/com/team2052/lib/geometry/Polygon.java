package com.team2052.lib.geometry;

import static org.wpilib.units.Units.Radians;

import lombok.Getter;
import lombok.Setter;
import org.wpilib.math.geometry.Translation2d;
import org.wpilib.units.measure.Angle;

public class Polygon {
  @Getter @Setter private Translation2d[] vertices;

  /**
   * Constructs a polygon with the given vertices.
   *
   * @param vertices The vertices of the polygon.
   * @throws IllegalArgumentException if the number of vertices is less than 3.
   */
  public Polygon(Translation2d... vertices) {
    if (vertices == null || vertices.length < 3)
      throw new IllegalArgumentException("At least 3 vertices are required to form a polygon");
    this.vertices = vertices;
  }

  /**
   * Checks if the given point lies inside the polygon.
   *
   * @param point The point to check for inclusion within the polygon.
   * @return True if the point lies inside the polygon, false otherwise.
   */
  public boolean isPointInPolygon(Translation2d point) {
    int n = vertices.length;
    boolean inside = false;

    for (int i = 0, j = n - 1; i < n; j = i++) {
      Translation2d pi = vertices[i];
      Translation2d pj = vertices[j];

      // Condition 1: Check if the point's Y-coordinate falls between the edge's Y bounds
      boolean intersectY = ((pi.getY() > point.getY()) != (pj.getY() > point.getY()));

      // Condition 2: Compute X-intersection point and check if the test point is to its left
      if (intersectY) {
        double xIntersection =
            (pj.getX() - pi.getX()) * (point.getY() - pi.getY()) / (pj.getY() - pi.getY())
                + pi.getX();
        if (point.getX() < xIntersection) {
          inside = !inside; // Toggle state (even/odd counter toggle)
        }
      }
    }
    return inside;
  }

  /**
   * Creates a regular polygon given the number of sides, outer radius, and the angle of the first
   * point.
   *
   * @param numSides The number of sides of the regular polygon.
   * @param outerRadius The radius from the center to the vertices of the polygon.
   * @param firstPointAngle The angle of the first point relative to the center.
   * @return A regular polygon with the specified parameters.
   * @throws IllegalArgumentException if the number of sides is less than 3.
   */
  public static Polygon regularFromOuterRadius(
      int numSides, double outerRadius, Angle firstPointAngle) {
    if (outerRadius <= 0) outerRadius = -outerRadius; // just take the absolute value
    if (numSides < 3) throw new IllegalArgumentException("Number of sides must be at least 3");

    Translation2d[] vertices = new Translation2d[numSides];
    for (int i = 0; i < numSides; i++) {
      double angle = (2 * Math.PI * i / numSides) + firstPointAngle.in(Radians);
      double x = outerRadius * Math.cos(angle);
      double y = outerRadius * Math.sin(angle);
      vertices[i] = new Translation2d(x, y);
    }
    return new Polygon(vertices);
  }

  /**
   * Creates a regular polygon given the number of sides, outer radius, the angle of the first
   * point, and the center position.
   *
   * @param numSides The number of sides of the regular polygon.
   * @param outerRadius The radius from the center to the vertices of the polygon.
   * @param firstPointAngle The angle of the first point relative to the center.
   * @param center The center position of the polygon.
   * @return A regular polygon with the specified parameters.
   * @throws IllegalArgumentException if the number of sides is less than 3.
   */
  public static Polygon regularFromOuterRadius(
      int numSides, double outerRadius, Angle firstPointAngle, Translation2d center) {
    Polygon polygon = regularFromOuterRadius(numSides, outerRadius, firstPointAngle);
    for (int i = 0; i < polygon.vertices.length; i++) {
      Translation2d vertex = polygon.vertices[i];
      polygon.vertices[i] =
          new Translation2d(vertex.getX() + center.getX(), vertex.getY() + center.getY());
    }
    return polygon;
  }

  /**
   * Creates a regular polygon given the number of sides, inner radius, and the angle of the first
   * point.
   *
   * @param numSides The number of sides of the regular polygon.
   * @param innerRadius The radius from the center to the midpoints of the sides of the polygon.
   * @param firstPointAngle The angle of the first point relative to the center.
   * @return A regular polygon with the specified parameters.
   * @throws IllegalArgumentException if the number of sides is less than 3.
   */
  public static Polygon regularFromInnerRadius(
      int numSides, double innerRadius, Angle firstPointAngle) {
    double outerRadius = innerRadius / Math.cos(Math.PI / numSides);
    return regularFromOuterRadius(numSides, outerRadius, firstPointAngle);
  }

  /**
   * Creates a regular polygon given the number of sides, inner radius, the angle of the first
   * point, and the center position.
   *
   * @param numSides The number of sides of the regular polygon.
   * @param innerRadius The radius from the center to the midpoints of the sides of the polygon.
   * @param firstPointAngle The angle of the first point relative to the center.
   * @param center The center position of the polygon.
   * @return A regular polygon with the specified parameters.
   * @throws IllegalArgumentException if the number of sides is less than 3.
   */
  public static Polygon regularFromInnerRadius(
      int numSides, double innerRadius, Angle firstPointAngle, Translation2d center) {
    double outerRadius = innerRadius / Math.cos(Math.PI / numSides);
    return regularFromOuterRadius(numSides, outerRadius, firstPointAngle, center);
  }
}
