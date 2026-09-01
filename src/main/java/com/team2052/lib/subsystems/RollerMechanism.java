package com.team2052.lib.subsystems;

import static org.wpilib.units.Units.*;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.CoastOut;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.controls.MotionMagicVelocityTorqueCurrentFOC;
import com.ctre.phoenix6.controls.VelocityTorqueCurrentFOC;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.MotorAlignmentValue;
import com.team2052.lib.helpers.MathHelpers;
import org.wpilib.command3.Command;
import org.wpilib.units.measure.AngularVelocity;
import org.wpilib.units.measure.Current;
import org.wpilib.units.measure.Voltage;

/**
 * The RollerMechanism (previously RollerSubsystem) is an abstract class designed to be the
 * superclass of any other roller mechanisms. Generally, these other mechanisms can be things like
 * intake rollers, shooter rollers, or feeder rollers. If defining a mechinism that, when it boils
 * down to a motor running at a set velocity, this class is defined to be an abstraction of it.
 */
public abstract class RollerMechanism extends PeriodicMechanism {
  protected final TalonFX leader;
  protected final TalonFX[] followers;
  protected RollerMechanismConstants constants;
  protected final VelocityTorqueCurrentFOC velocityControl =
      new VelocityTorqueCurrentFOC(0).withSlot(0);
  protected final MotionMagicVelocityTorqueCurrentFOC motionMagicControl =
      new MotionMagicVelocityTorqueCurrentFOC(0).withSlot(1);
  protected final CoastOut coastControl = new CoastOut();
  protected final VoltageOut voltageControl = new VoltageOut(0);
  protected TalonFXConfiguration leaderConfig;
  protected TalonFXConfiguration[] followerConfigs;
  protected AngularVelocity goalVelocity = RotationsPerSecond.of(0);

  private final StatusSignal<AngularVelocity> velocitySignal;
  private final StatusSignal<Current> torqueCurrentSignal;
  private final BaseStatusSignal[] signals;

  public RollerMechanism(RollerMechanismConstants constants) {
    super(constants.name);
    this.constants = constants;

    leader = new TalonFX(constants.leaderTalonFXConstants.id, constants.leaderTalonFXConstants.bus);

    leaderConfig = new TalonFXConfiguration();

    leaderConfig.Feedback.SensorToMechanismRatio = constants.sensorToMechanismRatio;

    leaderConfig.Slot0.kP = constants.slot0kP;
    leaderConfig.Slot0.kI = constants.slot0kI;
    leaderConfig.Slot0.kD = constants.slot0kD;
    leaderConfig.Slot0.kV = constants.slot0kV;
    leaderConfig.Slot0.kA = constants.slot0kA;
    leaderConfig.Slot0.kS = constants.slot0kS;

    leaderConfig.Slot1.kP = constants.slot1kP;
    leaderConfig.Slot1.kI = constants.slot1kI;
    leaderConfig.Slot1.kD = constants.slot1kD;
    leaderConfig.Slot1.kV = constants.slot1kV;
    leaderConfig.Slot1.kA = constants.slot1kA;
    leaderConfig.Slot1.kS = constants.slot1kS;

    leaderConfig.MotionMagic.MotionMagicAcceleration = constants.motionMagicAcceleration;
    leaderConfig.MotionMagic.MotionMagicJerk = constants.motionMagicJerk;

    motionMagicControl.FeedForward = constants.motionMagicFeedForward;

    leaderConfig.MotorOutput.Inverted =
        (constants.counterClockwisePositive
            ? InvertedValue.CounterClockwise_Positive
            : InvertedValue.Clockwise_Positive);
    leaderConfig.MotorOutput.NeutralMode = constants.neutralMode;

    leaderConfig.CurrentLimits.SupplyCurrentLimit = constants.supplyCurrentLimit.in(Amps);
    leaderConfig.CurrentLimits.SupplyCurrentLimitEnable = constants.enableSupplyCurrentLimit;
    leaderConfig.CurrentLimits.StatorCurrentLimit = constants.statorCurrentLimit.in(Amps);
    leaderConfig.CurrentLimits.StatorCurrentLimitEnable = constants.enableStatorCurrentLimit;

    leaderConfig.OpenLoopRamps.DutyCycleOpenLoopRampPeriod = constants.rampRate;
    leaderConfig.OpenLoopRamps.VoltageOpenLoopRampPeriod = constants.rampRate;
    leaderConfig.OpenLoopRamps.TorqueOpenLoopRampPeriod = constants.rampRate;

    leaderConfig.ClosedLoopRamps.DutyCycleClosedLoopRampPeriod = constants.rampRate;
    leaderConfig.ClosedLoopRamps.VoltageClosedLoopRampPeriod = constants.rampRate;
    leaderConfig.ClosedLoopRamps.TorqueClosedLoopRampPeriod = constants.rampRate;

    followers = new TalonFX[constants.followerTalonFXConstants.length];
    followerConfigs = new TalonFXConfiguration[constants.followerTalonFXConstants.length];

    for (int i = 0; i < followers.length; i++) {
      int lID = constants.leaderTalonFXConstants.id; // Leader ID
      int fID = constants.followerTalonFXConstants[i].id; // Follower ID
      CANBus fBus = constants.followerTalonFXConstants[i].bus; // Follower CAN bus
      MotorAlignmentValue fAligned =
          constants
              .followerTalonFXConstants[i]
              .alignmentValue; // If the motors rotate in the same direction or not

      if (!fBus.equals(constants.leaderTalonFXConstants.bus)) {
        throw new RuntimeException("Leader and Follower TalonFXs must be on the same CAN bus.");
      }

      @SuppressWarnings("resource")
      TalonFX followerMotor = new TalonFX(fID, fBus);
      followerMotor.clearStickyFaults();
      followerMotor.setControl(
          new Follower(lID, fAligned).withUpdateFreqHz(constants.followerRateHz));

      followers[i] = followerMotor;
      TalonFXConfiguration followerConfig = new TalonFXConfiguration();
      followerMotor.getConfigurator().refresh(followerConfig);
      followerConfig.MotorOutput.NeutralMode = constants.neutralMode;
      followerConfig.MotorOutput.Inverted =
          (constants.counterClockwisePositive
              ? InvertedValue.CounterClockwise_Positive
              : InvertedValue.Clockwise_Positive);
      followerConfig.CurrentLimits.SupplyCurrentLimit = constants.supplyCurrentLimit.in(Amps);
      followerConfig.CurrentLimits.SupplyCurrentLimitEnable = constants.enableSupplyCurrentLimit;
      followerConfig.CurrentLimits.StatorCurrentLimit = constants.statorCurrentLimit.in(Amps);
      followerConfig.CurrentLimits.StatorCurrentLimitEnable = constants.enableStatorCurrentLimit;
      followerMotor.getConfigurator().apply(followerConfig);
      followerConfigs[i] = followerConfig;
      followerMotor.optimizeBusUtilization();
    }

    leader.getConfigurator().apply(leaderConfig);

    leader.optimizeBusUtilization();

    velocitySignal = leader.getVelocity();
    torqueCurrentSignal = leader.getTorqueCurrent();
    signals = new BaseStatusSignal[] {velocitySignal, torqueCurrentSignal};
    BaseStatusSignal.setUpdateFrequencyForAll(200.0, signals);
  }

  /**
   * Get the current velocity of the leader motor
   *
   * @return {@link AngularVelocity} of the roller motors. Is adjusted by the provided gear ratio.
   */
  public AngularVelocity getVelocity() {
    return velocitySignal.getValue();
  }

  /** Sets the motor to coast mode. */
  public void setCoastOut() {
    leader.setControl(coastControl);
  }

  /**
   * Set the goal velocity of the roller subsystem. Will be clamped to the max velocity set in
   * constants if it is not 0.
   *
   * @param goalVelocity The desired {@link AngularVelocity} for the roller subsystem
   */
  public void setGoalVelocityTorque(AngularVelocity goalVelocity) {
    this.goalVelocity = goalVelocity;
    if (constants.maxAngularVelocity.in(RotationsPerSecond) != 0) {
      this.goalVelocity =
          RotationsPerSecond.of(
              MathHelpers.clamp(
                  goalVelocity.in(RotationsPerSecond),
                  constants.maxAngularVelocity.unaryMinus().in(RotationsPerSecond),
                  constants.maxAngularVelocity.in(RotationsPerSecond)));
    }
    leader.setControl(velocityControl.withVelocity(this.goalVelocity));
    // Logger.recordOutput(
    // constants.name + "/Goal Velocity RPS", this.goalVelocity.in(RotationsPerSecond));
  }

  /**
   * Sets the goal velocity of the roller motors with {@link MotionMagicVelocityTorqueCurrentFOC}
   * control system.
   *
   * @param goalVelocity the goal {@link AngularVelocity}. Is clamped to the max angualr velocity
   *     provided in the provided constants.
   */
  public void setGoalVelocityMotionMagic(AngularVelocity goalVelocity) {
    this.goalVelocity = goalVelocity;
    if (constants.maxAngularVelocity.in(RotationsPerSecond) != 0) {
      this.goalVelocity =
          RotationsPerSecond.of(
              MathHelpers.clamp(
                  goalVelocity.in(RotationsPerSecond),
                  constants.maxAngularVelocity.unaryMinus().in(RotationsPerSecond),
                  constants.maxAngularVelocity.in(RotationsPerSecond)));
    }
    leader.setControl(motionMagicControl.withVelocity(goalVelocity));
  }

  /**
   * Set the motor output as a percentage of the max velocity defined in constants. Will be clamped
   * to the max velocity if it is not 0.
   *
   * @param pct The desired motor output percentage, between -1 and 1
   */
  public void setMotorPct(double pct) {
    setGoalVelocityTorque(constants.maxAngularVelocity.times(pct));
  }

  /**
   * Runs the roller motors through the {@link VoltageOut} control mode.
   *
   * @param volts The {@link Voltage} input. Typically capped at 12-13 volts which is the normal
   *     voltage of the robots battery.
   */
  public void setOpenLoop(Voltage volts) {
    leader.setControl(voltageControl.withOutput(volts));
  }

  /**
   * Runs the roller motors through the {@link DutyCycleOut} control mode.
   *
   * @param output The percent output. Limited between -1 and 1. Percent is applied to supply
   *     voltage and such will NOT allow for a consistant way to run the motors at a set velocity.
   */
  public void setOpenLoop(double output) {
    leader.setControl(new DutyCycleOut(output).withEnableFOC(true));
  }

  /** Stop the roller motors by setting the goal velocity to 0. */
  public void stopMotor() {
    setOpenLoop(Volts.of(0));
    goalVelocity = RotationsPerSecond.of(0);
  }

  /** Reverse the direction of the roller motors by negating the current goal velocity. */
  public void reverseMotor() {
    setGoalVelocityTorque(goalVelocity.unaryMinus());
  }

  /**
   * Check if the roller subsystem is at the goal velocity within a specified margin.
   *
   * @param margin The allowable margin of error for the velocity
   * @return true if the current velocity is within the margin of the goal velocity, false otherwise
   */
  public boolean isAtGoalVelocity(AngularVelocity margin) {
    return Math.abs(getVelocity().in(RotationsPerSecond) - goalVelocity.in(RotationsPerSecond))
        < margin.in(RotationsPerSecond);
  }

  /**
   * Check if the roller subsystem is at the goal velocity within a specified percentage margin.
   *
   * @param pctMargin The allowable margin of error for the velocity as a percentage of max velocity
   * @return true if the current velocity is within the margin of the goal velocity, false otherwise
   */
  public boolean isAtGoalVelocity(double pctMargin) {
    return isAtGoalVelocity(constants.maxAngularVelocity.times(pctMargin));
  }

  /**
   * Check if the roller subsystem is at a specified velocity within a specified margin.
   *
   * @param goalVelocity The {@link AngularVelocity} to check against
   * @param margin The allowable margin of error for the velocity
   * @return true if the current velocity is within the margin of the specified velocity, false
   *     otherwise
   */
  public boolean isAtVelocity(AngularVelocity goalVelocity, AngularVelocity margin) {
    System.out.println(
        "shooter param v:"
            + getVelocity().in(RotationsPerSecond)
            + " g: "
            + goalVelocity.in(RotationsPerSecond));
    return Math.abs(getVelocity().in(RotationsPerSecond) - goalVelocity.in(RotationsPerSecond))
        < margin.in(RotationsPerSecond);
  }

  /**
   * Check if the roller subsystem is at a specified velocity within a specified percentage margin.
   *
   * @param pctGoalVelocity The velocity to check against as a percentage of max velocity
   * @param pctMargin The allowable margin of error for the velocity as a percentage of max velocity
   * @return true if the current velocity is within the margin of the specified velocity, false
   *     otherwise
   */
  public boolean isAtVelocity(double pctGoalVelocity, double pctMargin) {
    return isAtVelocity(
        constants.maxAngularVelocity.times(pctGoalVelocity),
        constants.maxAngularVelocity.times(pctMargin));
  }

  /**
   * Check if the roller subsystem is stopped (at 0 velocity) within a specified margin.
   *
   * @param margin The allowable margin of error for the velocity
   * @return true if the current velocity is within the margin of 0, false otherwise
   */
  public boolean isStopped(AngularVelocity margin) {
    return isAtVelocity(RotationsPerSecond.of(0), margin);
  }

  /**
   * Create a command that runs the roller at a specified velocity until the command is interrupted,
   * at which point it will stop the motor.
   *
   * @param velocity The desired {@link AngularVelocity} for the roller subsystem
   * @return A Command that runs the roller at the specified {@link AngularVelocity}
   */
  public Command runAtVelocityCommand(AngularVelocity velocity) {
    return run(coroutine -> {
          setGoalVelocityTorque(velocity);
          coroutine.park();
        })
        .whenCanceled(this::stopMotor)
        .named(getName() + " Run at Percent Command");
    // Commands v2 implementation:
    // return Commands.runEnd(() -> setGoalVelocityTorque(velocity), () -> stopMotor(), this);
  }

  /**
   * Create a command that runs the roller at a specified percentage of max velocity until the
   * command is interrupted, at which point it will stop the motor.
   *
   * @param pct The desired motor output percentage, between -1 and 1
   * @return A Command that runs the roller at the specified percentage of max velocity
   */
  public Command runAtPctCommand(double pct) {
    return run(coroutine -> {
          setMotorPct(pct);
          coroutine.park();
        })
        .whenCanceled(this::stopMotor)
        .named(getName() + " Run at Percent Command");
    // Commands v2 implementation:
    // return Commands.runEnd(() -> setMotorPct(pct), () -> stopMotor(), this);
  }

  @Override
  public void inputPeriodic() {
    BaseStatusSignal.refreshAll(signals);
  }
}
