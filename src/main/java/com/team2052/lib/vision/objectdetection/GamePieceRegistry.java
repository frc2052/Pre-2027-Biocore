package com.team2052.lib.vision.objectdetection;

import static org.wpilib.units.Units.Meters;
import static org.wpilib.units.Units.Seconds;

import com.team2052.lib.regions.Region;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import org.wpilib.math.geometry.Translation2d;
import org.wpilib.system.Timer;
import org.wpilib.units.measure.Time;

public class GamePieceRegistry<G extends GamePiece> {

  @Getter private List<G> gamePieces;

  public GamePieceRegistry() {
    gamePieces = new ArrayList<>();
  }

  public void empty() {
    gamePieces = new ArrayList<>();
  }

  /**
   * Returns the closest game piece to the specified location.
   *
   * @param location The location to find the closest piece to.
   * @return The closest game piece, or an empty Optional if no pieces are registered.
   */
  public Optional<G> getClosestPiece(Translation2d location) {
    if (gamePieces.isEmpty()) return Optional.empty();
    G closest = gamePieces.get(0);
    double closestDistance = closest.getPoseIn2d().getTranslation().getDistance(location);
    for (G piece : gamePieces) {
      double distance = piece.getPoseIn2d().getTranslation().getDistance(location);
      if (distance < closestDistance) {
        closest = piece;
        closestDistance = distance;
      }
    }
    return Optional.of(closest);
  }

  /**
   * Gets the n closest game pieces to the specified location.
   *
   * @param location The location to find the closest pieces to.
   * @param n The number of closest pieces to return.
   * @return An array containing the n closest game pieces. Will not nessisarily be of size n if
   *     there are less than n values in the array to check.
   */
  public GamePiece[] getNClosestPieces(Translation2d location, int n) {
    if (n <= 0) return new GamePiece[0];
    if (gamePieces.size() < n)
      return getNClosestPieces(
          location, gamePieces.size()); // limited recursion, only able to run once

    GamePiece[] closestPieces = gamePieces.toArray(new GamePiece[0]);

    // Uses a built in sorting algorithm to arrange the array in order
    Arrays.sort(
        closestPieces,
        (a, b) -> {
          double distA = a.getPoseIn2d().getTranslation().getDistance(location);
          double distB = b.getPoseIn2d().getTranslation().getDistance(location);
          return Double.compare(distA, distB);
        });

    // returns the first n values in that array.
    return Arrays.copyOf(closestPieces, n);
  }

  /**
   * Registers a game piece with the registry.
   *
   * @param piece The piece to register.
   */
  public void registerPiece(G piece) {
    for (G compPiece : gamePieces) {
      double dist =
          piece
              .getGamePiecePose()
              .getTranslation()
              .getDistance(compPiece.getGamePiecePose().getTranslation());
      double compRad = compPiece.getApproxRadius().in(Meters);
      double pieceRad = piece.getApproxRadius().in(Meters);
      if (dist < compRad + pieceRad) { // Game pieces are the same.
        gamePieces.remove(compPiece);
      }
    }
    gamePieces.add(piece);
  }

  /**
   * Adds all of the game pieces to the registry.
   *
   * @param pieces The pieces to add.
   */
  public void registerPiece(G... pieces) {
    for (G piece : pieces) {
      registerPiece(piece);
    }
  }

  /**
   * Removes all game pieces that are considered "stale".
   *
   * @param staleQualifier How long ago until game pieces are considered "stale".
   */
  public void purge(Time staleQualifier) {
    double currentTimestamp = Timer.getMonotonicTimestamp();
    double minTimestamp = currentTimestamp - staleQualifier.in(Seconds);
    gamePieces.removeIf(g -> g.getTimestamp().in(Seconds) < minTimestamp);
  }

  /**
   * Removes all game pieces that are within the specified region.
   *
   * @param emptyRegion The region that should be empty
   */
  public void purge(Region emptyRegion) {
    gamePieces.removeIf(g -> emptyRegion.isPointInRegion(g.getPoseIn2d().getTranslation()));
  }
}
