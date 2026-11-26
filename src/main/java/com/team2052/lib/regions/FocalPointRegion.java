package com.team2052.lib.regions;

import static org.wpilib.units.Units.Meters;

import java.util.ArrayList;
import java.util.List;

import org.wpilib.math.geometry.Translation2d;
import org.wpilib.units.measure.Distance;
import org.wpilib.util.Pair;

public class FocalPointRegion implements Region {

    private List<Pair<Translation2d, Double>> focalPoints = new ArrayList<>();
    private Distance radius = Meters.of(0);

    public FocalPointRegion(Distance radius) {
        this.radius = radius;
    }

    public FocalPointRegion(Distance radius, List<Pair<Translation2d, Double>> focalPoints) {
        this.radius = radius;
        this.focalPoints = focalPoints;
    }

    public void addFocalPoint(Translation2d point, double weight) {
        focalPoints.add(new Pair<>(point, weight));
    }

    public void addFocalPoint(Translation2d point) {
        addFocalPoint(point, 1.0);
    }

    public void addFocalPoints(List<Pair<Translation2d, Double>> points) {
        focalPoints.addAll(points);
    }


    @Override
    public boolean isPointInRegion(Translation2d point) {
        double total = 0;

        for (Pair<Translation2d, Double> focalPoint : focalPoints) {
            double distance = point.getDistance(focalPoint.getFirst());
            total += focalPoint.getSecond() * distance;
        }

        return total <= radius.in(Meters);
    }

}
