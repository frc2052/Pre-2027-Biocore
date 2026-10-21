package first.robot.auto.PathPlanner;

import org.wpilib.command3.Command;
import org.wpilib.command3.Coroutine;
import org.wpilib.command3.Mechanism;
import org.wpilib.command3.NeedsNameBuilderStage;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.util.Pair;

import first.robot.auto.ChorPaths;

public class AutoFactory extends AutoBase implements Mechanism {

  public Pair<Pose2d, Command> PathAuto() {
    return Pair.of(getStartPose(ChorPaths.TEST_PATH), followPathPlannerPathCommand(ChorPaths.TEST_PATH));
  }


  public Pair<Pose2d, Command> doSomethingAuto(){
    return Pair.of(getStartPose(ChorPaths.TEST_PATH), Command.noRequirements(Coroutine-> {
        Coroutine.await(followPathPlannerPathCommand(ChorPaths.TEST_PATH));
        try {
            Coroutine.wait(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        doSomething();  
    }).named("bruh"));
    }
  }



