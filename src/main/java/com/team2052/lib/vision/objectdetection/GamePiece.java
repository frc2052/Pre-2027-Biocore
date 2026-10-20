package com.team2052.lib.vision.objectdetection;

import static org.wpilib.units.Units.Seconds;

import lombok.Getter;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Pose3d;
import org.wpilib.system.Timer;
import org.wpilib.units.measure.Distance;
import org.wpilib.units.measure.Time;

/** An abstract class designed to be extended with special qualities of each game piece. */
public abstract class GamePiece {

  @Getter private Pose3d gamePiecePose;
  @Getter private Time timestamp;

  public GamePiece(Pose3d gamePiecePose, Time timestamp) {
    this.gamePiecePose = gamePiecePose;
    this.timestamp = timestamp;
  }

  public GamePiece(Pose3d gamePiecePose) {
    this(gamePiecePose, Seconds.of(Timer.getMonotonicTimestamp()));
  }

  public GamePiece(Pose2d gamePiecePose2d, Time timestamp) {
    this(new Pose3d(gamePiecePose2d), timestamp);
  }

  public GamePiece(Pose2d gamePiecePose2d) {
    this(gamePiecePose2d, Seconds.of(Timer.getMonotonicTimestamp()));
  }

  public GamePiece() {
    this(new Pose3d(), Seconds.of(0));
  }

  public Pose2d getPoseIn2d() {
    return gamePiecePose.toPose2d();
  }

  /**
   * Gets the approximate radius of the game piece. Used to determine weather a 2 registered
   * detections are the same piece.
   *
   * @return The approximate radius of the game piece.
   */
  public abstract Distance getApproxRadius();

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    GamePiece that = (GamePiece) obj;
    return gamePiecePose.equals(that.gamePiecePose) && timestamp.equals(that.timestamp);
  }
}
