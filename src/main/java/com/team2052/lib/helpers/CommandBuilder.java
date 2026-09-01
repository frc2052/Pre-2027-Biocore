package com.team2052.lib.helpers;

import org.wpilib.command3.Command;
import org.wpilib.command3.NeedsNameBuilderStage;

public class CommandBuilder {

  public static NeedsNameBuilderStage instant(Runnable runnable) {
    return Command.noRequirements(
        coroutine -> {
          runnable.run();
        });
  }

  public static Command instant(Runnable runnable, String name) {
    return Command.noRequirements(
            coroutine -> {
              runnable.run();
            })
        .named(name);
  }
}
