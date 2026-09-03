package com.team2052.lib.vision.limelight;

import static org.wpilib.units.Units.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.function.Function;

import org.wpilib.math.geometry.Pose3d;
import org.wpilib.math.geometry.Rotation3d;
import org.wpilib.math.geometry.Translation3d;
import org.wpilib.networktables.NetworkTable;
import org.wpilib.networktables.NetworkTableInstance;
import org.wpilib.units.measure.*;

import com.team2052.lib.vision.limelight.LimelightHelpers.IMUData;
import com.team2052.lib.vision.limelight.LimelightHelpers.PoseEstimate;
import com.team2052.lib.vision.limelight.LimelightHelpers.RawDetection;
import com.team2052.lib.vision.limelight.LimelightHelpers.RawFiducial;
import com.team2052.lib.vision.limelight.LimelightHelpers.RawTarget;

import lombok.Getter;

/** 
 * Represents a Limelight camera
 * 
 * <p> https://docs.limelightvision.io/docs/docs-limelight/apis/complete-networktables-api </p>
 */
public class LimelightCamera {
    
    @Getter private final String cameraName;
    @Getter private final NetworkTable table;
    @Getter private final LimelightConstants constants;

    LimelightCamera(LimelightConstants cameraConstants) {
        this.cameraName = cameraConstants.limelightName;
        this.constants = cameraConstants;
        this.table = NetworkTableInstance.getDefault().getTable(cameraName);
        setPipeline(constants.defaultPipeline);
        setCameraPose(constants.limelightPose);
    }

    /* NETWORK TABLE GETTERS */

    /**
     * Gets the value of the TV field from the Limelight camera.
     * @return True if the Limelight has a valid target, false otherwise.
     */
    public boolean getTV() {
        return LimelightHelpers.getTV(cameraName);
    }

    /**
     * Gets the value of the TX field from the Limelight camera.
     * @return The horizontal offset from crosshair to target (LL1: -27 degrees to 27 degrees / LL2: -29.8 to 29.8 degrees).
     */
    public Angle getTX() {
        return Degrees.of(LimelightHelpers.getTX(cameraName));
    }

    /**
     * Gets the value of the TY field from the Limelight camera.
     * @return The vertical offset from crosshair to target (LL1: -20.5 degrees to 20.5 degrees / LL2: -24.85 to 24.85 degrees).
     */
    public Angle getTY() {
        return Degrees.of(LimelightHelpers.getTY(cameraName));
    }

    /**
     * Gets the value of the TXNC field from the Limelight camera.
     * @return Horizontal Offset From Principal Pixel To Target (degrees).
     */
    public Angle getTXNC() {
        return Degrees.of(LimelightHelpers.getTXNC(cameraName));
    }

    /**
     * Gets the value of the TYNC field from the Limelight camera.
     * @return Vertical Offset From Principal Pixel To Target (degrees).
     */
    public Angle getTYNC() {
        return Degrees.of(LimelightHelpers.getTYNC(cameraName));
    }

    /**
     * Gets the value of the TA field from the Limelight camera.
     * @return The target area (0% of image to 100% of image).
     */
    public double getTA() {
        return LimelightHelpers.getTA(cameraName);
    }

    /**
     * Gets the value of the TL field from the Limelight camera.
     * @return The pipeline’s latency contribution.
     */
    public Time getTL() {
        return Milliseconds.of(LimelightHelpers.getLatency_Pipeline(cameraName));
    }

    /**
     * Gets the value of the CL field from the Limelight camera.
     * @return Capture pipeline latency (ms). Time between the end of the exposure of the middle row of the sensor to the beginning of the tracking pipeline.
     */
    public Time getCL() {
        return Milliseconds.of(LimelightHelpers.getLatency_Capture(cameraName));
    }

    /**
     * Gets the total latency of the Limelight camera.
     * @return The total latency (ms). Time between the end of the exposure of the middle row of the sensor to the beginning of the tracking pipeline plus the pipeline’s latency contribution.
     */
    public Time getTotalLatency() {
        return getTL().plus(getCL());
    }

    /**
     * Gets the value of the T2D field from the Limelight camera.
     * @return Array containing several values for matched-timestamp statistics: 
     *      [targetValid, targetCount, targetLatency, captureLatency, tx, ty, txnc, tync, ta, tid, targetClassIndexDetector , 
     *      targetClassIndexClassifier, targetLongSidePixels, targetShortSidePixels, targetHorizontalExtentPixels, targetVerticalExtentPixels, targetSkewDegrees].
     */
    public double[] getT2D() {
        return LimelightHelpers.getT2DArray(cameraName);
    }

    /**
     * Gets the value of the pipeline index from the Limelight camera.
     * @return The current pipeline index (0-9).
     */
    public int getPipelineIndex() {
        return (int) LimelightHelpers.getCurrentPipelineIndex(cameraName);
    }

    /**
     * Gets the value of the pipeline type from the Limelight camera.
     * @return The current pipeline type e.g. "pipe_color".
     */
    public String getPipelineType() {
        return LimelightHelpers.getCurrentPipelineType(cameraName);
    }

    /**
     * Gets the value of the JSON dump from the Limelight camera.
     * @return Full JSON dump of targeting results. Must be enabled per-pipeline in the 'output' tab of the Limelight web UI.
     */
    public String getJSONDump() {
        return LimelightHelpers.getJSONDump(cameraName);
    }

    /**
     * Gets the value of the target color from the Limelight camera.
     * @return Get the average BGR color underneath the crosshair region as a double array [B, G, R].
     */
    public double[] getTargetColor() {
        return LimelightHelpers.getTargetColor(cameraName);
    }

    /**
     * Gets the value of the heartbeat from the Limelight camera.
     * @return heartbeat value. Increases once per frame, resets at 2 billion.
     */
    public double getHeartbeat() {
        return LimelightHelpers.getHeartbeat(cameraName);
    }

    /**
     * Gets the value of the hardware metrics from the Limelight camera.
     * @return Hardware metrics [cpu_temp_celsius, cpu_usage, ram_usage_percent, fps].
     */
    public double[] getHardwareMetrics() {
        return table.getEntry("hw").getDoubleArray(new double[4]);
    }

    /**
     * Gets the value of the crosshairs from the Limelight camera.
     * @return 2D Crosshairs [cx0, cy0, cx1, cy1]
     */
    public double[] getCrosshairs() {
        return table.getEntry("crosshairs").getDoubleArray(new double[4]);
    }

    /**
     * Gets the value of the target class from the Limelight camera.
     * @return Name of classifier pipeline's computed class
     */
    public String getTCClass() {
        return table.getEntry("tcclass").getString("");
    }

    /**
     * Gets the value of the target class from the Limelight camera.
     * @return Name of detector pipeline's computed class
     */
    public String getTDClass() {
        return table.getEntry("tdclass").getString("");
    }

    /**
     * Robot transform in field-space.
     * @return LimelightBotPose object containing the robot's pose, latency, tag count, tag span, average tag distance from camera, and average tag area.
     */
    @MegaTag1
    public LimelightBotPose getBotPose() {
        return botPoseArrayToLimelightBotPose(LimelightHelpers.getBotPose(cameraName));
    }

    /**
     * Robot transform in field-space (blue driverstation WPILIB origin).
     * @return LimelightBotPose object containing the robot's pose, latency, tag count, tag span, average tag distance from camera, and average tag area.
     */
    @MegaTag1
    public LimelightBotPose getBotPose_wpiBlue() {
        return botPoseArrayToLimelightBotPose(LimelightHelpers.getBotPose_wpiBlue(cameraName));
    }

    /**
     * Robot transform in field-space (red driverstation WPILIB origin).
     * @return LimelightBotPose object containing the robot's pose, latency, tag count, tag span, average tag distance from camera, and average tag area.
     */
    @MegaTag1
    public LimelightBotPose getBotPose_wpiRed() {
        return botPoseArrayToLimelightBotPose(LimelightHelpers.getBotPose_wpiRed(cameraName));
    }

    /**
     * Pose estimate of the robot in field-space (blue driverstation WPILIB origin).
     * @return PoseEstimate object containing the robot's pose estimate, latency, and ambiguity.
     */
    @MegaTag1
    public PoseEstimate getBotPoseEstimate_wpiBlue() {
        return LimelightHelpers.getBotPoseEstimate_wpiBlue(cameraName);
    }

    /**
     * Pose estimate of the robot in field-space (red driverstation WPILIB origin).
     * @return PoseEstimate object containing the robot's pose estimate, latency, and ambiguity.
     */
    @MegaTag1
    public PoseEstimate getBotPoseEstimate_wpiRed() {
        return LimelightHelpers.getBotPoseEstimate_wpiRed(cameraName);
    }

    /**
     * Robot transform in field-space.
     * @return LimelightBotPose object containing the robot's pose, latency, tag count, tag span, average tag distance from camera, and average tag area.
     */
    @MegaTag2
    public LimelightBotPose getBotPose_Orb() {
        return botPoseArrayToLimelightBotPose(table.getEntry("botpose_orb").getDoubleArray(new double[11]));
    }

    /**
     * Robot transform in field-space (blue driverstation WPILIB origin).
     * @return LimelightBotPose object containing the robot's pose, latency, tag count, tag span, average tag distance from camera, and average tag area.
     */
    @MegaTag2
    public LimelightBotPose getBotPose_Orb_wpiBlue() {
        return botPoseArrayToLimelightBotPose(table.getEntry("botpose_orb_wpiblue").getDoubleArray(new double[11]));
    }

    /**
     * Robot transform in field-space (red driverstation WPILIB origin).
     * @return LimelightBotPose object containing the robot's pose, latency, tag count, tag span, average tag distance from camera, and average tag area.
     */
    @MegaTag2
    public LimelightBotPose getBotPose_Orb_wpiRed() {
        return botPoseArrayToLimelightBotPose(table.getEntry("botpose_orb_wpired").getDoubleArray(new double[11]));
    }

    /**
     * Pose estimate of the robot in field-space (blue driverstation WPILIB origin).
     * @return PoseEstimate object containing the robot's pose estimate, latency, and ambiguity.
     */
    @MegaTag2
    public PoseEstimate getBotPoseEstimate_Orb_wpiBlue() {
        return LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2(cameraName);
    }

    /**
     * Pose estimate of the robot in field-space (red driverstation WPILIB origin).
     * @return PoseEstimate object containing the robot's pose estimate, latency, and ambiguity.
     */
    @MegaTag2
    public PoseEstimate getBotPoseEstimate_Orb_wpiRed() {
        return LimelightHelpers.getBotPoseEstimate_wpiRed_MegaTag2(cameraName);
    }

    /**
     * Gets the camera pose of the Limelight camera in target-space.
     * @return The camera pose of the Limelight camera in target-space.
     */
    public Pose3d getCameraPose_TargetSpace() {
        return LimelightHelpers.getCameraPose3d_TargetSpace(cameraName);
    }

    /**
     * Gets the target pose of the Limelight camera in camera-space.
     * @return The target pose of the Limelight camera in camera-space.
     */
    public Pose3d getTargetPose_CameraSpace() {
        return LimelightHelpers.getTargetPose3d_CameraSpace(cameraName);
    }

    /**
     * Gets the target pose of the Limelight camera in robot-space.
     * @return The target pose of the Limelight camera in robot-space.
     */
    public Pose3d getTargetPose_RobotSpace() {
        return LimelightHelpers.getTargetPose3d_RobotSpace(cameraName);
    }

    /**
     * Gets the robot pose of the Limelight camera in target-space.
     * @return The robot pose of the Limelight camera in target-space.
     */
    public Pose3d getBotPose_TargetSpace() {
        return LimelightHelpers.getBotPose3d_TargetSpace(cameraName);
    }

    /**
     * Gets the camera pose of the Limelight camera in robot-space.
     * @return The camera pose of the Limelight camera in robot-space.
     */
    public Pose3d getCameraPose_RobotSpace() {
        return LimelightHelpers.getCameraPose3d_RobotSpace(cameraName);
    }

    /**
     * Gets the tag ID of the primary in-view Aplril Tag.
     * @return The tag ID of the primary in-view April Tag. Returns 0 if no tag is in view.
     */
    public int getTagID() {
        return (int) table.getEntry("tid").getInteger(0);
    }

    /**
     * Gets the standard deviations of the Limelight camera.
     * @return MegaTag Standard Deviations [MT1x, MT1y, MT1z, MT1roll, MT1pitch, MT1Yaw, MT2x, MT2y, MT2z, MT2roll, MT2pitch, MT2yaw].
     */
    public double[] getSTDDevs() {
        return table.getEntry("stddevs").getDoubleArray(new double[12]);
    }

    /**
     * Gets the IMU data of the Limelight camera.
     * @return IMUData object containing the Limelight camera's orientation and angular velocity.
     */
    public IMUData getIMUData() {
        return LimelightHelpers.getIMUData(cameraName);
    }

    /**
     * Gets the raw fiducials detected by the Limelight camera.
     * @return An array of RawFiducial objects representing the raw fiducials detected by the Limelight camera.
     */
    public RawFiducial[] getRawFiducials() {
        return LimelightHelpers.getRawFiducials(cameraName);
    }

    /**
     * Gets the raw detections detected by the Limelight camera.
     * @return An array of RawDetection objects representing the raw detections detected by the Limelight camera.
     */
    public RawDetection[] getRawDetections() {
        return LimelightHelpers.getRawDetections(cameraName);
    }

    /**
     * Gets the raw targets detected by the Limelight camera.
     * @return An array of RawTarget objects representing the raw targets detected by the Limelight camera.
     */
    public RawTarget[] getRawTargets() {
        return LimelightHelpers.getRawTargets(cameraName);
    }

    /**
     * Use to get the value of any unimplemented Limelight data field.
     * @param getter Function that takes a String (Limelight camera name) and returns a value of type {@link T}.
     * @return Function return with input of limelight camera name.
     */
    public <T> T getData(Function<String, T> getter) {
        return getter.apply(cameraName);
    }

    /* NETWORK TABLE SETTERS */

    /**
     * Sets the pipeline index of the Limelight camera.
     * @param pipelineIndex The pipeline index to set (0-9).
     */
    public void setPipeline(int pipelineIndex) {
        LimelightHelpers.setPipelineIndex(cameraName, pipelineIndex);
    }

    /**
     * Enables or disables the rewind feature of the Limelight camera.
     * @param rewind True to enable rewind, false to pause.
     */
    public void setRewind(boolean rewind) {
        LimelightHelpers.setRewindEnabled(cameraName, rewind);
    }

    /**
     * Recommend setting this to 100-200 while disabled. 
     * Sets number of frames to skip between processed frames to reduce temperature rise. 
     * Outputs are not zeroed during skipped frames.
     * @param throttle The number of frames to skip between processed frames. 0 means process every frame, 1 means process every other frame, etc.
     */
    public void setThrottle(int throttle) {
        LimelightHelpers.SetThrottle(cameraName, throttle);
    }

    /**
     * Sets the camera pose of the Limelight camera.
     * @param pose The pose of the Limelight camera relative to the robot.
     */
    public void setCameraPose(Pose3d pose) {
        double[] poseArray = new double[]{
            pose.getMeasureX().in(Meters),
            pose.getMeasureY().in(Meters),
            pose.getMeasureZ().in(Meters),
            pose.getRotation().getMeasureX().in(Degrees),
            pose.getRotation().getMeasureY().in(Degrees),
            pose.getRotation().getMeasureZ().in(Degrees)
        };
        table.getEntry("camerapose_robotspace_set").setDoubleArray(poseArray);
    }

    /**
     * Sets the robot orientation of the Limelight camera.
     * @param rotation The rotation of the robot relative to the field.
     * @param rotationRate The rotation rate of the robot relative to the field. Is usually unessasary but could be useful.
     */
    public void setRobotOrientation(Rotation3d rotation, Rotation3d rotationRate) {
        double[] orientationArray = new double[]{
            rotation.getMeasureX().in(Degrees),
            rotationRate.getMeasureX().in(Degrees),
            rotation.getMeasureY().in(Degrees),
            rotationRate.getMeasureY().in(Degrees),
            rotation.getMeasureZ().in(Degrees),
            rotationRate.getMeasureZ().in(Degrees)
        };
        table.getEntry("robot_orientation_set").setDoubleArray(orientationArray);
    }

    /**
     * Override valid fiducial ids for localization. This is useful if you want to ignore certain tags or only use a subset of tags for localization.
     * @param validIDs An array of valid fiducial IDs to set.
     */
    public void setValidFiducialIDs(int[] validIDs) {
        double[] validIDsDouble = new double[validIDs.length];
        for (int i = 0; i < validIDs.length; i++) {
            validIDsDouble[i] = validIDs[i];
        }
        table.getEntry("fiducial_id_filters_set").setDoubleArray(validIDsDouble);
    }

    public void flushNetworkTables() {
        table.getInstance().flush();
    }

    /**
     * Converts a botpose array from the Limelight camera to a Pose3d object.
     * @param botPoseArray The botpose array from the Limelight camera.
     * @return A Pose3d object representing the robot's pose.
     * @throws IllegalArgumentException if the botPose array does not have 6 elements.
     */
    private Pose3d botPoseArrayToPose3d(double[] botPoseArray) {
        if (botPoseArray.length != 6) {
            System.err.println("Warning: botPose array length is not 6. Returning default Pose3d.");
            return new Pose3d(); // Return a default Pose3d if the array length is not 6
        }
        return new Pose3d(
            new Translation3d(Meters.of(botPoseArray[0]), Meters.of(botPoseArray[1]), Meters.of(botPoseArray[2])),
            new Rotation3d(Degrees.of(botPoseArray[3]), Degrees.of(botPoseArray[4]), Degrees.of(botPoseArray[5]))
        );
    }

    /**
     * Converts a botpose array from the Limelight camera to a LimelightBotPose object.
     * @param botPoseArray The botpose array from the Limelight camera.
     * @return A LimelightBotPose object representing the robot's pose, latency, tag count, tag span, average tag distance from camera, and average tag area.
     * @throws IllegalArgumentException if the botPose array does not have 11 elements.
     */
    private LimelightBotPose botPoseArrayToLimelightBotPose(double[] botPoseArray) {
        if (botPoseArray.length != 11) {
            System.err.println("Warning: botPose array length is not 11. Returning default LimelightBotPose.");
            return new LimelightBotPose(new Pose3d(), Milliseconds.of(0), 0, 0.0, Meters.of(0), 0.0); // Return a default LimelightBotPose if the array length is not 11
        }
        return new LimelightBotPose(
            botPoseArrayToPose3d(botPoseArray),
            Milliseconds.of(botPoseArray[6]),
            (int) botPoseArray[7],
            botPoseArray[8],
            Meters.of(botPoseArray[9]),
            botPoseArray[10]
        );
    }

    public class LimelightConstants {
        public String limelightName = "";
        public int defaultPipeline = 0;
        public Pose3d limelightPose = new Pose3d();
    }

    public class LimelightBotPose {
        public final Pose3d botPose;
        public final Time latency;
        public final int tagCount;
        public final double tagSpan;
        public final Distance aveTagDistFromCamera;
        public final double aveTagArea;

        public LimelightBotPose(Pose3d botPose, Time latency, int tagCount, double tagSpan, Distance aveTagDistFromCamera, double aveTagArea) {
            this.botPose = botPose;
            this.latency = latency;
            this.tagCount = tagCount;
            this.tagSpan = tagSpan;
            this.aveTagDistFromCamera = aveTagDistFromCamera;
            this.aveTagArea = aveTagArea;
        }
    }

    @Target({ElementType.METHOD})
    public @interface MegaTag1 {}

    @Target({ElementType.METHOD})
    public @interface MegaTag2 {}
}