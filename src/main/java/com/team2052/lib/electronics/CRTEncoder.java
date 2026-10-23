package com.team2052.lib.electronics;

import static org.wpilib.units.Units.Radians;
import static org.wpilib.units.Units.Rotations;
import static org.wpilib.units.Units.Seconds;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.hardware.CANcoder;
import lombok.Getter;
import lombok.Setter;
import org.wpilib.math.filter.LinearFilter;
import org.wpilib.math.util.MathUtil;
import org.wpilib.units.measure.Angle;
import org.wpilib.util.Pair;

/**
 * CRTEncoder represents 2 CANcoders that are mounted on gears that both interlock with a larger
 * gear. The measure of that larger gear is determined via using the Chinese Remainder Theorem. The
 * number of teeth on the two encoders should be co-prime.
 *
 * <p>https://www.chiefdelphi.com/t/how-to-chinese-remainder-theorem-for-gears-turrets/512876
 */
public class CRTEncoder {

  private final CANcoder encoderA;
  private final CANcoder encoderB;

  @Getter private final CRTEncoderConfig config;

  private final LinearFilter filter;

  @Getter @Setter private Angle zeroPosition;

  /**
   * Creates a new CRTEncoder with the given configuration.
   *
   * @param config The configuration for the CRTEncoder.
   */
  public CRTEncoder(CRTEncoderConfig config) {
    if (config.aTeeth == config.bTeeth)
      throw new IllegalArgumentException(
          "The number of teeth on the CRT encoders can not be equal");
    if (config.aTeeth <= config.measuredGearTeeth || config.bTeeth <= config.measuredGearTeeth)
      throw new IllegalArgumentException(
          "The number of teeth on the measured gear must be less than the number of teeth on the CRT encoders");

    this.config = config;

    encoderA =
        new CANcoder(
            this.config.encoderAConstants.id.getFirst(),
            this.config.encoderAConstants.id.getSecond());
    encoderB =
        new CANcoder(
            this.config.encoderBConstants.id.getFirst(),
            this.config.encoderBConstants.id.getSecond());

    encoderA.getConfigurator().apply(this.config.encoderAConstants.config);
    encoderB.getConfigurator().apply(this.config.encoderBConstants.config);

    filter = LinearFilter.movingAverage(config.filterTaps);

    zeroPosition = config.zeroPosition;
  }

  /**
   * Gets the value of the measured gear via looking at the two offset encoders. Applies zero offset
   * and runs through filter.
   *
   * @return The value of the measured gear
   */
  public Angle getMeasuredGearAngle() {
    return zeroPosition.plus(getNonZeroMeasuredGearAngle());
  }

  /**
   * Gets a time synced pair of the two encoder values.
   *
   * @return A pair of the two encoder values, A is first, B is second.
   */
  public Pair<Angle, Angle> getEncoderABAngle() {
    StatusSignal<Angle> cancoderAPositionSignal = encoderA.getAbsolutePosition(false);
    StatusSignal<Angle> cancoderBPositionSignal = encoderB.getAbsolutePosition(false);

    BaseStatusSignal.waitForAll(
        config.loopPeriod.in(Seconds), cancoderAPositionSignal, cancoderBPositionSignal);
    return new Pair<Angle, Angle>(
        cancoderAPositionSignal.getValue(), cancoderBPositionSignal.getValue());
  }

  /**
   * Gets the value of the measured gear with no zero applied, filter is still applied.
   *
   * @return The non-zero value of the measured gear.
   */
  public Angle getNonZeroMeasuredGearAngle() {
    int aTeeth = config.aTeeth;
    int bTeeth = config.bTeeth;
    int measuredGearTeeth = config.measuredGearTeeth;

    Pair<Angle, Angle> encoderABAngle = getEncoderABAngle();

    // get absolute positions of both encoders
    Angle aAngle = encoderABAngle.getFirst();
    Angle bAngle = encoderABAngle.getSecond();

    double ratioAtoB = ((double) aTeeth) / bTeeth;

    // predict the angle if there have been no rotations of encoder A
    Angle predictedAngleB = aAngle.times(ratioAtoB);
    Angle differenceInPredictedAngle = bAngle.minus(predictedAngleB);

    Angle differenceWrappedAngle =
        Radians.of(MathUtil.angleModulus(differenceInPredictedAngle.in(Radians)));

    Angle errorPerGearARotation = Rotations.of(ratioAtoB);

    long numGearARotations =
        Math.round(
            differenceWrappedAngle.in(Rotations) / (errorPerGearARotation.in(Rotations) - 1.0));

    // find the angle of the measured gear
    Angle gearAAngle = encoderABAngle.getFirst().plus(Rotations.of(numGearARotations));
    Angle measuredGearAngle = gearAAngle.times((double) aTeeth / measuredGearTeeth);

    // apply through the filter and then invert it as the measured gear runs in reverse
    return Rotations.of(filter.calculate(measuredGearAngle.in(Rotations))).unaryMinus();
  }

  /**
   * Gets that angle of the measured gear with an already filled filter. Should only be used when
   * re-zeroing or on boot up as a way to establish an initial value.
   *
   * @return The measured gear angle.
   */
  public Angle getAngleWithFilledFilter() {
    Angle angle = Rotations.of(0);
    for (int i = 0; i < config.filterTaps; i++) {
      angle = getMeasuredGearAngle();
    }
    return angle;
  }

  /**
   * Gets the value of encoder A.
   *
   * @return The absolute position of encoder A.
   */
  public Angle getEncoderAAngle() {
    return encoderA.getAbsolutePosition().getValue();
  }

  /**
   * Gets the value of encoder A.
   *
   * @return The absolute position of encoder A.
   */
  public Angle getEncoderBAngle() {
    return encoderB.getAbsolutePosition().getValue();
  }

  /** Zeros the CRT Encoder. */
  public void zero() {
    setZeroPosition(getMeasuredGearAngle());
  }
}
