package first.robot.auto;

import com.pathplanner.lib.path.PathPlannerPath;
import com.pathplanner.lib.util.FlippingUtil;
import com.team2052.lib.commands.CommandBuilder;
import com.team2052.lib.util.RobotInfo;
import first.robot.commands.drive.secede.DriveToPoseCommand;
import org.wpilib.command3.Command;
import org.wpilib.driverstation.DriverStationErrors;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.util.Pair;

public class AutoBase {

  /**
   * Command that drives to the specified pose
   *
   * @param pose the pose to drive to
   * @return The command that drives to the pose
   */
  public static Command driveToPoseCommand(Pose2d pose) {
    return new DriveToPoseCommand(() -> true, true, () -> pose).create();
  }

  /**
   * Command that follows the provided Choreo Path
   *
   * @param chorPath the Choreo Path to follow
   * @return The command that follows the path
   */
  public static Command followPathCommand(ChorPaths chorPath) {
    try {
      PathPlannerPath path = PathPlannerPath.fromChoreoTrajectory(chorPath.getPathName());
      path.preventFlipping = false;
      // return AutoBuilder.followPath(path); TODO: Update when PathPlannerLib has CommandsV3
      return CommandBuilder.emptyInstantCommand();
    } catch (Exception e) {
      DriverStationErrors.reportError(
          "CANNOT FIND CHOREO PATH NAMED: " + chorPath.getPathName(), e.getStackTrace());
      return CommandBuilder.emptyInstantCommand();
    }
  }

  /**
   * Gets the starting pose for the given Choreo Path
   *
   * @param chorPath the Choreo Path to get the starting pose for
   * @return The starting pose
   */
  public static Pose2d getStartPose(ChorPaths chorPath) {
    try {
      PathPlannerPath path = PathPlannerPath.fromChoreoTrajectory(chorPath.getPathName());
      return getAllianceAdjustedPose(path.getStartingHolonomicPose().get());
    } catch (Exception e) {
      DriverStationErrors.reportError(
          "CANNOT FIND CHOREO PATH NAMED: " + chorPath.getPathName(), e.getStackTrace());
      return new Pose2d();
    }
  }

  /**
   * Gets the starting pose and command for the given Choreo Path
   *
   * @param chorPath the Choreo Path to get the starting pose and command for
   * @return A pair containing the starting pose and the command to follow the path
   */
  public static Pair<Pose2d, Command> getStartPoseAndCommand(ChorPaths chorPath) {
    return new Pair<>(getStartPose(chorPath), followPathCommand(chorPath));
  }

  /**
   * Gets the alliance-adjusted pose for the given pose
   *
   * @param bluePose the pose to adjust
   * @return The alliance-adjusted pose
   */
  public static Pose2d getAllianceAdjustedPose(Pose2d bluePose) {
    if (RobotInfo.isRedAlliance()) {
      return FlippingUtil.flipFieldPose(bluePose);
    } else {
      return bluePose;
    }
  }

  public static void doSomething() {}
}
