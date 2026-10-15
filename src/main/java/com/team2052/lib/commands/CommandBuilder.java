package com.team2052.lib.commands;

import static org.wpilib.units.Units.Seconds;

import java.util.function.Consumer;
import org.wpilib.command3.Command;
import org.wpilib.command3.Coroutine;
import org.wpilib.command3.Mechanism;
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

  /**
   * Creates a command with no mechanism requirements.
   *
   * @param name The name of the command
   * @param body The body of the command, executed by the coroutine
   * @return A command with no mechanism requirements and the specified name and body
   */
  public static Command create(String name, Consumer<Coroutine> body) {
    return Command.noRequirements(body).named(name);
  }

  /**
   * Creates a command requiring the given mechanism(s).
   *
   * @param mechanism The mechanism(s) required by the command
   * @param name The name of the command
   * @param body The body of the command, executed by the coroutine
   * @return A command requiring the specified mechanism(s) with the given name and body
   */
  public static Command create(Mechanism mechanism, String name, Consumer<Coroutine> body) {
    return Command.requiring(mechanism).executing(body).named(name);
  }

  /**
   * Creates an empty command that does nothing.
   *
   * @return An empty command that does nothing
   */
  public static Command emptyCommand() {
    return instant(() -> {}, "Empty Command");
  }
}
