package com.team2052.lib.input;

import org.wpilib.command3.Trigger;
import org.wpilib.driverstation.POVDirection;

public class T16000MJoystick extends JoinedCommandJoystick {
  public T16000MJoystick(int port) {
    super(port);
  }

  // --- Stick Buttons ---
  public Trigger frontTrigger() {
    return button(1);
  }

  public Trigger middleThumbButton() {
    return button(2);
  }

  public Trigger leftThumbButton() {
    return button(3);
  }

  public Trigger rightThumbButton() {
    return button(4);
  }

  // --- Base Buttons (Left Side) ---
  public Trigger leftBaseTopLeft() {
    return button(11);
  }

  public Trigger leftBaseTopMiddle() {
    return button(12);
  }

  public Trigger leftBaseTopRight() {
    return button(13);
  }

  public Trigger leftBaseBottomLeft() {
    return button(16);
  }

  public Trigger leftBaseBottomMiddle() {
    return button(15);
  }

  public Trigger leftBaseBottomRight() {
    return button(14);
  }

  // --- Base Buttons (Right Side) ---
  public Trigger rightBaseTopLeft() {
    return button(7);
  }

  public Trigger rightBaseTopMiddle() {
    return button(6);
  }

  public Trigger rightBaseTopRight() {
    return button(5);
  }

  public Trigger rightBaseBottomLeft() {
    return button(8);
  }

  public Trigger rightBaseBottomMiddle() {
    return button(9);
  }

  public Trigger rightBaseBottomRight() {
    return button(10);
  }

  // --- Axes ---
  public double getX() {
    return getRawAxis(0);
  }

  public double getY() {
    return getRawAxis(1);
  }

  public double getTwist() {
    return getRawAxis(2);
  }

  public double getSlider() {
    return getRawAxis(3);
  }

  // --- POV ---
  public Trigger povUp() {
    return button(POVDirection.UP);
  }

  public Trigger povDown() {
    return button(POVDirection.DOWN);
  }

  public Trigger povLeft() {
    return button(POVDirection.LEFT);
  }

  public Trigger povRight() {
    return button(POVDirection.RIGHT);
  }

  public Trigger povUpLeft() {
    return button(POVDirection.UP_LEFT);
  }

  public Trigger povUpRight() {
    return button(POVDirection.UP_RIGHT);
  }

  public Trigger povDownLeft() {
    return button(POVDirection.DOWN_LEFT);
  }

  public Trigger povDownRight() {
    return button(POVDirection.DOWN_RIGHT);
  }
}
