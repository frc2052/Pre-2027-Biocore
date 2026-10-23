package com.team2052.lib.electronics;

import static org.wpilib.units.Units.Milliseconds;
import static org.wpilib.units.Units.Rotations;

import com.team2052.lib.mechanisms.CANCoderConstants;
import org.wpilib.units.measure.Angle;
import org.wpilib.units.measure.Time;

public class CRTEncoderConfig {

  public CANCoderConstants encoderAConstants = new CANCoderConstants();
  public CANCoderConstants encoderBConstants = new CANCoderConstants();

  /** The number of teeth on encoder A, should be co-prime with encoder B. A is typically the larger gear */
  public int aTeeth = 1;
  /** The number of teeth on encoder B, should be co-prime with encoder A. B is typically the smaller gear */
  public int bTeeth = 1;
  /** The number of teeth on the measured gear */
  public int measuredGearTeeth = 1;
  /** The number of taps for the moving average filter */
  public int filterTaps = 5;

  public Time loopPeriod = Milliseconds.of(20);

  public Angle zeroPosition = Rotations.of(0);
}
