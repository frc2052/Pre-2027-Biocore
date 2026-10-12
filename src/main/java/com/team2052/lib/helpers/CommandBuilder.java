package com.team2052.lib.helpers;

import static org.wpilib.units.Units.Seconds;

import org.wpilib.command3.Command;
import org.wpilib.command3.NeedsNameBuilderStage;
import org.wpilib.units.measure.Time;

public class CommandBuilder {

  /**
   * Creates an instant command with the specified runnable. Still needs to be named.
   *
   * @param runnable the runnable to execute immediately
   * @return a {@link NeedsNameBuilderStage} of the command.
   */
  public static NeedsNameBuilderStage instant(Runnable runnable) {
    return Command.noRequirements(
        coroutine -> {
          runnable.run();
        });
  }

  /**
   * Creates an instant command with the specified runnable and name.
   *
   * @param runnable the runnable to execute immediately
   * @param name the name of the command
   * @return an instant command with the specified runnable and name
   */
  public static Command instant(Runnable runnable, String name) {
    return instant(runnable).named(name);
  }

  /**
   * Creates a command that waits for the specified amount of time.
   *
   * @param time time to wait
   * @return a command that waits for the specified amount of time
   */
  public static Command wait(Time time) {
    return Command.waitFor(time).named("Wait for " + time.toLongString());
  }

  /**
   * Creates a command that waits for the specified number of seconds.
   *
   * @param seconds seconds to wait
   * @return a command that waits for the specified number of seconds
   */
  public static Command wait(double seconds) {
    return Command.waitFor(Seconds.of(seconds)).named("Wait for " + seconds + " seconds");
  }
}
