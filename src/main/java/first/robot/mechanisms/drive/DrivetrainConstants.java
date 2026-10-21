package first.robot.mechanisms.drive;

import static org.wpilib.units.Units.*;

import com.team2052.lib.util.FeedForwardConfiguration;
import com.team2052.lib.util.PIDConfiguration;
import com.team2052.lib.util.PIDFFConfiguration;
import first.robot.mechanisms.drive.ctre.CommandSwerveDrivetrain;
import first.robot.mechanisms.drive.ctre.generated.TunerConstants;
import org.wpilib.math.linalg.VecBuilder;
import org.wpilib.math.linalg.Vector;
import org.wpilib.math.numbers.N3;
import org.wpilib.units.measure.Angle;
import org.wpilib.units.measure.AngularVelocity;
import org.wpilib.units.measure.Current;
import org.wpilib.units.measure.Distance;
import org.wpilib.units.measure.LinearVelocity;
import org.wpilib.units.measure.Mass;

public class DrivetrainConstants {
  public static final CommandSwerveDrivetrain TUNER_DRIVETRAIN_CONSTANTS =
      TunerConstants.createDrivetrain();
  /*
   * If using the generator, the order in which modules are constructed is
   * Front Left, Front Right, Back Left, Back Right. This means if you need
   * the Back Left module, call {@code getModule(2);} to get the 3rd index
   * (0-indexed) module, corresponding to the Back Left module.
   */

  public static final LinearVelocity DRIVE_MAX_SPEED = TunerConstants.kSpeedAt12Volts;
  public static final AngularVelocity DRIVE_MAX_ANGULAR_RATE =
      RadiansPerSecond.of(DRIVE_MAX_SPEED.in(MetersPerSecond) / 0.42);

  public static final Current DRIVE_CURRENT_LIMIT_AMPS = Amps.of(80.0);

  public static final Distance WHEEL_RADIUS =
      Meters.of(TUNER_DRIVETRAIN_CONSTANTS.getModuleConstants()[0].WheelRadius);
  // Left-to-right distance between drivetrain wheels
  public static final Distance DRIVETRAIN_TRACK_WIDTH = Inches.of(23.5);
  // Front-to-back distance between drivetrain wheels
  public static final Distance DRIVETRAIN_WHEELBASE = Inches.of(23.5);

  public static final Mass DRIVETRAIN_MASS = Pounds.of(107.1);

  public static final Vector<N3> ODOMETRY_STDDEV = VecBuilder.fill(0.003, 0.003, 0.002);
  public static final double XY_STDDEV_COEFFICIENT = 1;
  public static final double ANGLE_STDDEV_COEFFICIENT = 1;

  public static final Angle HEADING_TOLERANCE = Degrees.of(3);

  public static final double DRIVE_DEADBAND = 0.05;

  // TODO: adjust these tolerances based on actual drivetrain performance
  public static final Distance XY_TOLERANCE = Centimeters.of(5);
  public static final Angle ROTATION_TOLERANCE = Degrees.of(3);

  public static final PIDFFConfiguration XY_PIDFF;
  public static final PIDFFConfiguration ROTATION_PIDFF;


  static {
    XY_PIDFF =
        new PIDFFConfiguration(
            new PIDConfiguration(0, 0, 0, 0, 0), new FeedForwardConfiguration(0, 0, 0));
    ROTATION_PIDFF =
        new PIDFFConfiguration(
            new PIDConfiguration(0, 0, 0, 0, 0), new FeedForwardConfiguration(0, 0, 0));
  }


   //TODO: Choreo lib configs.
   
    public static final PIDFFConfiguration xConfiguration = new PIDFFConfiguration(
            new PIDConfiguration(0, 0, 0, 0, 0), new FeedForwardConfiguration(0, 0, 0));

    public static final PIDFFConfiguration yConfiguration = new PIDFFConfiguration(
            new PIDConfiguration(0, 0, 0, 0, 0), new FeedForwardConfiguration(0, 0, 0));

    public static final PIDFFConfiguration headingConfiguration = new PIDFFConfiguration(
            new PIDConfiguration(0, 0, 0, 0, 0), new FeedForwardConfiguration(0, 0, 0));


}
