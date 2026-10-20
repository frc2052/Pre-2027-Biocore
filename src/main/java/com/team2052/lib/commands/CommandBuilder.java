package com.team2052.lib.commands;

import static org.wpilib.units.Units.Seconds;

import java.util.ArrayList;
import java.util.List;
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
   * @param runnable The runnable to execute immediately.
   * @return A {@link NeedsNameBuilderStage} of the command.
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
   * @param runnable The runnable to execute immediately.
   * @param name The name of the command.
   * @return An instant command with the specified runnable and name.
   */
  public static Command instant(Runnable runnable, String name) {
    return instant(runnable).named(name);
  }

  /**
   * Creates an instant command with the specified runnable and a required mechanism. Still needs to
   * be named.
   *
   * @param runnable The runnable to execute immediately.
   * @param mechanisms The required mechanism(s).
   * @return A {@link NeedsNameBuilderStage} of the command.
   */
  public static NeedsNameBuilderStage instant(Runnable runnable, Mechanism... mechanisms) {
    List<Mechanism> mechanismList = new ArrayList<>();

    for (Mechanism mechanism : mechanisms) {
      mechanismList.add(mechanism);
    }

    return Command.requiring(mechanismList)
        .executing(
            coroutine -> {
              runnable.run();
            });
  }

  /**
   * Creates an instant command with the specified runnable, name, and required mechanism(s).
   *
   * @param runnable The runnable to execute immediately.
   * @param name The name of the command.
   * @param mechanisms The required mechanism(s).
   * @return An instant command with the specified runnable, name, and required mechanism(s).
   */
  public static Command instant(Runnable runnable, String name, Mechanism... mechanisms) {
    return instant(runnable, mechanisms).named(name);
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
   * @param body The body of the command, executed by the coroutine
   * @param name The name of the command
   * @return A command with no mechanism requirements and the specified name and body
   */
  public static Command create(Consumer<Coroutine> body, String name) {
    return Command.noRequirements(body).named(name);
  }

  /**
   * Creates a command requiring the given mechanism(s).
   *
   * @param body The body of the command, executed by the coroutine
   * @param name The name of the command
   * @param mechanisms The mechanism(s) required by the command
   * @return A command requiring the specified mechanism(s) with the given name and body
   */
  public static Command create(Consumer<Coroutine> body, String name, Mechanism... mechanisms) {
    List<Mechanism> mechanismList = new ArrayList<>();

    for (Mechanism mechanism : mechanisms) {
      mechanismList.add(mechanism);
    }

    return Command.requiring(mechanismList).executing(body).named(name);
  }

  /**
   * Creates an empty command that does nothing and instantly finishes.
   *
   * @return An empty command that does nothing.
   */
  public static Command emptyInstantCommand() {
    return instant(() -> {}, "Empty Instant Command");
  }

  /**
   * Creates an empty command that never ends.
   *
   * @return An empty command that does nothing.
   */
  public static Command emptyRunningCommand() {
    return create(
        coroutine -> {
          while (true) {
            coroutine.yield();
          }
        },
        "Empty Running Command");
  }
}
