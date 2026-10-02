package com.team2052.lib.geometry;

import org.wpilib.math.geometry.Translation2d;

import lombok.Getter;
import lombok.Setter;

public class LineSegment {
    
    @Getter @Setter private Translation2d start;
    @Getter @Setter private Translation2d end;

    public LineSegment(Translation2d start, Translation2d end) {
        this.start = start;
        this.end = end;
    }

    public LineSegment() {
        this.start = new Translation2d();
        this.end = new Translation2d();
    }

    /**
     * Checks if the given point lies on this line segment.
     * @param point The point to check.
     * @return True if the point lies on the line segment, false otherwise.
     */
    public boolean isPointOnLine(Translation2d point) {
        // Check collinearity using the cross product of vector AB and vector AP
        // Vector AB = (x2 - x1, y2 - y1)
        // Vector AP = (px - x1, py - y1)
        double crossProduct = (end.getX() - start.getX()) * (point.getY() - start.getY()) - (end.getY() - start.getY()) * (point.getX() - start.getX());
        if (crossProduct != 0) {
            return false;
        }

        // Check if the point lies within the bounding box of the segment
        boolean withinX = point.getX() >= Math.min(start.getX(), end.getX()) && point.getX() <= Math.max(start.getX(), end.getX());
        boolean withinY = point.getY() >= Math.min(start.getY(), end.getY()) && point.getY() <= Math.max(start.getY(), end.getY());
        return withinX && withinY;
    }

    /**
     * Checks if the given point lies on this line segment within a specified epsilon tolerance.
     * @param point The point to check.
     * @param epsilon The tolerance for considering the point as lying on the line segment.
     * @return True if the point lies on the line segment within the epsilon tolerance, false otherwise.
     */
    public boolean epsilonIsPointOnLine(Translation2d point, double epsilon) {
        // Check collinearity using the cross product of vector AB and vector AP
        double crossProduct = (end.getX() - start.getX()) * (point.getY() - start.getY()) - (end.getY() - start.getY()) * (point.getX() - start.getX());
        if (Math.abs(crossProduct) > epsilon) {
            return false;
        }

        // Check if the point lies within the bounding box of the segment
        boolean withinX = point.getX() >= Math.min(start.getX(), end.getX()) - epsilon && point.getX() <= Math.max(start.getX(), end.getX()) + epsilon;
        boolean withinY = point.getY() >= Math.min(start.getY(), end.getY()) - epsilon && point.getY() <= Math.max(start.getY(), end.getY()) + epsilon;
        return withinX && withinY;
    }

    /**
     * Gets the cross product of this line segment and another line segment.
     * @param other The other line segment to compute the cross product with.
     * @return The cross product of the two line segments.
     */
    public double getCrossProduct(LineSegment other) {
        return (end.getX() - start.getX()) * (other.end.getY() - other.start.getY()) - (end.getY() - start.getY()) * (other.end.getX() - other.start.getX());
    }

    /**
     * Gets the dot product of this line segment and another line segment.
     * @param other The other line segment to compute the dot product with.
     * @return The dot product of the two line segments.
     */
    public double getDotProduct(LineSegment other) {
        return (end.getX() - start.getX()) * (other.end.getX() - other.start.getX()) + (end.getY() - start.getY()) * (other.end.getY() - other.start.getY());
    }

    /**
     * Gets the length of the line segment.
     * @return The length of the line segment.
     */
    public double getLength() {
        return start.getDistance(end);
    }

    /**
     * Gets the midpoint of the line segment.
     * @return The midpoint of the line segment.
     */
    public Translation2d getMidpoint() {
        return new Translation2d((start.getX() + end.getX()) / 2, (start.getY() + end.getY()) / 2);
    }
}
