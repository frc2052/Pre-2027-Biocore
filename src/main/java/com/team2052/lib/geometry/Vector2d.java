package com.team2052.lib.geometry;

import static org.wpilib.units.Units.Meters;
import static org.wpilib.units.Units.Radians;
import static org.wpilib.units.Units.RadiansPerSecond;
import static org.wpilib.units.Units.RadiansPerSecondPerSecond;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.geometry.Transform2d;
import org.wpilib.math.geometry.Translation2d;
import org.wpilib.math.interpolation.Interpolatable;
import org.wpilib.math.kinematics.ChassisAccelerations;
import org.wpilib.math.kinematics.ChassisVelocities;
import org.wpilib.math.linalg.VecBuilder;
import org.wpilib.math.linalg.Vector;
import org.wpilib.math.numbers.N2;
import org.wpilib.math.util.MathUtil;
import org.wpilib.units.measure.Angle;
import org.wpilib.units.measure.AngularAcceleration;
import org.wpilib.units.measure.AngularVelocity;
import org.wpilib.units.measure.Distance;
import org.wpilib.util.protobuf.ProtobufSerializable;
import org.wpilib.util.struct.StructSerializable;

/**
 * A 2D vector in the plane.
 *
 * <p>DISCLAIMER: This class is a wrapper around Translation2d (That class is final and cannot be
 * extended which is why this class is mostly contains a copy of the Translation2d class), and is
 * not intended to be used in place of Translation2d. It is intended to be used as a vector in the
 * plane, and provides additional functionality for vector operations.
 */
public final class Vector2d
    implements Interpolatable<Vector2d>, ProtobufSerializable, StructSerializable {

  public static final Vector2d ZERO = new Vector2d(0, 0);
  public static final Vector2d UNIT_X = new Vector2d(1, 0);
  public static final Vector2d UNIT_Y = new Vector2d(0, 1);

  private Translation2d vector;

  /** Creates a new Vector2d with zero components. */
  public Vector2d() {
    this.vector = new Translation2d();
  }

  /**
   * Creates a new Vector2d from a Translation2d.
   *
   * @param vector The translation vector to initialize with.
   */
  public Vector2d(Translation2d vector) {
    this.vector = vector;
  }

  /**
   * Creates a new Vector2d from x and y components.
   *
   * @param x The x component of the vector.
   * @param y The y component of the vector.
   */
  public Vector2d(double x, double y) {
    this.vector = new Translation2d(x, y);
  }

  /**
   * Creates a new Vector2d from x and y components in a measure.
   *
   * @param x The x component of the vector in a measure.
   * @param y The y component of the vector in a measure.
   */
  public Vector2d(Distance x, Distance y) {
    this.vector = new Translation2d(x, y);
  }

  /**
   * Creates a new Vector2d from a Vector.
   *
   * @param vector The vector to initialize with.
   */
  public Vector2d(Vector<N2> vector) {
    this.vector = new Translation2d(vector.get(0, 0), vector.get(1, 0));
  }

  /**
   * Creates a new Vector2d from an angle and magnitude.
   *
   * @param theta The angle of the vector.
   * @param magnitude The magnitude of the vector.
   * @return A new Vector2d with the given angle and magnitude.
   */
  public Vector2d(Angle theta, double magnitude) {
    this.vector = new Translation2d(magnitude, new Rotation2d(theta.in(Radians)));
  }

  /**
   * Creates a new Vector2d from a rotation and magnitude.
   *
   * @param rotation The rotation of the vector.
   * @param magnitude The magnitude of the vector.
   * @return A new Vector2d with the given rotation and magnitude.
   */
  public Vector2d(Rotation2d rotation, double magnitude) {
    this.vector = new Translation2d(magnitude, rotation);
  }

  /**
   * Creates a new Vector2d from a {@link ChassisVelocities} object.
   *
   * @param chassisVelocities
   */
  public Vector2d(ChassisVelocities chassisVelocities) {
    this.vector = new Translation2d(chassisVelocities.vx, chassisVelocities.vy);
  }

  /**
   * Creates a new Vector2d from a {@link ChassisAccelerations} object.
   *
   * @param chassisAccelerations
   */
  public Vector2d(ChassisAccelerations chassisAccelerations) {
    this.vector = new Translation2d(chassisAccelerations.ax, chassisAccelerations.ay);
  }

  /**
   * Creates a new Vector2d from a {@link ChassisJerks} object.
   *
   * @param chassisJerks
   */
  public Vector2d(ChassisJerks chassisJerks) {
    this.vector = new Translation2d(chassisJerks.jx, chassisJerks.jy);
  }

  /**
   * Returns the vector as a Translation2d.
   *
   * @return The vector as a Translation2d.
   */
  public Translation2d toTranslation2d() {
    return vector;
  }

  public Transform2d toTransform2d() {
    return new Transform2d(vector, new Rotation2d());
  }

  /**
   * Returns the vector as a {@link ChassisVelocities} object with zero angular velocity.
   *
   * @return The vector as a {@link ChassisVelocities} object with zero angular velocity.
   */
  public ChassisVelocities toChassisVelocities() {
    return new ChassisVelocities(vector.getX(), vector.getY(), 0);
  }

  /**
   * Returns the vector as a {@link ChassisVelocities} object with the given angular velocity.
   *
   * @param omega The angular velocity to use for the {@link ChassisVelocities} object.
   * @return The vector as a {@link ChassisVelocities} object with the given angular velocity.
   */
  public ChassisVelocities toChassisVelocities(double omega) {
    return new ChassisVelocities(vector.getX(), vector.getY(), omega);
  }

  /**
   * Returns the vector as a {@link ChassisVelocities} object with the given angular velocity.
   *
   * @param omega The angular velocity to use for the {@link ChassisVelocities} object.
   * @return The vector as a {@link ChassisVelocities} object with the given angular velocity.
   */
  public ChassisVelocities toChassisVelocities(AngularVelocity omega) {
    return new ChassisVelocities(vector.getX(), vector.getY(), omega.in(RadiansPerSecond));
  }

  /**
   * Returns the vector as a {@link ChassisAccelerations} object with zero angular acceleration.
   *
   * @return The vector as a {@link ChassisAccelerations} object with zero angular acceleration.
   */
  public ChassisAccelerations toChassisAccelerations() {
    return new ChassisAccelerations(vector.getX(), vector.getY(), 0);
  }

  /**
   * Returns the vector as a {@link ChassisAccelerations} object with the given angular
   * acceleration.
   *
   * @param alpha The angular acceleration to use for the {@link ChassisAccelerations} object.
   * @return The vector as a {@link ChassisAccelerations} object with the given angular
   *     acceleration.
   */
  public ChassisAccelerations toChassisAccelerations(double alpha) {
    return new ChassisAccelerations(vector.getX(), vector.getY(), alpha);
  }

  /**
   * Returns the vector as a {@link ChassisAccelerations} object with the given angular
   * acceleration.
   *
   * @param alpha The angular acceleration to use for the {@link ChassisAccelerations} object.
   * @return The vector as a {@link ChassisAccelerations} object with the given angular
   *     acceleration.
   */
  public ChassisAccelerations toChassisAccelerations(AngularAcceleration alpha) {
    return new ChassisAccelerations(
        vector.getX(), vector.getY(), alpha.in(RadiansPerSecondPerSecond));
  }

  /**
   * Returns the vector as a {@link ChassisJerks} object with zero angular jerk.
   *
   * @return The vector as a {@link ChassisJerks} object with zero angular jerk.
   */
  public ChassisJerks toChassisJerks() {
    return new ChassisJerks(vector.getX(), vector.getY(), 0);
  }

  /**
   * Returns the vector as a {@link ChassisJerks} object with the given angular jerk.
   *
   * @param jerk The angular jerk to use for the ChassisJerks object.
   * @return The vector as a {@link ChassisJerks} object with the given angular jerk.
   */
  public ChassisJerks toChassisJerks(double jerk) {
    return new ChassisJerks(vector.getX(), vector.getY(), jerk);
  }

  /**
   * Applies the vector to a given point in 2D space.
   *
   * @param point The point to apply the vector to.
   * @return The new point after applying the vector.
   */
  public Translation2d applyVector(Translation2d point) {
    return point.plus(vector);
  }

  /**
   * Calculates the distance between two vectors in 2D space.
   *
   * <p>The distance between vectors is defined as √((x₂−x₁)²+(y₂−y₁)²).
   *
   * @param other The vector to compute the distance to.
   * @return The distance between the two vectors.
   */
  public double getDistance(Vector2d other) {
    return Math.hypot(other.vector.getX() - vector.getX(), other.vector.getY() - vector.getY());
  }

  /**
   * Calculates the square of the distance between two vectors in 2D space. This is equivalent to
   * squaring the result of {@link #getDistance(Vector2d)}, but avoids computing a square root.
   *
   * <p>The square of the distance between vectors is defined as (x₂−x₁)²+(y₂−y₁)².
   *
   * @param other The vector to compute the squared distance to.
   * @return The square of the distance between the two vectors, in square meters.
   */
  public double getSquaredDistance(Vector2d other) {
    double dx = other.vector.getX() - vector.getX();
    double dy = other.vector.getY() - vector.getY();
    return dx * dx + dy * dy;
  }

  /**
   * Returns the X component of the vector.
   *
   * @return The X component of the vector.
   */
  public double getX() {
    return vector.getX();
  }

  /**
   * Returns the Y component of the vector.
   *
   * @return The Y component of the vector.
   */
  public double getY() {
    return vector.getY();
  }

  /**
   * Returns the X component of the vector in a measure.
   *
   * @return The x component of the vector in a measure.
   */
  public Distance getMeasureX() {
    return Meters.of(vector.getX());
  }

  /**
   * Returns the Y component of the vector in a measure.
   *
   * @return The y component of the vector in a measure.
   */
  public Distance getMeasureY() {
    return Meters.of(vector.getY());
  }

  /**
   * Returns a 2D translation vector representation of this vector.
   *
   * @return A 2D translation vector representation of this vector.
   */
  public Vector<N2> toVector() {
    return VecBuilder.fill(vector.getX(), vector.getY());
  }

  /**
   * Returns the norm, or distance from the origin to the vector.
   *
   * @return The norm of the vector.
   */
  public double getNorm() {
    return Math.hypot(vector.getX(), vector.getY());
  }

  /**
   * Returns the squared norm, or squared distance from the origin to the vector. This is equivalent
   * to squaring the result of {@link #getNorm()}, but avoids computing a square root.
   *
   * @return The squared norm of the vector, in square meters.
   */
  public double getSquaredNorm() {
    return vector.getX() * vector.getX() + vector.getY() * vector.getY();
  }

  /**
   * Returns the magnitude of the projection of this vector onto another vector.
   *
   * @param other The vector to project onto.
   * @return The magnitude of the projection of this vector onto the other vector.
   */
  public double getProjectionMagnitude(Vector2d other) {
    double dot = this.dot(other);
    double otherNorm = other.getNorm();
    if (otherNorm > 1e-6) {
      return dot / otherNorm;
    } else {
      return 0;
    }
  }

  /**
   * Returns the vector projection of this vector onto another vector.
   *
   * @param other The vector to project onto.
   * @return The vector projection of this vector onto the other vector.
   */
  public Vector2d getProjectionOnto(Vector2d other) {
    return other.getAsUnitVector().times(getProjectionMagnitude(other));
  }

  /**
   * Returns the angle this vector forms with the positive X axis.
   *
   * @return The angle of the vector, or an empty Optional if the angle was undefined.
   */
  public Optional<Rotation2d> getAngle() {
    if (Math.hypot(vector.getX(), vector.getY()) > 1e-6) {
      return Optional.of(new Rotation2d(vector.getX(), vector.getY()));
    } else {
      return Optional.empty();
    }
  }

  /**
   * Returns a unit vector in the same direction as this vector. If the vector is zero, returns a
   * zero vector.
   *
   * @return A unit vector in the same direction as this vector, or a zero vector if this vector is
   *     zero.
   */
  public Vector2d getAsUnitVector() {
    double norm = getNorm();
    if (norm > 1e-6) {
      return this.div(norm);
    } else {
      return new Vector2d(0, 0);
    }
  }

  /**
   * Applies a rotation to the vector in 2D space.
   *
   * <p>This multiplies the vector by a counterclockwise rotation matrix of the given angle.
   *
   * <pre>
   * [x_new]   [other.cos, -other.sin][x]
   * [y_new] = [other.sin,  other.cos][y]
   * </pre>
   *
   * <p>For example, rotating a Vector2d of &lt;2, 0&gt; by 90 degrees will return a Vector2d of
   * &lt;0, 2&gt;.
   *
   * @param other The rotation to rotate the vector by.
   * @return The new rotated vector.
   */
  public Vector2d rotateBy(Rotation2d other) {
    return new Vector2d(
        vector.getX() * other.getCos() - vector.getY() * other.getSin(),
        vector.getX() * other.getSin() + vector.getY() * other.getCos());
  }

  /**
   * Rotates this vector around another vector in 2D space.
   *
   * <pre>
   * [x_new]   [rot.cos, -rot.sin][x - other.x]   [other.x]
   * [y_new] = [rot.sin,  rot.cos][y - other.y] + [other.y]
   * </pre>
   *
   * @param other The other vector to rotate around.
   * @param rot The rotation to rotate the vector by.
   * @return The new rotated vector.
   */
  public Vector2d rotateAround(Vector2d other, Rotation2d rot) {
    return new Vector2d(
        (vector.getX() - other.vector.getX()) * rot.getCos()
            - (vector.getY() - other.vector.getY()) * rot.getSin()
            + other.vector.getX(),
        (vector.getX() - other.vector.getX()) * rot.getSin()
            + (vector.getY() - other.vector.getY()) * rot.getCos()
            + other.vector.getY());
  }

  /**
   * Computes the dot product between this vector and another vector in 2D space.
   *
   * <p>The dot product between two vectors is defined as x₁x₂+y₁y₂.
   *
   * @param other The vector to compute the dot product with.
   * @return The dot product between the two vectors, in square meters.
   */
  public double dot(Vector2d other) {
    return vector.getX() * other.vector.getX() + vector.getY() * other.vector.getY();
  }

  /**
   * Computes the cross product between this vector and another vector in 2D space.
   *
   * <p>The 2D cross product between two vectors is defined as x₁y₂-x₂y₁.
   *
   * @param other The vector to compute the cross product with.
   * @return The cross product between the two vectors, in square meters.
   */
  public double cross(Vector2d other) {
    return vector.getX() * other.vector.getY() - vector.getY() * other.vector.getX();
  }

  /**
   * Returns the sum of two vectors in 2D space.
   *
   * <p>For example, Vector2d(1.0, 2.5) + Vector2d(2.0, 5.5) = Vector2d{3.0, 8.0).
   *
   * @param other The vector to add.
   * @return The sum of the vectors.
   */
  public Vector2d plus(Vector2d other) {
    return new Vector2d(vector.getX() + other.vector.getX(), vector.getY() + other.vector.getY());
  }

  /**
   * Returns the difference between two vectors.
   *
   * <p>For example, Vector2d(5.0, 4.0) - Vector2d(1.0, 2.0) = Vector2d(4.0, 2.0).
   *
   * @param other The vector to subtract.
   * @return The difference between the two vectors.
   */
  public Vector2d minus(Vector2d other) {
    return new Vector2d(vector.getX() - other.vector.getX(), vector.getY() - other.vector.getY());
  }

  /**
   * Returns the inverse of the current vector. This is equivalent to rotating by 180 degrees,
   * flipping the point over both axes, or negating all components of the vector.
   *
   * @return The inverse of the current vector.
   */
  public Vector2d unaryMinus() {
    return new Vector2d(-vector.getX(), -vector.getY());
  }

  /**
   * Returns the vector multiplied by a scalar.
   *
   * <p>For example, Vector2d(2.0, 2.5) * 2 = Vector2d(4.0, 5.0).
   *
   * @param scalar The scalar to multiply by.
   * @return The scaled vector.
   */
  public Vector2d times(double scalar) {
    return new Vector2d(vector.getX() * scalar, vector.getY() * scalar);
  }

  /**
   * Returns the vector divided by a scalar.
   *
   * <p>For example, Vector2d(2.0, 2.5) / 2 = Vector2d(1.0, 1.25).
   *
   * @param scalar The scalar to multiply by.
   * @return The reference to the new mutated object.
   */
  public Vector2d div(double scalar) {
    return new Vector2d(vector.getX() / scalar, vector.getY() / scalar);
  }

  /**
   * Returns the nearest Vector2d from a collection of vectors.
   *
   * @param vectors The collection of vectors.
   * @return The nearest Vector2d from the collection.
   */
  public Vector2d nearest(Collection<Vector2d> vectors) {
    return Collections.min(vectors, Comparator.comparing(this::getDistance));
  }

  @Override
  public String toString() {
    return String.format("Vector2d(X: %.2f, Y: %.2f)", vector.getX(), vector.getY());
  }

  /**
   * Checks equality between this Vector2d and another object.
   *
   * @param obj The other object.
   * @return Whether the two objects are equal or not.
   */
  @Override
  public boolean equals(Object obj) {
    return obj instanceof Vector2d other
        && Math.abs(other.vector.getX() - vector.getX()) < 1E-9
        && Math.abs(other.vector.getY() - vector.getY()) < 1E-9;
  }

  /**
   * Returns the hash code for this Vector2d.
   *
   * @return The hash code for this Vector2d.
   */
  @Override
  public int hashCode() {
    return vector.hashCode();
  }

  /**
   * Interpolates between this Vector2d and another Vector2d.
   *
   * @param endValue The other Vector2d to interpolate to.
   * @param t The interpolation parameter, where 0.0 returns this Vector2d and 1.0 returns the other
   *     Vector2d.
   * @return The interpolated Vector2d.
   */
  @Override
  public Vector2d interpolate(Vector2d endValue, double t) {
    return new Vector2d(
        MathUtil.lerp(this.getX(), endValue.getX(), t),
        MathUtil.lerp(this.getY(), endValue.getY(), t));
  }

  /**
   * Returns a new Vector2d that is the normalized version of this vector.
   *
   * @param translation The translation to normalize.
   * @return A new Vector2d that is the normalized version of this vector.
   */
  public static Vector2d fromTranslation(Translation2d translation) {
    return new Vector2d(translation);
  }

  /**
   * Returns a new Vector2d that is the normalized version of this vector.
   *
   * @param angle The angle to create the vector from.
   * @param magnitude The magnitude of the vector.
   * @return A new Vector2d that is the normalized version of this vector.
   */
  public static Vector2d fromAngleAndMagnitude(Angle angle, double magnitude) {
    return new Vector2d(angle, magnitude);
  }

  /**
   * Returns a new Vector2d from x and y components.
   *
   * @param x The x component.
   * @param y The y component.
   * @return A new Vector2d.
   */
  public static Vector2d fromXY(double x, double y) {
    return new Vector2d(x, y);
  }

  /**
   * Returns a new Vector2d from a {@link Vector}.
   *
   * @param vector The vector to convert.
   * @return A new Vector2d.
   */
  public static Vector2d fromVector(Vector<N2> vector) {
    return new Vector2d(vector.get(0, 0), vector.get(1, 0));
  }

  /**
   * Returns a unit vector from an angle.
   *
   * @param angle The angle to create the unit vector from.
   * @return A unit vector from the angle.
   */
  public static Vector2d getUnitVectorFromAngle(Angle angle) {
    return new Vector2d(angle, 1.0);
  }

  /**
   * Returns a unit vector from a rotation.
   *
   * @param rotation The rotation to create the unit vector from.
   * @return A unit vector from the rotation.
   */
  public static Vector2d getUnitVectorFromRotation(Rotation2d rotation) {
    return new Vector2d(rotation, 1.0);
  }
}
