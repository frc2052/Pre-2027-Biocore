package first.robot.mechanisms.vision;

import com.team2052.lib.vision.limelight.LimelightCamera.LimelightConstants;
import com.team2052.lib.vision.questnav.QuestNavConstants;
import org.wpilib.math.geometry.Pose3d;
import org.wpilib.math.geometry.Transform3d;

public class VisionConstants {

  public static final QuestNavConstants QUEST_NAV_CONSTANTS = new QuestNavConstants();
  public static final LimelightConstants LEFT_LIMELIGHT_CONSTANTS = new LimelightConstants();
  public static final LimelightConstants RIGHT_LIMELIGHT_CONSTANTS = new LimelightConstants();

  static {
    QUEST_NAV_CONSTANTS.name = "Vision Mechanism";
    QUEST_NAV_CONSTANTS.questPose = new Transform3d();

    LEFT_LIMELIGHT_CONSTANTS.defaultPipeline = 0;
    LEFT_LIMELIGHT_CONSTANTS.limelightName = "Left Limelight";
    LEFT_LIMELIGHT_CONSTANTS.limelightPose = new Pose3d();

    RIGHT_LIMELIGHT_CONSTANTS.defaultPipeline = 0;
    RIGHT_LIMELIGHT_CONSTANTS.limelightName = "Right Limelight";
    RIGHT_LIMELIGHT_CONSTANTS.limelightPose = new Pose3d();
  }
}
