package com.team2052.lib.input;

import org.wpilib.command3.Scheduler;
import org.wpilib.command3.button.CommandJoystick;
import org.wpilib.driverstation.POVDirection;

public class ClearableCommandJoystick extends CommandJoystick {

    private static final int MAX_NUM_BUTTONS = 64;

    public ClearableCommandJoystick(int port) {
        super(port);
    }

    public ClearableCommandJoystick(int port, Scheduler scheduler) {
        super(scheduler, port);
    }

    public void clearAllButtonBindings() {
        for (int i = 1; i <= MAX_NUM_BUTTONS; i++) {
            button(i).unbind();
        }

        for (POVDirection d : POVDirection.values()) {
            pov(d).unbind();
        }
    }
    
}
