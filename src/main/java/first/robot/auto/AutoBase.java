package first.robot.auto;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.path.PathPlannerPath;
import com.pathplanner.lib.util.FlippingUtil;
import com.team2052.lib.helpers.CommandBuilder;
import first.robot.auto.chorPath.ChorPaths;
import org.wpilib.command3.Command;
import org.wpilib.driverstation.Alliance;
import org.wpilib.driverstation.DriverStation;
import org.wpilib.driverstation.MatchState;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.system.DataLogManager;

public class AutoBase {

  public Command followPathCommand(ChorPaths chorPath) {
    try {
      PathPlannerPath path = PathPlannerPath.fromChoreoTrajectory(chorPath.getPathName());
      path.preventFlipping = false;
      return AutoBuilder.followPath(path);
    } catch (Exception e) {
      DataLogManager.log(
          "CANNOT FIND CHOREO PATH NAMED: " + chorPath.getPathName() + "," + e.getStackTrace());
      return CommandBuilder.instant(() -> {}, "Empty Command");
    }
  }

  public Pose2d getStartPose(ChorPaths chorPath) {
    try {
      PathPlannerPath path = PathPlannerPath.fromChoreoTrajectory(chorPath.getPathName());
      return getAllianceAdjustedPose(path.getStartingHolonomicPose().get());
    } catch (Exception e) {
      DataLogManager.log(
          "CANNOT FIND CHOREO PATH NAMED: " + chorPath.getPathName()+","+e.getStackTrace());
      return new Pose2d();
    }
  }

  public static Pose2d getAllianceAdjustedPose(Pose2d bluePose) {
    if (MatchState.getAlliance().orElse(Alliance.BLUE) == Alliance.RED) {
      return FlippingUtil.flipFieldPose(bluePose);
    } else {
      return bluePose;
    }
  }


  public void doSomething() {
  }
}
