package com.team2052.lib.vision;

import static org.wpilib.units.Units.Degrees;
import static org.wpilib.units.Units.Radians;

import com.ctre.phoenix6.swerve.SwerveDrivetrain.SwerveDriveState;
import com.team2052.lib.vision.limelight.LimelightHelpers.PoseEstimate;
import com.team2052.lib.vision.limelight.LimelightHelpers.RawFiducial;
import com.team2052.lib.vision.questnav.QuestNavMechanism.QuestState;
import java.util.Optional;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.geometry.Rotation3d;
import org.wpilib.math.linalg.VecBuilder;
import org.wpilib.math.linalg.Vector;
import org.wpilib.math.numbers.N3;
import org.wpilib.units.measure.Angle;
import org.wpilib.util.Pair;

public class StandardDeviationCalculator {

  public static Vector<N3> getMegaTagOneStdDev(
      double xyCoefficient,
      double angleCoefficient,
      PoseEstimate estimate,
      Pose2d currentPose,
      Vector<N3> baselineDeviation) {

    // return max if no valid tags
    if (estimate.rawFiducials.length == 0)
      return VecBuilder.fill(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);

    double closestTagDist = Double.MAX_VALUE;
    double avgAmbiguity = 0;
    for (RawFiducial fiducial : estimate.rawFiducials) {
      if (fiducial.distToCamera < closestTagDist) {
        closestTagDist = fiducial.distToCamera;
      }
      avgAmbiguity += fiducial.ambiguity;
    }
    if (closestTagDist < 1) closestTagDist = 1;
    avgAmbiguity = avgAmbiguity / estimate.rawFiducials.length;

    if (avgAmbiguity > 0.7)
      return VecBuilder.fill(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);

    double distToCurrent = estimate.pose.getTranslation().getDistance(currentPose.getTranslation());
    double angleDist =
        Math.abs(estimate.pose.getRotation().getRadians() - currentPose.getRotation().getRadians());

    double distSTD = distToCurrent < 1 ? Math.pow(distToCurrent, 2) : Double.MAX_VALUE;
    double angleSTD =
        angleDist < Math.PI / 4 || distSTD < 1
            ? Math.pow(angleDist / Math.PI / 4, 2) * Math.PI / 4
            : Double.MAX_VALUE;

    double xyStddev =
        xyCoefficient
            * ((Math.pow(closestTagDist, 2) / 4) + distSTD)
            / (estimate.tagCount * avgAmbiguity);
    double angleStdDev =
        angleCoefficient
            * ((Math.pow(closestTagDist, 2) / 4) + angleSTD)
            / (2 * estimate.tagCount * avgAmbiguity);

    return VecBuilder.fill(xyStddev, xyStddev, angleStdDev).plus(baselineDeviation);
  }

  public static Vector<N3> getMegaTagTwoStdDev(
      double xyCoefficient,
      PoseEstimate estimate,
      Vector<N3> baselineDeviation,
      double pushedYawDeviation) {

    // return max if no valid tags
    if (estimate.rawFiducials.length == 0)
      return VecBuilder.fill(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);

    double angleStdDev = Double.MAX_VALUE;

    double closestTagDist = Double.MAX_VALUE;
    for (RawFiducial fiducial : estimate.rawFiducials) {
      if (fiducial.distToCamera < closestTagDist) {
        closestTagDist = fiducial.distToCamera;
      }
    }
    if (closestTagDist < 1) closestTagDist = 1;

    double angleDistDev = Math.sin(pushedYawDeviation) * closestTagDist;

    double xyStddev =
        xyCoefficient * ((Math.pow(closestTagDist, 2) / 4) + angleDistDev) / (estimate.tagCount);

    return VecBuilder.fill(xyStddev, xyStddev, angleStdDev).plus(baselineDeviation);
  }

  public static Vector<N3> getQuestNavStdDev(
      double xyCoefficient,
      double angleCoefficient,
      QuestState state,
      Pose2d currentPose,
      Pair<Pose2d, Optional<Pair<Pose2d, Vector<N3>>>> lastOdomPose,
      Vector<N3> baselineDeviation) {

    // return max if not both connected and tracking
    if (!(state.isConnected && state.isTracking))
      return VecBuilder.fill(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);

    Pose2d lastPose = lastOdomPose.getFirst();
    Pose2d lastEstimation = lastOdomPose.getSecond().get().getFirst();

    double x = (state.flatRobotPose.getX() - lastPose.getX()) + lastEstimation.getX();
    double y = (state.flatRobotPose.getY() - lastPose.getY()) + lastEstimation.getY();
    double theta =
        (state.flatRobotPose.getRotation().getRadians() - lastPose.getRotation().getRadians())
            + lastEstimation.getRotation().getRadians();

    Pose2d actualMeasuredPose = new Pose2d(x, y, new Rotation2d(theta));

    double distToCurrent =
        actualMeasuredPose.getTranslation().getDistance(currentPose.getTranslation());
    double angleDist =
        Math.abs(
            actualMeasuredPose.getRotation().getRadians() - currentPose.getRotation().getRadians());

    double distSTD = distToCurrent < 1 ? Math.pow(distToCurrent, 2) : Double.MAX_VALUE;
    double angleSTD =
        angleDist < Math.PI / 4 || distSTD < 1
            ? Math.pow(angleDist / Math.PI / 4, 2) * Math.PI / 4
            : Double.MAX_VALUE;

    double xyStddev = xyCoefficient * (distSTD) / 2;
    double angleStdDev = angleCoefficient * (angleSTD) / 4;

    return VecBuilder.fill(xyStddev, xyStddev, angleStdDev).plus(baselineDeviation);
  }

  public static Vector<N3> getWheelOdometryStdDev(
      double xyCoefficient,
      double angleCoefficient,
      SwerveDriveState state,
      Rotation3d robotRotation,
      Vector<N3> baselineDeviation) {

    Angle totalNonYawAngle = Radians.of(Math.hypot(robotRotation.getX(), robotRotation.getX()));

    double rotStdDev = 0;
    if (totalNonYawAngle.abs(Degrees) > 5) {
      rotStdDev = totalNonYawAngle.abs(Degrees) / 4;
    }

    double xyStddev = xyCoefficient * rotStdDev;
    double angleStdDev = angleCoefficient * 0.1;
    return VecBuilder.fill(xyStddev, xyStddev, angleStdDev).plus(baselineDeviation);
  }
}
