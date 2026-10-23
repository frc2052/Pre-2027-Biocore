package com.team2052.lib.helpers;

import static org.wpilib.units.Units.MetersPerSecond;
import static org.wpilib.units.Units.RadiansPerSecond;

import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.geometry.Translation2d;
import org.wpilib.units.measure.AngularVelocity;
import org.wpilib.units.measure.LinearVelocity;

import com.team2052.lib.geometry.Vector2d;

public class PhysicsHelpers {
    
    // Tangential Velocity

    /**
     * Returns the tangential velocity vector of a point at a given rotational offset from the center of rotation, given the angular velocity of the rotation.
     * @param rotationalOffset The offset of the point from the center of rotation.
     * @param angularVelocity The angular velocity of the rotation.
     * @return The tangential velocity vector.
     */
    public static Vector2d getTangentialVelocity(Translation2d rotationalOffset, AngularVelocity angularVelocity) {
        if (rotationalOffset.getAngle().isEmpty()) return new Vector2d(0, 0);

        return Vector2d.getUnitVectorFromRotation(rotationalOffset.getAngle().get())
            .rotateBy(new Rotation2d(Math.copySign(Math.PI / 2, angularVelocity.in(RadiansPerSecond))))
            .times(rotationalOffset.getNorm())
            .times(angularVelocity.in(RadiansPerSecond));
    }

    /**
     * Returns the tangential velocity vector of a point at a given position, given the linear velocity of the point.
     * @param movingPoint The position of the point relative to the center of rotation.
     * @param velocity The velocity vector of the point relative to the center of rotation.
     * @return The tangential velocity vector.
     */
    public static Vector2d getTangentialVelocity(Translation2d movingPoint, Vector2d velocity) {
        if (movingPoint.getAngle().isEmpty())
            return new Vector2d(0, 0);

        Vector2d unitVector = Vector2d.getUnitVectorFromRotation(movingPoint.getAngle().get())
                .rotateBy(Rotation2d.CW_PI_2);
        return velocity.getProjectionOnto(unitVector);
    }

    /**
     * Returns the linear tangential velocity of a point at a given rotational offset from the center of rotation, given the angular velocity of the rotation.
     * @param rotationalOffset The offset of the point from the center of rotation.
     * @param angularVelocity The angular velocity of the rotation.
     * @return The linear tangential velocity.
     */
    public static LinearVelocity getLinearTangentialVelocity(Translation2d rotationalOffset,
            AngularVelocity angularVelocity) {
        return MetersPerSecond.of(rotationalOffset.getNorm() * angularVelocity.in(RadiansPerSecond));
    }

    /**
     * Returns the linear tangential velocity of a point at a given position, given the angular velocity of the rotation.
     * @param movingPoint The position of the point relative to the center of rotation.
     * @param velocity The velocity of the point relative to the center of rotation.
     * @return The linear tangential velocity.
     */
    public static LinearVelocity getLinearTangentialVelocity(Translation2d movingPoint, Vector2d velocity) {
        return MetersPerSecond.of(getTangentialVelocity(movingPoint, velocity).getNorm());
    }

    // Radial Velocity

    /**
     * Returns the radial velocity vector of a point at a given position, given the linear velocity of the point.
     * @param movingPoint The position of the point relative to the center of rotation.
     * @param velocity The velocity vector of the point relative to the center of rotation.
     * @return The radial velocity vector.
     */
    public static Vector2d getRadialVelocity(Translation2d movingPoint, Vector2d velocity) {
        if (movingPoint.getAngle().isEmpty()) return new Vector2d(0, 0);

        Vector2d unitVector = Vector2d.getUnitVectorFromRotation(movingPoint.getAngle().get());
        return velocity.getProjectionOnto(unitVector);
    }

    /**
     * Returns the radial velocity vector of a point at a given rotational offset from the center of rotation, given the angular velocity of the rotation.
     * @param rotationalOffset The offset of the point from the center of rotation.
     * @param angularVelocity The angular velocity of the rotation.
     * @return The radial velocity vector.
     */
    public static Vector2d getRadialVelocity(Translation2d rotationalOffset, AngularVelocity angularVelocity) {
        if (rotationalOffset.getAngle().isEmpty()) return new Vector2d(0, 0);

        return Vector2d.getUnitVectorFromRotation(rotationalOffset.getAngle().get())
            .times(rotationalOffset.getNorm())
            .times(angularVelocity.in(RadiansPerSecond));
    }

    /**
     * Returns the linear velocity of a point at a given rotational offset from the
     * center of rotation, given the velocity of the point relative to the center of rotation.
     * 
     * @param movingPoint The position of the point relative to the center of rotation.
     * @param velocity The velocity vector of the point relative to the center of rotation.
     * @return The linear radial velocity.
     */
    public static LinearVelocity getLinearRadialVelocity(Translation2d movingPoint, Vector2d velocity) {
        return MetersPerSecond.of(getRadialVelocity(movingPoint, velocity).getNorm());
    }

    /**
     * Returns the linear velocity of a point at a given rotational offset from the center of rotation, given the angular velocity of the rotation.
     * @param rotationalOffset The offset of the point from the center of rotation.
     * @param angularVelocity The angular velocity of the rotation.
     * @return The linear radial velocity.
     */
    public static LinearVelocity getLinearRadialVelocity(Translation2d rotationalOffset, AngularVelocity angularVelocity) {
        return MetersPerSecond.of(rotationalOffset.getNorm() * angularVelocity.in(RadiansPerSecond));
    }
}
