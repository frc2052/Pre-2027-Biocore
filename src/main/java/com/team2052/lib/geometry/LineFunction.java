package com.team2052.lib.geometry;

import java.util.function.Function;

import org.wpilib.math.geometry.Translation2d;

import lombok.Getter;
import lombok.Setter;

public class LineFunction implements Function<Double, Double> {
    @Getter @Setter private double slope;
    @Getter @Setter private double yIntercept;

    public LineFunction(double slope, double yIntercept) {
        this.slope = slope;
        this.yIntercept = yIntercept;
    }

    public LineFunction(Translation2d pointOne, Translation2d pointTwo) {
        if (pointTwo.getX() == pointOne.getX()) throw new IllegalArgumentException("Points must have different x-coordinates to define a line.");
        this.slope = (pointTwo.getY() - pointOne.getY()) / (pointTwo.getX() - pointOne.getX());
        this.yIntercept = pointOne.getY() - slope * pointOne.getX();
    }

    @Override
    public Double apply(Double x) {
        return getY(x);
    }

    /**
     * Returns the y-coordinate of the line for the given x-coordinate.
     * @param x The x-coordinate for which to calculate the y-coordinate.
     * @return The y-coordinate of the line at the given x-coordinate.
     */
    public double getY(double x) {
        return slope * x + yIntercept;
    }

    /**
     * Returns the x-coordinate of the line for the given y-coordinate.
     * @param y The y-coordinate for which to calculate the x-coordinate.
     * @return The x-coordinate of the line at the given y-coordinate.
     */
    public double getX(double y) {
        return (y - yIntercept) / slope;
    }

    /**
     * Returns the inverse of the line function, effectively swapping the x and y axes.
     * @return A new LineFunction representing the inverse of the current line function.
     */
    public LineFunction inverse() {
        return new LineFunction(1 / slope, -yIntercept / slope);
    }

}
