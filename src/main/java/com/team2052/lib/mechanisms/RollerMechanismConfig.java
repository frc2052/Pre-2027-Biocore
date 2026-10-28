package com.team2052.lib.mechanisms;

import static org.wpilib.units.Units.*;

import com.ctre.phoenix6.signals.NeutralModeValue;
import com.team2052.lib.mechanisms.ServoMechanismConfig.TalonFXConfig;
import lombok.Getter;
import lombok.Setter;
import org.wpilib.units.measure.AngularVelocity;
import org.wpilib.units.measure.Current;
import org.wpilib.units.measure.Time;

public class RollerMechanismConfig {

  public static final RollerMechanismConfig DEFAULT = new RollerMechanismConfig();

  @Getter @Setter public String name = "UNNAMED_MOTOR";
  @Getter @Setter public TalonFXConfig leaderTalonFXConstants = new TalonFXConfig();
  @Getter @Setter public TalonFXConfig[] followerTalonFXConstants = new TalonFXConfig[0];

  @Getter @Setter public NeutralModeValue neutralMode = NeutralModeValue.Coast;

  // Used for finding end angular velocity from sensor velocity
  @Getter @Setter public double sensorToMechanismRatio = 1.0;

  @Getter @Setter public double slot0kP = 0;
  @Getter @Setter public double slot0kI = 0;
  @Getter @Setter public double slot0kD = 0;
  @Getter @Setter public double slot0kV = 0;
  @Getter @Setter public double slot0kA = 0;
  @Getter @Setter public double slot0kS = 0;
  @Getter @Setter public double slot0kDeadband = 0;
  @Getter @Setter public double rampRate = 0;

  @Getter @Setter public double slot1kP = 0;
  @Getter @Setter public double slot1kI = 0;
  @Getter @Setter public double slot1kD = 0;
  @Getter @Setter public double slot1kV = 0;
  @Getter @Setter public double slot1kA = 0;
  @Getter @Setter public double slot1kS = 0;

  @Getter @Setter public double motionMagicAcceleration = 0; // rps/s
  @Getter @Setter public double motionMagicJerk = 0; // rps/s/s
  @Getter @Setter public double motionMagicFeedForward = 0;

  @Getter @Setter public Current supplyCurrentLimit = Amps.of(60);
  @Getter @Setter public Current supplyCurrentThreshold = Amps.of(60);
  @Getter @Setter public Time supplyCurrentTimeout = Seconds.of(0.0);
  @Getter @Setter public boolean enableSupplyCurrentLimit = false;

  @Getter @Setter public Current statorCurrentLimit = Amps.of(40);
  @Getter @Setter public boolean enableStatorCurrentLimit = false;

  @Getter @Setter public double followerRateHz = 20;

  /** Leave 0 for no max velocity, However is required for percent control */
  @Getter @Setter public AngularVelocity maxAngularVelocity = RotationsPerSecond.of(0);

  @Getter @Setter public boolean counterClockwisePositive = false;

  /**
   * Sets the proportional gain for the motor controller.
   *
   * <p>The units for this gain is dependent on the control mode. Since this gain is multiplied by
   * error in the input, the units should be defined as units of output per unit of input error.
   *
   * @param kP the proportional gain
   * @return this
   */
  public RollerMechanismConfig withKp(double kP) {
    this.slot0kP = kP;
    return this;
  }

  /**
   * Sets the integral gain for the motor controller.
   *
   * <p>The units for this gain is dependent on the control mode. Since this gain is multiplied by
   * error in the input integrated over time (in units of seconds), the units should be defined as
   * units of output per unit of integrated input error. For example, when controlling velocity
   * using a duty cycle closed loop, integrating velocity over time results in rps * s = rotations.
   * Therefore, the units for the integral gain will be duty cycle per rotation of accumulated
   * error, or 1/rot.
   *
   * @param kI the integral gain
   * @return this
   */
  public RollerMechanismConfig withKi(double kI) {
    this.slot0kI = kI;
    return this;
  }

  /**
   * Sets the derivative gain for the motor controller.
   *
   * <p>The units for this gain is dependent on the control mode. Since this gain is multiplied by
   * the rate of change of error in the input (in units of per second), the units should be defined
   * as units of output per unit of input error rate. For example, when controlling velocity using a
   * duty cycle closed loop, the units for the derivative gain will be duty cycle per (rps / s), or
   * s / rot.
   *
   * @param kD the derivative gain
   * @return this
   */
  public RollerMechanismConfig withKd(double kD) {
    this.slot0kD = kD;
    return this;
  }

  /**
   * Sets the velocity gain for the motor controller.
   *
   * @param kV the velocity gain
   * @return this
   */
  public RollerMechanismConfig withKv(double kV) {
    this.slot0kV = kV;
    return this;
  }

  /**
   * Sets the acceleration gain for the motor controller.
   *
   * @param kA the acceleration gain
   * @return this
   */
  public RollerMechanismConfig withKa(double kA) {
    this.slot0kA = kA;
    return this;
  }

  /**
   * Sets the static gain for the motor controller.
   *
   * @param kS the static gain
   * @return this
   */
  public RollerMechanismConfig withKs(double kS) {
    this.slot0kS = kS;
    return this;
  }
}
