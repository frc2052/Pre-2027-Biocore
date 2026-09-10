package com.team2052.lib.vision.questnav;

import static org.wpilib.units.Units.*;

import com.questnav.QuestNav;
import com.team2052.lib.helpers.CommandBuilder;
import com.team2052.lib.subsystems.PeriodicMechanism;
import lombok.Getter;
import org.wpilib.command3.Trigger;
import org.wpilib.driverstation.DriverStationDisplay;
import org.wpilib.driverstation.DriverStationErrors;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Pose3d;
import org.wpilib.units.measure.Time;

public class QuestNavMechanism extends PeriodicMechanism {

  protected final QuestNav quest = new QuestNav();
  protected final QuestNavConstants constants;

  @Getter private QuestState lastState = new QuestState();

  public QuestNavMechanism(QuestNavConstants constants) {
    super(constants.name);
    this.constants = constants;
    quest.setPose(new Pose3d().transformBy(constants.questPose));

    addBatteryFlagWarning(50);
    addBatteryFlagWarning(20);
    addBatteryFlagWarning(10);
    addBatteryFlagWarning(5);

    quest.onConnected(() -> DriverStationDisplay.addLine("Quest Nav Connected"));
    quest.onDisconnected(() -> DriverStationErrors.reportWarning("Quest Nac Disconnected", false));
  }

  protected void addBatteryFlagWarning(int cutoff) {
    Trigger lowBatteryFlag = new Trigger(() -> lastState.batteryPct * 100 < cutoff);
    lowBatteryFlag.onTrue(
        CommandBuilder.instant(
            () ->
                DriverStationErrors.reportWarning("Quest Battery Less than " + cutoff + "%", false),
            name + " Battery Less Than " + cutoff));
  }

  public Pose3d questPoseToRobotPose(Pose3d pose) {
    return pose.transformBy(constants.questPose);
  }

  public Pose3d robotPoseToQuestPose(Pose3d pose) {
    return pose.transformBy(constants.questPose.inverse());
  }

  private void readAllUnreadFrames() {
    for (var frame : quest.getAllUnreadPoseFrames()) {
      if (frame.dataTimestamp() > lastState.dataTimestamp.in(Seconds)) {
        lastState.robotPose = questPoseToRobotPose(frame.questPose3d());
        lastState.questPose = frame.questPose3d();
        lastState.flatRobotPose = lastState.robotPose.toPose2d();
        lastState.dataTimestamp = Seconds.of(frame.dataTimestamp());
      }
    }
  }

  @Override
  public void inputPeriodic() {
    quest.commandPeriodic();

    lastState.isConnected = quest.isConnected();
    lastState.isTracking = quest.isTracking();

    if (lastState.isConnected && lastState.isTracking) {
      readAllUnreadFrames();
      lastState.latency = Milliseconds.of(quest.getLatency());
    }

    var batteryPercent = quest.getBatteryPercent();
    if (batteryPercent.isPresent()) {
      lastState.batteryPct = batteryPercent.getAsInt() / 100.0;
    }
  }

  @Override
  public void outputPeriodic() {}

  @Override
  public void loggingPeriodic() {}

  public class QuestState {
    public Pose3d robotPose = new Pose3d();
    public Pose2d flatRobotPose = new Pose2d();
    public Pose3d questPose = new Pose3d();
    public Time dataTimestamp = Seconds.of(0);
    public Time latency = Seconds.of(0);

    public boolean isConnected = false;
    public boolean isTracking = false;

    public double batteryPct = 1;
  }
}
