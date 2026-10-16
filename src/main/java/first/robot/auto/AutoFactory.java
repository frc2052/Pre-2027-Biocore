package first.robot.auto;

import org.wpilib.command3.Command;
import org.wpilib.command3.Mechanism;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.util.Pair;

public class AutoFactory extends AutoBase implements Mechanism {

  // ------------------------------- EXAMPLE CODE -------------------------------
  public Pair<Pose2d, Command> PathAuto() {
    return Pair.of(getStartPose(ChorPaths.TEST_PATH), followPathCommand(ChorPaths.TEST_PATH));
  }

  public Pair<Pose2d, Command> doSomethingAuto() {
    return Pair.of(
        getStartPose(ChorPaths.TEST_PATH),
        Command.noRequirements(
                Coroutine -> {
                  Coroutine.await(followPathCommand(ChorPaths.TEST_PATH));
                  try {
                    Coroutine.wait(1000);
                  } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                  }
                  doSomething();
                })
            .named("bruh"));
  }
}
