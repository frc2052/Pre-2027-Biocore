package first.robot;

import static org.wpilib.units.Units.*;

import org.wpilib.units.measure.*;

public class Constants {

  public static final Time MAIN_LOOP_PERIOD = Milliseconds.of(20);

  public static final class OpModeColors {
    public static final String MATCH_TELEOP = "#00ff51";
    public static final String NON_MATCH_TELEOP = "#500000";
    public static final String AUTO = "#ff0000";
    public static final String UTILITY = "#a200ff";
  }
}
