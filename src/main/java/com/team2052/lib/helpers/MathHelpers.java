package com.team2052.lib.helpers;

import static org.wpilib.units.Units.Radians;

import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.geometry.Transform3d;
import org.wpilib.math.geometry.Translation2d;
import org.wpilib.math.geometry.Translation3d;
import org.wpilib.math.kinematics.ChassisVelocities;
import org.wpilib.math.util.MathUtil;
import org.wpilib.units.measure.Angle;

public class MathHelpers {

  public static final Pose2d POSE_2D_ZERO = new Pose2d();
  public static final Rotation2d ROTATION_2D_ZERO = new Rotation2d();
  public static final Rotation2d ROTATION_2D_PI = Rotation2d.fromDegrees(180.0);
  public static final Translation2d TRANSLATION_2D_ZERO = new Translation2d();

  /**
   * Returns a Pose2d with the given rotation and a translation of (0, 0).
   *
   * @param rotation The rotation to use for the Pose2d.
   * @return A Pose2d with the given rotation and a translation of (0, 0).
   */
  public static final Pose2d pose2dFromRotation(Rotation2d rotation) {
    return new Pose2d(TRANSLATION_2D_ZERO, rotation);
  }

  /**
   * Returns a Pose2d with the given translation and a rotation of 0 degrees.
   *
   * @param translation The translation to use for the Pose2d.
   * @return A Pose2d with the given translation and a rotation of 0 degrees.
   */
  public static final Pose2d pose2dFromTranslation(Translation2d translation) {
    return new Pose2d(translation, ROTATION_2D_ZERO);
  }

  /**
   * Applies a deadband to the given value. If the absolute value of the input is less than the
   * deadband, returns 0. Otherwise, scales the input to be in the range [0, 1] outside of the
   * deadband.
   *
   * @param value The input value.
   * @param deadband The deadband threshold.
   * @return The value with the deadband applied.
   */
  public static double deadband(double value, double deadband) {
    deadband = Math.abs(deadband);
    if (deadband == 1) {
      return 0;
    }
    double scaledValue = (value + (value < 0 ? deadband : -deadband)) / (1 - deadband);
    return (Math.abs(value) > Math.abs(deadband)) ? scaledValue : 0;
  }

  /**
   * Returns the norm of the translational components of a ChassisVelocities object, which is just
   * the hypotenuse of the x and y velocities.
   *
   * @param velocities The ChassisVelocities object.
   * @return The norm of the translational components of the ChassisVelocities object.
   */
  public static double chassisVelocitiesNorm(ChassisVelocities velocities) {
    return Math.hypot(velocities.vx, velocities.vy);
  }

  /**
   * Returns the Transform3d with the smaller translation norm.
   *
   * @see MathHelpers.chassisVelocitiesNorm(ChassisVelocities) for how the translation norm is
   *     calculated.
   * @param t1 The first transform.
   * @param t2 The second transform.
   * @return The transform with the smaller translation norm.
   */
  public static Transform3d getSmallestTransform(Transform3d t1, Transform3d t2) {
    if (t1.getTranslation().getNorm() < t2.getTranslation().getNorm()) {
      return t1;
    }

    return t2;
  }

  /**
   * Clamps a value between a minimum and maximum value.
   *
   * @param value The value to clamp.
   * @param min The minimum value.
   * @param max The maximum value.
   * @return The clamped value.
   */
  public static double clamp(double value, double min, double max) {
    return Math.max(min, Math.min(max, value));
  }

  /**
   * Calculates the modular inverse of a value with respect to a given modulus using the Extended
   * Euclidean Algorithm.
   *
   * @param value The value for which to find the modular inverse.
   * @param modulus The modulus.
   * @return The modular inverse of the value with respect to the modulus.
   */
  public static int modInverse(int value, int modulus) {
    int originalModulus = modulus;

    int previousCoefficient = 0;
    int currentCoefficient = 1;

    int previousRemainder = modulus;
    int currentRemainder = value;

    while (currentRemainder != 0) {
      int quotient = previousRemainder / currentRemainder;

      int nextRemainder = previousRemainder - quotient * currentRemainder;
      previousRemainder = currentRemainder;
      currentRemainder = nextRemainder;

      int nextCoefficient = previousCoefficient - quotient * currentCoefficient;
      previousCoefficient = currentCoefficient;
      currentCoefficient = nextCoefficient;
    }

    if (previousRemainder != 1) {
      throw new IllegalArgumentException("No modular inverse exists");
    }

    int inverse = previousCoefficient;
    if (inverse < 0) inverse += originalModulus;

    return inverse;
  }

  /**
   * Calculates the hypotenuse of a variable number of double values. This method computes the
   * square root of the sum of the squares of the provided values.
   *
   * @param doubles The values for which to calculate the hypotenuse.
   * @return The hypotenuse of the provided values.
   */
  public static double hypot(double... doubles) {
    double sum = 0;
    for (double d : doubles) {
      sum += d * d;
    }
    return Math.sqrt(sum);
  }

  /* ------------------------- Epsilon Checks ------------------------- */

  /**
   * Checks if two doubles are equal within a given epsilon (difference).
   *
   * @param a The first double.
   * @param b The second double.
   * @param epsilon The epsilon value.
   * @return True if the doubles are equal within the epsilon, false otherwise.
   */
  public static boolean epsilonEquals(double a, double b, double epsilon) {
    return (a - epsilon <= b) && (a + epsilon >= b);
  }

  /**
   * Checks if two angles are equal within a given epsilon (difference).
   *
   * @param angle1 The first angle.
   * @param angle2 The second angle.
   * @param epsilon The epsilon value.
   * @return True if the angles are equal within the epsilon, false otherwise.
   */
  public static boolean angleEpsilonEquals(Angle angle1, Angle angle2, Angle epsilon) {
    // pi to -pi
    double angle1Rads = MathUtil.angleModulus(angle1.in(Radians));
    double angle2Rads = MathUtil.angleModulus(angle2.in(Radians));

    double difference0 = Math.abs(angle1Rads - angle2Rads);
    double difference1 = Math.abs(angle1Rads - angle2Rads + 2 * Math.PI);
    double difference2 = Math.abs(angle1Rads - angle2Rads - 2 * Math.PI);

    return (difference0 <= epsilon.in(Radians)
        || difference1 <= epsilon.in(Radians)
        || difference2 <= epsilon.in(Radians));
  }

  /**
   * Checks if two Translation2d objects are equal within a given epsilon (difference).
   *
   * @param translation1 The first Translation2d.
   * @param translation2 The second Translation2d.
   * @param epsilon The epsilon value.
   * @return True if the Translation2d objects are equal within the epsilon, false otherwise.
   */
  public static boolean translation2dEpsilonEquals(
      Translation2d translation1, Translation2d translation2, double epsilon) {
    return Math.hypot(
            translation1.getX() - translation2.getX(), translation1.getY() - translation2.getY())
        <= epsilon;
  }

  /**
   * Checks if two Translation2d objects are equal within a given epsilon (difference) for both x
   * and y coordinates.
   *
   * @param translation1 The first Translation2d.
   * @param translation2 The second Translation2d.
   * @param epsilonX The epsilon value for the x-coordinate.
   * @param epsilonY The epsilon value for the y-coordinate.
   * @return True if the Translation2d objects are equal within the epsilon for both coordinates,
   *     false otherwise.
   */
  public static boolean translation2dEpsilonEquals(
      Translation2d translation1, Translation2d translation2, double epsilonX, double epsilonY) {
    return epsilonEquals(translation1.getX(), translation2.getX(), epsilonX)
        && epsilonEquals(translation1.getY(), translation2.getY(), epsilonY);
  }

  public static boolean translation3dEpsilonEquals(
      Translation3d translation1, Translation3d translation2, double epsilon) {
    return MathHelpers.hypot(
            translation1.getX() - translation2.getX(),
            translation1.getY() - translation2.getY(),
            translation1.getZ() - translation2.getZ())
        <= epsilon;
  }

  /**
   * Checks if two Pose2d objects are equal within a given epsilon (difference) for both translation
   * and rotation.
   *
   * @param pose1 The first Pose2d.
   * @param pose2 The second Pose2d.
   * @param epsilonXY The epsilon value for the translation coordinates.
   * @param epsilonRotation The epsilon value for the rotation.
   * @return True if the Pose2d objects are equal within the epsilon for both translation and
   *     rotation, false otherwise.
   */
  public static boolean pose2dEpsilonEquals(
      Pose2d pose1, Pose2d pose2, double epsilonXY, Angle epsilonRotation) {
    return translation2dEpsilonEquals(pose1.getTranslation(), pose2.getTranslation(), epsilonXY)
        && angleEpsilonEquals(
            pose1.getRotation().getMeasure(), pose2.getRotation().getMeasure(), epsilonRotation);
  }
}
