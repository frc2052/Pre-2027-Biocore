package com.team2052.lib.subsystems;

import static org.wpilib.units.Units.Hertz;

import com.ctre.phoenix6.CANBus;
import com.ctre.phoenix6.configs.CANcoderConfiguration;
import lombok.Getter;
import lombok.Setter;
import org.wpilib.units.measure.Frequency;
import org.wpilib.util.Pair;

public class CANCoderConstants {
  @Getter @Setter public Pair<Integer, CANBus> id;
  @Getter @Setter public CANcoderConfiguration config = new CANcoderConfiguration();
  @Getter @Setter public Frequency statusSignalUpdateFrequency = Hertz.of(50);
}
