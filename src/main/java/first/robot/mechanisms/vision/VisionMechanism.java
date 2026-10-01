package first.robot.mechanisms.vision;

import com.team2052.lib.vision.limelight.LimelightCamera;
import com.team2052.lib.vision.questnav.QuestNavMechanism;

public class VisionMechanism extends QuestNavMechanism {

    private static VisionMechanism INSTANCE;

    private final LimelightCamera leftLimelight;
    private final LimelightCamera rightLimelight;

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
    }

    @Override
    public void outputPeriodic() {
        super.outputPeriodic();
    }

    @Override
    public void loggingPeriodic() {
        super.loggingPeriodic();
    }

}