package com.team2052.lib.geometry;

import java.util.function.Function;

import org.wpilib.math.geometry.Translation2d;

public interface ParametricFunction extends Function<Double, Translation2d>{
    
    @Override
    public Translation2d apply(Double t);

    /**
     * Get the x-coordinate of the parametric function at the given parameter value.
     * @param t The parameter value for which to calculate the x-coordinate.
     * @return The x-coordinate of the parametric function at the given parameter value.
     */
    public default double getX(double t) {
        return apply(t).getX();
    }

    /**
     * Get the y-coordinate of the parametric function at the given parameter value.
     * @param t The parameter value for which to calculate the y-coordinate.
     * @return The y-coordinate of the parametric function at the given parameter value.
     */
    public default double getY(double t) {
        return apply(t).getY();
    }
}
