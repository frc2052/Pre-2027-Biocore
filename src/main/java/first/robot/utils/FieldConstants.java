package first.robot.utils;

import static org.wpilib.units.Units.*;

import com.team2052.lib.regions.CombinedRegion;
import com.team2052.lib.regions.OverlappingRegion;
import com.team2052.lib.regions.RectangleRegion;
import com.team2052.lib.regions.Region;
import org.wpilib.fields.Fields;
import org.wpilib.math.geometry.Translation2d;
import org.wpilib.units.measure.*;

public class FieldConstants {

  public static final Fields FIELD = Fields.FRC_2026_REBUILT_WELDED; // TODO: Change to 2027

  public static final Distance FIELD_LENGTH = Meters.of(FIELD.length);
  public static final Distance FIELD_WIDTH = Meters.of(FIELD.width);

  public static class FieldRegions {
    public static final Region FULL_FIELD =
        new RectangleRegion(new Translation2d(0, 0), new Translation2d(FIELD_LENGTH, FIELD_WIDTH));
    public static final Region BLUE_HALF =
        new RectangleRegion(
            new Translation2d(0, 0), new Translation2d(FIELD_LENGTH.times(0.5), FIELD_WIDTH));
    public static final Region RED_HALF =
        new RectangleRegion(
            new Translation2d(FIELD_LENGTH.times(0.5), Meters.zero()),
            new Translation2d(FIELD_LENGTH, FIELD_WIDTH));

    public static final Region INVALID_ROBOT_POSE_REGION = new CombinedRegion(new Region[0]);
    public static final Region VALID_ROBOT_POSE_REGION =
        new OverlappingRegion(FULL_FIELD, INVALID_ROBOT_POSE_REGION.inverse());

    public static final Region BLUE_PERSPECTIVE_RIGHT =
        new RectangleRegion(
            new Translation2d(0, 0), new Translation2d(FIELD_LENGTH, FIELD_WIDTH.times(0.5)));
    public static final Region BLUE_PERSPECTIVE_LEFT =
        new RectangleRegion(
            new Translation2d(Meters.zero(), FIELD_WIDTH.times(0.5)),
            new Translation2d(FIELD_LENGTH, FIELD_WIDTH));
    public static final Region RED_PERSPECTIVE_RIGHT = BLUE_PERSPECTIVE_LEFT;
    public static final Region RED_PERSPECTIVE_LEFT = BLUE_PERSPECTIVE_RIGHT;

    public static final Region BLUE_RIGHT_CORNER =
        new RectangleRegion(
            new Translation2d(0, 0),
            new Translation2d(FIELD_LENGTH.times(0.5), FIELD_WIDTH.times(0.5)));
    public static final Region BLUE_LEFT_CORNER =
        new RectangleRegion(
            new Translation2d(Meters.zero(), FIELD_WIDTH.times(0.5)),
            new Translation2d(FIELD_LENGTH.times(0.5), FIELD_WIDTH));
    public static final Region RED_RIGHT_CORNER =
        new RectangleRegion(
            new Translation2d(FIELD_LENGTH.times(0.5), FIELD_WIDTH.times(0.5)),
            new Translation2d(FIELD_LENGTH, FIELD_WIDTH));
    public static final Region RED_LEFT_CORNER =
        new RectangleRegion(
            new Translation2d(Meters.zero(), FIELD_WIDTH.times(0.5)),
            new Translation2d(FIELD_LENGTH.times(0.5), FIELD_WIDTH));
  }
}
