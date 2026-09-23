package com.team2052.lib.estimators;

import static org.wpilib.units.Units.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.kinematics.ChassisAccelerations;
import org.wpilib.math.kinematics.ChassisVelocities;
import org.wpilib.math.linalg.VecBuilder;
import org.wpilib.math.linalg.Vector;
import org.wpilib.math.numbers.N3;
import org.wpilib.units.measure.Time;
import org.wpilib.util.Pair;

import com.ctre.phoenix6.Utils;
import com.team2052.lib.geometry.ChassisJerks;
import com.team2052.lib.geometry.Vector2d;

import lombok.Getter;

public class PositionEstimator {

    // Poses represent a vector holding x, y, and theta
    @Getter public Pose2d lastPose = new Pose2d();
    @Getter public Vector<N3> lastPoseStdDev = VecBuilder.fill(0, 0, 0);
    @Getter public ChassisVelocities lastVelocity = new ChassisVelocities();
    @Getter public Vector<N3> lastVelocityStdDev = VecBuilder.fill(0, 0, 0);
    @Getter public ChassisAccelerations lastAcceleration = new ChassisAccelerations();
    @Getter public Vector<N3> lastAccelerationStdDev = VecBuilder.fill(0, 0, 0);
    @Getter public ChassisJerks lastJerk = new ChassisJerks();
    @Getter public Vector<N3> lastJerkStdDev = VecBuilder.fill(0, 0, 0);

    @Getter public Time lastTimestamp = Seconds.of(0);

    // First in the pair is the last given pose, the second is the updated pose after a new odometry position was assigned. Will be empty when initially assigned.
    private HashMap<String, Pair<Pose2d, Optional<Pair<Pose2d, Vector<N3>>>>> odometryLastPose = new HashMap<>();

    // a list of the odometries to be updated.
    private List<String> toUpdate = new ArrayList<>();

    /**
     * Registers a new odometry system
     * @param name The name of the system
     * @param initialPose The initial pose to register.
     */
    private void registerOdometry(String name, Pose2d initialPose) {
        odometryLastPose.put(name, new Pair<>(initialPose, Optional.empty()));
    }

    /**
     * Updates the pose estimate via a set of {@link PoseMeasurement} and a set of {@link OdometryMeasurement}.
     * @param visionMeasurements The measurements stemming from vision
     * @param odometryMeasurements The measurements stemming from an odometry system.
     */
    public void update(PoseMeasurement[] visionMeasurements, OdometryMeasurement[] odometryMeasurements) {
        List<PoseMeasurement> totalMeasurements = new ArrayList<>();

        for (PoseMeasurement m : visionMeasurements) {
            totalMeasurements.add(m);
        }

        for (OdometryMeasurement om : odometryMeasurements) {
            if (!odometryLastPose.containsKey(om.name)) {
                registerOdometry(om.name, om.measurement.pose);
                totalMeasurements.add(om.measurement);
            } else {
                Pair<Pose2d, Optional<Pair<Pose2d, Vector<N3>>>> mapPull = odometryLastPose.get(om.name);
                Pose2d lastPose = mapPull.getFirst();
                Pose2d lastEstimation = mapPull.getSecond().get().getFirst();
                Vector<N3> stddev = mapPull.getSecond().get().getSecond();

                double x = (om.measurement.pose.getX() - lastPose.getX()) + lastEstimation.getX();
                double y = (om.measurement.pose.getY() - lastPose.getY()) + lastEstimation.getY();
                double theta = (om.measurement.pose.getRotation().getRadians() - lastPose.getRotation().getRadians()) + lastEstimation.getRotation().getRadians();

                Pose2d actualMeasuredPose = new Pose2d(x, y, new Rotation2d(theta));
                Vector<N3> newStdDev = VecBuilder.fill(
                    om.measurement.poseStdDev.get(0) + stddev.get(0),
                    om.measurement.poseStdDev.get(1) + stddev.get(1),
                    om.measurement.poseStdDev.get(2) + stddev.get(2)
                );
                totalMeasurements.add(new PoseMeasurement(actualMeasuredPose, newStdDev, om.measurement.measurementDelay, om.measurement.measurementReceivedTimestamp));
            }

            toUpdate.add(om.name);
        }

        update(totalMeasurements.toArray(new PoseMeasurement[0]));
    }

    /**
     * Updates the pose estimate via a set of {@link PoseMeasurement}.
     * @param measurements the set of {@link PoseMeasurement} to use for the update.
     */
    public void update(PoseMeasurement... measurements) {
        List<PoseMeasurement> validMeasurements = new ArrayList<>();
        for (PoseMeasurement m : measurements) {
            if (validatePoseMeasurement(m)) {
                PoseMeasurement timeUpdated = compensateForTime(m);
                validMeasurements.add(timeUpdated);
            }
        }

        applyAllMeasurements(validMeasurements.toArray(new PoseMeasurement[0]));
        applyOdometryUpdates();
    }

    /**
     * Applies the latest pose estimates to the odometries marked as requiring updates
     */
    private void applyOdometryUpdates() {
        for (String name : toUpdate) {
            if (!odometryLastPose.containsKey(name)) continue;
            Pair<Pose2d, Optional<Pair<Pose2d, Vector<N3>>>> mapPair = odometryLastPose.get(name);
            Pair<Pose2d, Optional<Pair<Pose2d, Vector<N3>>>> newPair = new Pair<>(mapPair.getFirst(), Optional.of(new Pair<>(lastPose, lastPoseStdDev)));
            odometryLastPose.replace(name, newPair);
        }
    }

    /**
     * Applies all implemented measurements via a bayesian updating of x, y, and theta.
     * @param measurements The {@link PoseMeasurement} to combine to combine.
     */
    @SuppressWarnings("unchecked")
    private void applyAllMeasurements(PoseMeasurement... measurements) {
        if (measurements.length == 0) return;
        List<Pair<Double, Double>> allX = new ArrayList<>();
        List<Pair<Double, Double>> allY = new ArrayList<>();
        List<Pair<Double, Double>> allTheta = new ArrayList<>();

        for (PoseMeasurement m : measurements) {
            allX.add(new Pair<>(m.pose.getX(), m.poseStdDev.get(0)));
            allY.add(new Pair<>(m.pose.getY(), m.poseStdDev.get(1)));
            allTheta.add(new Pair<>(m.pose.getRotation().getRadians(), m.poseStdDev.get(2)));
        }

        Pair<Double, Double> x = bayesianUpdate(allX.toArray(new Pair[0]));
        Pair<Double, Double> y = bayesianUpdate(allY.toArray(new Pair[0]));
        Pair<Double, Double> theta = bayesianUpdate(allTheta.toArray(new Pair[0]));

        Time newTimestamp = Seconds.of(Utils.getCurrentTimeSeconds());
        Time timestep = newTimestamp.minus(lastTimestamp);

        Pose2d newPose = new Pose2d(x.getFirst(), y.getFirst(), new Rotation2d(theta.getFirst()));
        Vector<N3> newPoseStandardDeviations = VecBuilder.fill(x.getSecond(), y.getSecond(), theta.getSecond());

        double scalar = 1/timestep.in(Seconds);
        ChassisVelocities newVel = new ChassisVelocities(
            newPose.getX() - lastPose.getX(), 
            newPose.getY() - lastPose.getY(), 
            newPose.getRotation().getRadians() - lastPose.getRotation().getRadians()
        ).times(scalar);
        Vector<N3> newVelStdDev = VecBuilder.fill(
            newPoseStandardDeviations.get(0) + lastPoseStdDev.get(0),
            newPoseStandardDeviations.get(1) + lastPoseStdDev.get(1),
            newPoseStandardDeviations.get(2) + lastPoseStdDev.get(2)
        );

        ChassisAccelerations newAcc = new ChassisAccelerations(
            newVel.vx - lastVelocity.vx,
            newVel.vy - lastVelocity.vy,
            newVel.omega - lastVelocity.omega
        ).times(scalar);
        Vector<N3> newAccStdDev = VecBuilder.fill(
            newVelStdDev.get(0) + lastVelocityStdDev.get(0),
            newVelStdDev.get(1) + lastVelocityStdDev.get(1),
            newVelStdDev.get(2) + lastVelocityStdDev.get(2)
        );

        ChassisJerks newJerks = new ChassisJerks(
            newAcc.ax - lastAcceleration.ax,
            newAcc.ay - lastAcceleration.ay,
            newAcc.alpha - lastAcceleration.alpha
        ).times(scalar);
        Vector<N3> newJerkStdDev = VecBuilder.fill(
            newAccStdDev.get(0) + lastAccelerationStdDev.get(0),
            newAccStdDev.get(1) + lastAccelerationStdDev.get(1),
            newAccStdDev.get(2) + lastAccelerationStdDev.get(2)
        );

        lastPose = newPose;
        lastPoseStdDev = newPoseStandardDeviations;
        lastVelocity = newVel;
        lastVelocityStdDev = newVelStdDev;
        lastAcceleration = newAcc;
        lastAccelerationStdDev = newAccStdDev;
        lastJerk = newJerks;
        lastJerkStdDev = newJerkStdDev;
    }

    /**
     * Combines the normal distributions through a bayesian update.
     * @param distributionPairs Pair where the first is the mean and the second is the standard deviation.
     * @return the combined distribution.
     */
    @SuppressWarnings("unchecked")
    private Pair<Double, Double> bayesianUpdate(Pair<Double, Double>... distributionPairs) {
        double totalPrecision = 0;
        double weightedMeanSum = 0;
        for (Pair<Double, Double> pair : distributionPairs) {
            if (pair.getSecond() == 0) {
                // 0 standard deviation means absolute certainty. Also avoids a divide by zero error.
                return pair;
            }
            double precision = 1 / (pair.getSecond()*pair.getSecond());
            totalPrecision += precision;
            weightedMeanSum += precision * pair.getFirst();
        }

        double variance = 1/totalPrecision;
        double standardDeviation = Math.sqrt(variance);
        double mean = weightedMeanSum / totalPrecision;
        return new Pair<Double,Double>(mean, standardDeviation);
    }

    /**
     * Seeds all values in the position estimator.
     * @param newPose The new {@link Pose2d} of the robot to seed.
     * @param newPoseStdDev The Standard Deviations of the new pose.
     * @param newVel The new {@link ChassisVelocities} of the robot to seed.
     * @param newVelStdDev The Standard Deviations of the new velocity.
     * @param newAcc The new {@link ChassisAccelerations} of the robot to seed.
     * @param newAccStdDev The Standard Deviations of the new acceleration.
     * @param newJerk The new {@link ChassisJerks} of the robot to seed.
     * @param newJerkStdDev The Standard Deviations of the new jerk.
     */
    public void seed(
        Pose2d newPose,
        Vector<N3> newPoseStdDev,
        ChassisVelocities newVel,
        Vector<N3> newVelStdDev,
        ChassisAccelerations newAcc,
        Vector<N3> newAccStdDev,
        ChassisJerks newJerk,
        Vector<N3> newJerkStdDev
    ) {
        seedPose(newPose, newPoseStdDev);
        seedAcceleration(newAcc, newAccStdDev);
        seedJerk(newJerk, newJerkStdDev);
    }

    /**
     * Seeds the robot pose
     * @param newPose the pose to seed
     */
    public void seedPose(Pose2d newPose) {
        lastPose = newPose;
    }

    /**
     * Seeds the robot pose and its accompanying standard deviations
     * @param newPose the pose to seed
     * @param newPoseStdDev the standard deviations of the pose
     */
    public void seedPose(Pose2d newPose, Vector<N3> newPoseStdDev) {
        seedPose(newPose);
        lastPoseStdDev = newPoseStdDev;
    }

    /**
     * Seeds the robot velocity
     * @param newVel the velocity to seed
     */
    public void seedVelocity(ChassisVelocities newVel) {
        lastVelocity = newVel;
    }

    /**
     * Seeds the robot velocity and its accompanying standard deviations
     * @param newVel the velocity to seed
     * @param newVelStdDev the standard deviations of the velocity
     */
    public void seedVelocity(ChassisVelocities newVel, Vector<N3> newVelStdDev) {
        seedVelocity(newVel);
        lastVelocityStdDev = newVelStdDev;
    }

    /**
     * Seeds the robot acceleration
     * @param newAcc the acceleration to seed
     */
    public void seedAcceleration(ChassisAccelerations newAcc) {
        lastAcceleration = newAcc;
    }

    /**
     * Seeds the robot acceleration and its accompanying standard deviations
     * @param newAcc the acceleration to seed
     * @param newAccStdDev the standard deviations of the acceleration
     */
    public void seedAcceleration(ChassisAccelerations newAcc, Vector<N3> newAccStdDev) {
        seedAcceleration(newAcc);
        lastAccelerationStdDev = newAccStdDev;
    }

    /**
     * Seeds the robot jerk
     * @param newJerk the jerk to seed
     */
    public void seedJerk(ChassisJerks newJerk) {
        lastJerk = newJerk;
    }

    /**
     * Seeds the robot jerk and its accompanying standard deviations
     * @param newAcc the jerk to seed
     * @param newAccStdDev the standard deviations of the jerk
     */
    public void seedJerk(ChassisJerks newJerks, Vector<N3> newJerksStdDev) {
        seedJerk(newJerks);
        lastJerkStdDev = newJerksStdDev;
    }

    /**
     * The xy velocity as a {@link Vector2d}
     * @return the velocity xy vector
     */
    public Vector2d getVelocityPositionVector() {
        return new Vector2d(lastVelocity.vx, lastVelocity.vy);
    }

    /**
     * The xy acceleration as a {@link Vector2d}
     * @return the acceleration xy vector
     */
    public Vector2d getAccelerationPositionVector() {
        return new Vector2d(lastAcceleration.ax, lastAcceleration.ay);
    }

    /**
     * The xy jerk as a {@link Vector2d}
     * @return the jerk xy vector
     */
    public Vector2d getJerkPositionVector() {
        return new Vector2d(lastJerk.jx, lastJerk.jy);
    }

    /**
     * Predicts the future pose of the robot based on its current estimation. Considers the first derivative (velocity) as constant.
     * @param timestep timestep into the future to predict too
     * @return the {@link PosePrediction} of the prediction.
     */
    public PosePrediction predictDOne(Time timestep) {
        double dt = timestep.in(Seconds);

        Pose2d newPose2d = new Pose2d(
            lastPose.getX() + lastVelocity.vx*dt, 
            lastPose.getY() + lastVelocity.vy*dt, 
            new Rotation2d(lastPose.getRotation().getRadians() + lastVelocity.omega*dt));

        Vector<N3> newPoseStdDev = VecBuilder.fill(
            lastPoseStdDev.get(0) + lastVelocityStdDev.get(0)*dt,
            lastPoseStdDev.get(1) + lastVelocityStdDev.get(1)*dt,
            lastPoseStdDev.get(2) + lastVelocityStdDev.get(3)*dt
        );

        Time newTimestamp = lastTimestamp.plus(timestep);

        return new PosePrediction(newPose2d, newPoseStdDev, lastVelocity, lastVelocityStdDev, new ChassisAccelerations(), VecBuilder.fill(0, 0, 0), new ChassisJerks(), VecBuilder.fill(0, 0, 0), timestep, newTimestamp, lastTimestamp, 1);
    }

    /**
     * Predicts the future pose of the robot based on its current estimation. Considers the second derivative (acceleration) as constant.
     * @param timestep timestep into the future to predict too
     * @return the {@link PosePrediction} of the prediction.
     */
    public PosePrediction predictDTwo(Time timestep) {
        double dt = timestep.in(Seconds);
        double dtsq = dt*dt*0.5;
        ChassisVelocities newVel = lastVelocity.plus(new ChassisVelocities(lastAcceleration.ax*dt, lastAcceleration.ay*dt, lastAcceleration.alpha*dt));

        Pose2d newPose2d = new Pose2d(
            lastPose.getX() + lastVelocity.vx*dt + lastAcceleration.ax*dtsq, 
            lastPose.getY() + lastVelocity.vy*dt + lastAcceleration.ay*dtsq, 
            new Rotation2d(lastPose.getRotation().getRadians() + lastVelocity.omega*dt + lastAcceleration.alpha*dtsq));
        
        Vector<N3> newVelStdDev = VecBuilder.fill(
            lastVelocityStdDev.get(0) + lastAccelerationStdDev.get(0)*dt, 
            lastVelocityStdDev.get(1) + lastAccelerationStdDev.get(1)*dt, 
            lastVelocityStdDev.get(2) + lastAccelerationStdDev.get(2)*dt);

        Vector<N3> newPoseStdDev = VecBuilder.fill(
            lastPoseStdDev.get(0) + lastVelocityStdDev.get(0)*dt + lastAccelerationStdDev.get(0)*dtsq,
            lastPoseStdDev.get(1) + lastVelocityStdDev.get(1)*dt + lastAccelerationStdDev.get(1)*dtsq,
            lastPoseStdDev.get(2) + lastVelocityStdDev.get(3)*dt + lastAccelerationStdDev.get(2)*dtsq
        );

        Time newTimestamp = lastTimestamp.plus(timestep);

        return new PosePrediction(newPose2d, newPoseStdDev, newVel, newVelStdDev, lastAcceleration, lastAccelerationStdDev, new ChassisJerks(), VecBuilder.fill(0, 0, 0), timestep, newTimestamp, lastTimestamp, 2);
    }

    /**
     * Predicts the future pose of the robot based on its current estimation. Considers the third derivative (jerk, don't be one) as constant.
     * @param timestep timestep into the future to predict too
     * @return the {@link PosePrediction} of the prediction.
     */
    public PosePrediction predictDThree(Time timestep) {
        double dt = timestep.in(Seconds);
        double dtsq = dt*dt*0.5;
        double dtcb = dtsq*dt*(1/3);
        ChassisAccelerations newAcc = lastAcceleration.plus(new ChassisAccelerations(lastJerk.jx*dt, lastJerk.jy*dt, lastJerk.zeta*dt));
        ChassisVelocities newVel = lastVelocity.plus(new ChassisVelocities(lastAcceleration.ax*dt + lastJerk.jx*dtsq, lastAcceleration.ay*dt + lastJerk.jy*dtsq, lastAcceleration.alpha*dt + lastJerk.zeta*dtsq));

        Pose2d newPose2d = new Pose2d(
            lastPose.getX() + lastVelocity.vx*dt + lastAcceleration.ax*dtsq + lastJerk.jx*dtcb, 
            lastPose.getY() + lastVelocity.vy*dt + lastAcceleration.ay*dtsq + lastJerk.jy*dtcb, 
            new Rotation2d(lastPose.getRotation().getRadians() + lastVelocity.omega*dt + lastAcceleration.alpha*dtsq + lastJerk.zeta*dtcb));

        Vector<N3> newAccStdDev = VecBuilder.fill(
            lastAccelerationStdDev.get(0) + lastJerkStdDev.get(0)*dt,
            lastAccelerationStdDev.get(1) + lastJerkStdDev.get(1)*dt,
            lastAccelerationStdDev.get(2) + lastJerkStdDev.get(2)*dt
        );

        Vector<N3> newVelStdDev = VecBuilder.fill(
            lastVelocityStdDev.get(0) + lastAccelerationStdDev.get(0)*dt + lastJerkStdDev.get(0)*dtsq, 
            lastVelocityStdDev.get(1) + lastAccelerationStdDev.get(1)*dt + lastJerkStdDev.get(1)*dtsq, 
            lastVelocityStdDev.get(2) + lastAccelerationStdDev.get(2)*dt + lastJerkStdDev.get(2)*dtsq);

        Vector<N3> newPoseStdDev = VecBuilder.fill(
            lastPoseStdDev.get(0) + lastVelocityStdDev.get(0)*dt + lastAccelerationStdDev.get(0)*dtsq + lastJerkStdDev.get(0)*dtcb,
            lastPoseStdDev.get(1) + lastVelocityStdDev.get(1)*dt + lastAccelerationStdDev.get(1)*dtsq + lastJerkStdDev.get(1)*dtcb,
            lastPoseStdDev.get(2) + lastVelocityStdDev.get(3)*dt + lastAccelerationStdDev.get(2)*dtsq + lastJerkStdDev.get(2)*dtcb
        );

        Time newTimestamp = lastTimestamp.plus(timestep);
        return new PosePrediction(newPose2d, newPoseStdDev, newVel, newVelStdDev, newAcc, newAccStdDev, lastJerk, lastJerkStdDev, timestep, newTimestamp, lastTimestamp, 3);
    }

    /**
     * Validates weather or not a pose measurement should be used.
     * @param measurement the measurement being checked
     * @return true if valid, false if not.
     */
    public boolean validatePoseMeasurement(PoseMeasurement measurement) {
        Time measurementTimestamp = measurement.measurementReceivedTimestamp.minus(measurement.measurementDelay);

        // check to see if the measurement was taken before our latest timestamp, if so then return false.
        if (measurementTimestamp.in(Seconds) <= lastTimestamp.in(Seconds)) {
            return false;
        }

        // reject if the standard deviations are high enough. x and y should be with 2.5 meters and rotation should be within 90 degrees. Otherwise it still has a chance to narrow down the position of the robot.
        if (measurement.poseStdDev.get(0) > 2.5 && measurement.poseStdDev.get(1) > 2.5 && measurement.poseStdDev.get(2) > Math.PI/2) {
            return false;
        }

        return true;
    }

    /**
     * Compensates for time delay by increasing the standard deviation for latency via the motion of the robot.
     * @param m The measurement to update.
     * @return The updated measurement
     */
    private PoseMeasurement compensateForTime(PoseMeasurement m) {
        double delaySeconds = m.measurementDelay.in(Seconds);
        double dsq = delaySeconds*delaySeconds*0.5;
        double dcb = dsq*delaySeconds/3;

        Vector<N3> newPoseStdDev = VecBuilder.fill(
            m.poseStdDev.get(0) + lastVelocityStdDev.get(0)*delaySeconds + lastAccelerationStdDev.get(0)*dsq + lastJerkStdDev.get(0)*dcb,
            m.poseStdDev.get(1) + lastVelocityStdDev.get(1)*delaySeconds + lastAccelerationStdDev.get(1)*dsq + lastJerkStdDev.get(1)*dcb,
            m.poseStdDev.get(2) + lastVelocityStdDev.get(2)*delaySeconds + lastAccelerationStdDev.get(2)*dsq + lastJerkStdDev.get(2)*dcb
        );

        return new PoseMeasurement(m.pose, newPoseStdDev, m.measurementDelay, m.measurementReceivedTimestamp);
    }

    /**
     * PosePrediction. Is the predicted pose of the robot at some timestep into the future of the last recorded state.
     */
    public class PosePrediction {
        /** Pose of the prediction */
        public final Pose2d pose;
        /** Standard Deviation of the pose, in meters and radians */
        public final Vector<N3> poseStdDev;
        /** Velocity of the prediction */
        public final ChassisVelocities velocity;
        /** Standard Deviation of the velocity, in meters / second and radians / second */
        public final Vector<N3> velocityStdDev;
        /** Acceleration of the prediction */
        public final ChassisAccelerations acceleration;
        /** Standard Deviation of the acceleration, in meters / second^2 and radians / second^s */
        public final Vector<N3> accelerationStdDev;
        /** Jerk of the prediction */
        public final ChassisJerks jerk;
        /** Standard Deviation of the jerk, in meters / second^3 and radians / second ^3 */
        public final Vector<N3> jerkStdDev;

        /** the timestep calculated into the future from the last timestamp */
        public final Time timestep;
        /** the timestamp that the predicted pose is a prediction of */
        public final Time predictedTimestamp;
        /** the last timestamp when the prediction was run */
        public final Time runTimestamp;

        /** The derivative of position kept constant. (1,2 or 3) */
        public final int dNum;

        public PosePrediction(
            Pose2d pose,
            Vector<N3> poseStdDev,
            ChassisVelocities velocity,
            Vector<N3> velocityStdDev,
            ChassisAccelerations acceleration,
            Vector<N3> accelerationStdDev,
            ChassisJerks jerk,
            Vector<N3> jerkStdDev,
            Time timestep,
            Time predictedTimestamp,
            Time runTimestamp,
            int dNum
        ) {
            this.pose = pose;
            this.poseStdDev = poseStdDev;
            this.velocity = velocity;
            this.velocityStdDev = velocityStdDev;
            this.acceleration = acceleration;
            this.accelerationStdDev = accelerationStdDev;
            this.jerk = jerk;
            this.jerkStdDev = jerkStdDev;
            this.timestep = timestep;
            this.predictedTimestamp = predictedTimestamp;
            this.runTimestamp = runTimestamp;
            this.dNum = dNum;
        }
    }
    
    /**
     * PoseMeasurement. Represents an absolute measurement of the robot pose from a field relative point of view.
     */
    public class PoseMeasurement {
        public final Pose2d pose;
        public final Vector<N3> poseStdDev;
        public final Time measurementDelay;
        public final Time measurementReceivedTimestamp; // timestamp for when software received the pose, is used for validation and updating delay.

        public PoseMeasurement(Pose2d pose, Vector<N3> poseStdDev, Time measurementDelay, Time measurementReceivedTimestamp) {
            this.pose = pose;
            this.poseStdDev = poseStdDev;
            this.measurementDelay = measurementDelay;
            this.measurementReceivedTimestamp = measurementReceivedTimestamp;
        }
    }

    /**
     * OdometryMeasurement. Represents a relative measurement of the robot pose from its initial pose. Is field relative.
     */
    public class OdometryMeasurement {
        public final PoseMeasurement measurement;
        public final String name;

        public OdometryMeasurement(PoseMeasurement measurement, String name) {
            this.measurement = measurement;
            this.name = name;
        }
    }

}
