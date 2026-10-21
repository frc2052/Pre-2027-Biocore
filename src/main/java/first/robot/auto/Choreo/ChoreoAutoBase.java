package first.robot.auto.Choreo;

import choreo.auto.AutoFactory;
import first.robot.RobotState;
import first.robot.mechanisms.drive.DrivetrainMechanism;

public class ChoreoAutoBase {
    private final DrivetrainMechanism drivetrain = DrivetrainMechanism.getInstance();
    private final RobotState robotState = RobotState.getInstance();
    private final AutoFactory autofactory = new AutoFactory(
        robotState::getRobotPose, 
        robotState::getSeedPose , 
        drivetrain::followChoreoTraj,
        true,
        drivetrain); // TODO: need to do after choreo updates to V3 commands. 
}
