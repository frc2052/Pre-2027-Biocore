package com.team2052.lib.geometry;

public class ChassisJerks {

  /** meters / seconds ^ 3 */
  public double jx;

  /** meters / seconds ^ 3 */
  public double jy;

  /** radians / seconds ^ 3 */
  public double zeta;

  public ChassisJerks(double jx, double jy, double zeta) {
    this.jx = jx;
    this.jy = jy;
    this.zeta = zeta;
  }

  public ChassisJerks() {
    jx = 0;
    jy = 0;
    zeta = 0;
  }

  public ChassisJerks plus(ChassisJerks jerk) {
    return new ChassisJerks(jx + jerk.jx, jy + jerk.jy, zeta + jerk.zeta);
  }

  public ChassisJerks minus(ChassisJerks jerk) {
    return plus(jerk.unaryMinus());
  }

  public ChassisJerks unaryMinus() {
    return new ChassisJerks(-jx, -jy, -zeta);
  }

  public ChassisJerks times(double scalar) {
    return new ChassisJerks(jx * scalar, jy * scalar, zeta * scalar);
  }

  public ChassisJerks div(double divisor) {
    return times(1 / divisor);
  }

  @Override
  public String toString() {
    return String.format(
        "ChassisJerks(Jx: %.2f m/s³, Jy: %.2f m/s³, Zeta: %.2f rad/s³)", jx, jy, zeta);
  }
}
