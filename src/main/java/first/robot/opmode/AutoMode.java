// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package first.robot.opmode;

import com.team2052.lib.helpers.CommandBuilder;
import first.robot.Constants;
import first.robot.Robot;
import first.robot.RobotState;
import org.wpilib.command3.Command;
import org.wpilib.command3.Scheduler;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.opmode.Autonomous;
import org.wpilib.opmode.PeriodicOpMode;
import org.wpilib.util.Pair;

@Autonomous(name = "Auto", group = "Autos", textColor = Constants.OpModeColors.AUTO)
public class AutoMode extends PeriodicOpMode {
  private final Robot robot;

  // TODO: Replace with actual auto command getter.
  private final Pair<Pose2d, Command> commandInitialize =
      new Pair<Pose2d, Command>(
          new Pose2d(),
          CommandBuilder.instant(
                  () -> {
                    return;
                  })
              .named("Command"));

  /** The Robot instance is passed into the opmode via the constructor. */
  public AutoMode(Robot robot) {
    this.robot = robot;
  }

  @Override
  public void start() {
    // Runs the auto
    Scheduler.getDefault().schedule(commandInitialize.getSecond());
  }

  @Override
  public void disabledPeriodic() {
    // Initialize the starting pose for the auto, resets the actual pose here.
    RobotState.getInstance().seedAutoStartPose(commandInitialize.getFirst());
  }

  @Override
  public void end() {
    // Make sure the command stops running once the auto ends
    Scheduler.getDefault().cancel(commandInitialize.getSecond());
  }

  /*
   * This method runs periodically, using the same period as the Robot instance.
   *
   * Additional periodic methods may be configured with addPeriodic(),
   * which can have periods that differ from the main Robot instance.
   */
  @Override
  public void periodic() {}
}
