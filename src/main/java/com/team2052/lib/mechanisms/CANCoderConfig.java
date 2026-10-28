package com.team2052.lib.mechanisms;

import static org.wpilib.units.Units.Hertz;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.configs.CANcoderConfiguration;
import lombok.Getter;
import lombok.Setter;
import org.wpilib.units.measure.Frequency;
import org.wpilib.util.Pair;

/**
 * CANCoderConfig
 *
 * <p>Example implementation:
 *
 * <pre>{@code
 * public static final CANCoderConfig CONFIG = CANCoderConfig.DEFAULT;
 *
 * static { // Anything within the static constructor runs before everything else, we use it here to define constants.
 *   CONFIG.id = new Pair<Integer, CANBus>(0, new CANBus("Bus Name"));
 *   CONFIG.statusSignalUpdateFrequency = Hertz.of(100);
 * }
 * }</pre>
 */
public class CANCoderConfig {

  public static final CANCoderConfig DEFAULT = new CANCoderConfig();

  /** Pair of a CANID and a CANBus for the CANCoder */
  @Getter @Setter public Pair<Integer, CANBus> id;

  /** The configuration for the CANCoder to be passed into upon its creation */
  @Getter @Setter public CANcoderConfiguration config = new CANcoderConfiguration();

  /** Update frequency for the CANCoder */
  @Getter @Setter public Frequency statusSignalUpdateFrequency = Hertz.of(50);

  /**
   * Sets the CANID for the CANCoder config.
   *
   * @param id The CANID to set.
   */
  public void setCANID(int id) {
    this.id = new Pair<Integer, CANBus>(id, this.id.getSecond());
  }

  /**
   * Sets the CANBus for the CANCoder config.
   *
   * @param bus The CANBus to set.
   */
  public void setCANBus(CANBus bus) {
    this.id = new Pair<Integer, CANBus>(this.id.getFirst(), bus);
  }

  /**
   * Gets the CANID for the CANCoder config.
   *
   * @return The CANID.
   */
  public int getCANID() {
    return id.getFirst();
  }

  /**
   * Gets the CANBus for the CANCoder config.
   *
   * @return The CANBus.
   */
  public CANBus getCANBus() {
    return id.getSecond();
  }

  /**
   * Sets the id pair for the config
   *
   * @param id The id to set.
   * @return The new config.
   */
  public CANCoderConfig withID(Pair<Integer, CANBus> id) {
    this.id = id;
    return this;
  }

  /**
   * Sets the CANID for the config.
   *
   * @param id The CANID to set.
   * @return The new config.
   */
  public CANCoderConfig withCANID(int id) {
    setCANID(id);
    return this;
  }

  /**
   * Sets the CANBus for the config.
   *
   * @param bus The CANBus to set.
   * @return The new config.
   */
  public CANCoderConfig withCANBus(CANBus bus) {
    setCANBus(bus);
    return this;
  }

  /**
   * Sets the CANCoder config for this config.
   *
   * @param config The config to set.
   * @return The new Config.
   */
  public CANCoderConfig withConfig(CANcoderConfiguration config) {
    this.config = config;
    return this;
  }

  /**
   * Sets the status signal update frequency for this config.
   *
   * @param statusSignalUpdateFrequency The frequency to set.
   * @return The new Config.
   */
  public CANCoderConfig withStatusSignalUpdateFrequency(Frequency statusSignalUpdateFrequency) {
    this.statusSignalUpdateFrequency = statusSignalUpdateFrequency;
    return this;
  }
}
