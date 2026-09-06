package com.team2052.lib.input;

import java.util.ArrayList;
import java.util.List;
import org.wpilib.command3.Scheduler;
import org.wpilib.command3.Trigger;
import org.wpilib.command3.button.CommandJoystick;
import org.wpilib.command3.button.JoystickButton;
import org.wpilib.command3.button.POVButton;
import org.wpilib.driverstation.POVDirection;

public class JoinedCommandJoystick extends CommandJoystick {

  private List<Trigger> createdTriggers = new ArrayList<>();

  public JoinedCommandJoystick(int port) {
    super(port);
  }

  public JoinedCommandJoystick(int port, Scheduler scheduler) {
    super(scheduler, port);
  }

  public void clearAllButtonBindings() {
    for (Trigger trigger : createdTriggers) {
      trigger.unbind();
    }

    createdTriggers = new ArrayList<>();
  }

  public Trigger button(int num) {
    Trigger button = new JoystickButton(getJoystick(), num);
    createdTriggers.add(button);
    return button;
  }

  public Trigger button(POVDirection direction) {
    Trigger button = new POVButton(getJoystick(), direction);
    createdTriggers.add(button);
    return button;
  }

  public double getRawAxis(int axis) {
    return getJoystick().getRawAxis(axis);
  }
}
