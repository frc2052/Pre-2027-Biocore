package first.robot.mechanisms.vision;

import static org.wpilib.units.Units.Milliseconds;
import static org.wpilib.units.Units.Seconds;

import com.team2052.lib.estimators.PositionEstimator.PoseMeasurement;
import com.team2052.lib.vision.limelight.LimelightCamera;
import com.team2052.lib.vision.limelight.LimelightHelpers.PoseEstimate;
import com.team2052.lib.vision.questnav.QuestNavMechanism;
import first.robot.RobotState;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.wpilib.math.geometry.Pose3d;
import org.wpilib.math.geometry.Transform3d;
import org.wpilib.math.linalg.VecBuilder;
import org.wpilib.system.Timer;
import org.wpilib.units.measure.Time;
import org.wpilib.util.Pair;

public class VisionMechanism extends QuestNavMechanism {

  private static VisionMechanism INSTANCE;

  private final LimelightCamera leftLimelight;
  private final LimelightCamera rightLimelight;

  @Getter private boolean isLeftObjDetect = false;
  @Getter private boolean isRightObjDetect = false;

  private Pair<PoseMeasurement, PoseMeasurement> lastLeftMeasurement =
      new Pair<>(new PoseMeasurement(), new PoseMeasurement());
  private Pair<PoseMeasurement, PoseMeasurement> lastRightMeasurement =
      new Pair<>(new PoseMeasurement(), new PoseMeasurement());

  public static VisionMechanism getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new VisionMechanism();
    }
    return INSTANCE;
  }

  public VisionMechanism() {
    super(VisionConstants.QUEST_NAV_CONSTANTS);
    leftLimelight = new LimelightCamera(VisionConstants.LEFT_LIMELIGHT_CONSTANTS);
    rightLimelight = new LimelightCamera(VisionConstants.RIGHT_LIMELIGHT_CONSTANTS);
  }

  @Override
  public void inputPeriodic() {
    super.inputPeriodic();

    if (isLeftObjDetect) {
      readObjectDetectionLimelight(leftLimelight);
    } else {
      lastLeftMeasurement = readAprilTagLimelight(leftLimelight);
    }

    if (isRightObjDetect) {
      readObjectDetectionLimelight(rightLimelight);
    } else {
      lastRightMeasurement = readAprilTagLimelight(rightLimelight);
    }
  }

  @Override
  public void outputPeriodic() {
    super.outputPeriodic();
  }

  @Override
  public void loggingPeriodic() {
    super.loggingPeriodic();
  }

  public void setLeftToObjectDetection() {
    if (!VisionConstants.LEFT_LIMELIGHT_CONSTANTS.canDoObjectDetection) return;
    isLeftObjDetect = true;
    leftLimelight.setPipeline(VisionConstants.LEFT_LIMELIGHT_CONSTANTS.objectDetectionPipeline);
  }

  public void setLeftToAprilTag() {
    isLeftObjDetect = false;
    leftLimelight.setPipeline(VisionConstants.LEFT_LIMELIGHT_CONSTANTS.aprilTagPipeline);
  }

  public void toggleLeftPipeline() {
    if (isLeftObjDetect) {
      setLeftToAprilTag();
    } else {
      setLeftToObjectDetection();
    }
  }

  public void setRightToObjectDetection() {
    if (!VisionConstants.RIGHT_LIMELIGHT_CONSTANTS.canDoObjectDetection) return;
    isRightObjDetect = true;
    rightLimelight.setPipeline(VisionConstants.RIGHT_LIMELIGHT_CONSTANTS.objectDetectionPipeline);
  }

  public void setRightToAprilTag() {
    isRightObjDetect = false;
    rightLimelight.setPipeline(VisionConstants.RIGHT_LIMELIGHT_CONSTANTS.aprilTagPipeline);
  }

  public void toggleRightPipeline() {
    if (isRightObjDetect) {
      setRightToAprilTag();
    } else {
      setRightToObjectDetection();
    }
  }

  private Pair<PoseMeasurement, PoseMeasurement> readAprilTagLimelight(LimelightCamera limelight) {

    double[] stddevs = limelight.getSTDDevs();
    PoseEstimate mtOnePose = limelight.getBotPoseEstimate_wpiBlue();
    PoseEstimate mtTwoPose = limelight.getBotPoseEstimate_Orb_wpiBlue();

    Time timestamp = Seconds.of(Timer.getMonotonicTimestamp());

    PoseMeasurement mtOneMeasure =
        new PoseMeasurement(
            mtOnePose.pose,
            VecBuilder.fill(stddevs[0], stddevs[1], stddevs[5]),
            Milliseconds.of(mtOnePose.latency),
            timestamp);
    PoseMeasurement mtTwoMeasure =
        new PoseMeasurement(
            mtTwoPose.pose,
            VecBuilder.fill(stddevs[6], stddevs[7], stddevs[11]),
            Milliseconds.of(mtTwoPose.latency),
            timestamp);

    return new Pair<>(mtOneMeasure, mtTwoMeasure);
  }

  private Pose3d readObjectDetectionLimelight(LimelightCamera limelight) {
    Pose3d objectPose = limelight.getTargetPose_RobotSpace();
    Transform3d objectOffset =
        new Transform3d(
            objectPose.getX(), objectPose.getY(), objectPose.getZ(), objectPose.getRotation());
    Pose3d robotPose = new Pose3d(RobotState.getInstance().getRobotPose());
    return robotPose.transformBy(objectOffset);
  }

  public PoseMeasurement[] getAllValidMeasurements() {
    List<PoseMeasurement> validMeasurements = new ArrayList<>();

    if (isLeftObjDetect) {
      validMeasurements.add(lastLeftMeasurement.getFirst());
      validMeasurements.add(lastLeftMeasurement.getSecond());
    }

    if (isRightObjDetect) {
      validMeasurements.add(lastRightMeasurement.getFirst());
      validMeasurements.add(lastRightMeasurement.getSecond());
    }

    return validMeasurements.toArray(new PoseMeasurement[0]);
  }
}
