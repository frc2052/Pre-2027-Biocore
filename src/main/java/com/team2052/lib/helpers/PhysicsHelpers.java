package com.team2052.lib.helpers;

import static org.wpilib.units.Units.*;

import com.team2052.lib.geometry.Vector2d;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.geometry.Translation2d;
import org.wpilib.units.measure.*;

/**
 * PhysicsHelpers is a class that helps define and calculate physical relationships
 *
 * <p>## Definitions - <b>Angular Velocity</b>: The velocity of a point rotating around another. -
 * <b>Moving Point</b>: A point in space that is moving independently of its origin. - <b>Rotational
 * Offset</b>: A point in space that is rotating around its origin. - <b>Velocity</b>: The speed of
 * some point in space with a direction.
 */
public class PhysicsHelpers {

  // Constants

  /** The downward acceleration of gravity. Is 9.81 m/s/s */
  public static final LinearAcceleration g = MetersPerSecondPerSecond.of(9.81);

  // Rotational

  /**
   * Returns the velocity vector of the moving point in terms of its radial and tangential
   * components relative to the center of rotation, given the velocity of the point.
   *
   * @param movingPoint The position of the point relative to the center of rotation.
   * @param velocity The velocity vector of the point relative to the center of rotation.
   * @return The velocity vector of the moving point in terms of its radial and tangential
   *     components relative to the center of rotation.
   */
  public static Vector2d getAsToRadialAndTangential(Translation2d movingPoint, Vector2d velocity) {
    if (movingPoint.getAngle().isEmpty()) return new Vector2d(0, 0);

    Vector2d tangentialUnitVector =
        Vector2d.getUnitVectorFromRotation(movingPoint.getAngle().get())
            .rotateBy(Rotation2d.CW_PI_2);
    Vector2d radialUnitVector = tangentialUnitVector.rotateBy(Rotation2d.CW_PI_2);

    double tangentialVelocity = velocity.getProjectionOnto(tangentialUnitVector).getNorm();
    double radialVelocity = velocity.getProjectionOnto(radialUnitVector).getNorm();

    return new Vector2d(tangentialVelocity, radialVelocity);
  }

  // Tangential Velocity

  /**
   * Returns the tangential velocity vector of a point at a given rotational offset from the center
   * of rotation, given the angular velocity of the rotation.
   *
   * @param rotationalOffset The offset of the point from the center of rotation.
   * @param angularVelocity The angular velocity of the rotation.
   * @return The tangential velocity vector.
   */
  public static Vector2d getTangentialVelocity(
      Translation2d rotationalOffset, AngularVelocity angularVelocity) {
    if (rotationalOffset.getAngle().isEmpty()) return new Vector2d(0, 0);

    return Vector2d.getUnitVectorFromRotation(rotationalOffset.getAngle().get())
        .rotateBy(new Rotation2d(Math.copySign(Math.PI / 2, angularVelocity.in(RadiansPerSecond))))
        .times(rotationalOffset.getNorm())
        .times(angularVelocity.in(RadiansPerSecond));
  }

  /**
   * Returns the tangential velocity vector of a point at a given position, given the linear
   * velocity of the point.
   *
   * @param movingPoint The position of the point relative to the center of rotation.
   * @param velocity The velocity vector of the point relative to the center of rotation.
   * @return The tangential velocity vector.
   */
  public static Vector2d getTangentialVelocity(Translation2d movingPoint, Vector2d velocity) {
    if (movingPoint.getAngle().isEmpty()) return new Vector2d(0, 0);

    Vector2d tangentialUnitVector =
        Vector2d.getUnitVectorFromRotation(movingPoint.getAngle().get())
            .rotateBy(Rotation2d.CW_PI_2);
    return velocity.getProjectionOnto(tangentialUnitVector);
  }

  /**
   * Returns the linear tangential velocity of a point at a given rotational offset from the center
   * of rotation, given the angular velocity of the rotation.
   *
   * @param rotationalOffset The offset of the point from the center of rotation.
   * @param angularVelocity The angular velocity of the rotation.
   * @return The linear tangential velocity.
   */
  public static LinearVelocity getLinearTangentialVelocity(
      Translation2d rotationalOffset, AngularVelocity angularVelocity) {
    return MetersPerSecond.of(rotationalOffset.getNorm() * angularVelocity.in(RadiansPerSecond));
  }

  /**
   * Returns the linear tangential velocity of a point at a given position, given the angular
   * velocity of the rotation.
   *
   * @param movingPoint The position of the point relative to the center of rotation.
   * @param velocity The velocity of the point relative to the center of rotation.
   * @return The linear tangential velocity.
   */
  public static LinearVelocity getLinearTangentialVelocity(
      Translation2d movingPoint, Vector2d velocity) {
    return MetersPerSecond.of(getTangentialVelocity(movingPoint, velocity).getNorm());
  }

  // Radial Velocity

  /**
   * Returns the radial velocity vector of a point at a given position, given the linear velocity of
   * the point.
   *
   * @param movingPoint The position of the point relative to the center of rotation.
   * @param velocity The velocity vector of the point relative to the center of rotation.
   * @return The radial velocity vector.
   */
  public static Vector2d getRadialVelocity(Translation2d movingPoint, Vector2d velocity) {
    if (movingPoint.getAngle().isEmpty()) return new Vector2d(0, 0);

    Vector2d radialUnitVector = Vector2d.getUnitVectorFromRotation(movingPoint.getAngle().get());
    return velocity.getProjectionOnto(radialUnitVector);
  }

  /**
   * Returns the radial velocity vector of a point at a given rotational offset from the center of
   * rotation, given the angular velocity of the rotation.
   *
   * @param rotationalOffset The offset of the point from the center of rotation.
   * @param angularVelocity The angular velocity of the rotation.
   * @return The radial velocity vector.
   */
  public static Vector2d getRadialVelocity(
      Translation2d rotationalOffset, AngularVelocity angularVelocity) {
    if (rotationalOffset.getAngle().isEmpty()) return new Vector2d(0, 0);

    return Vector2d.getUnitVectorFromRotation(rotationalOffset.getAngle().get())
        .times(rotationalOffset.getNorm())
        .times(angularVelocity.in(RadiansPerSecond));
  }

  /**
   * Returns the linear velocity of a point at a given rotational offset from the center of
   * rotation, given the velocity of the point relative to the center of rotation.
   *
   * @param movingPoint The position of the point relative to the center of rotation.
   * @param velocity The velocity vector of the point relative to the center of rotation.
   * @return The linear radial velocity.
   */
  public static LinearVelocity getLinearRadialVelocity(
      Translation2d movingPoint, Vector2d velocity) {
    return MetersPerSecond.of(getRadialVelocity(movingPoint, velocity).getNorm());
  }

  /**
   * Returns the linear velocity of a point at a given rotational offset from the center of
   * rotation, given the angular velocity of the rotation.
   *
   * @param rotationalOffset The offset of the point from the center of rotation.
   * @param angularVelocity The angular velocity of the rotation.
   * @return The linear radial velocity.
   */
  public static LinearVelocity getLinearRadialVelocity(
      Translation2d rotationalOffset, AngularVelocity angularVelocity) {
    return MetersPerSecond.of(rotationalOffset.getNorm() * angularVelocity.in(RadiansPerSecond));
  }

  // Angular Velocity

  /**
   * Returns the angular velocity of a point at a given rotational offset from the center of
   * rotation, given the velocity of the point relative to the center of rotation.
   *
   * @param rotationalOffset The offset of the point from the center of rotation.
   * @param velocity The velocity vector of the point relative to the center of rotation.
   * @return
   */
  public static AngularVelocity getAngularVelocity(
      Translation2d rotationalOffset, Vector2d velocity) {
    if (rotationalOffset.getNorm() == 0) return RadiansPerSecond.of(0);

    return RadiansPerSecond.of(
        getTangentialVelocity(rotationalOffset, velocity).getNorm() / rotationalOffset.getNorm());
  }

  /**
   * Returns the angular velocity of a point at a given rotational offset from the center of
   * rotation, given the linear tangential velocity of the point.
   *
   * @param rotationalOffset The offset of the point from the center of rotation.
   * @param linearTangentialVelocity The linear tangential velocity of the point.
   * @return The angular velocity of the point.
   */
  public static AngularVelocity getAngularVelocity(
      Translation2d rotationalOffset, LinearVelocity linearTangentialVelocity) {
    if (rotationalOffset.getNorm() == 0) return RadiansPerSecond.of(0);

    return RadiansPerSecond.of(
        linearTangentialVelocity.in(MetersPerSecond) / rotationalOffset.getNorm());
  }
}
