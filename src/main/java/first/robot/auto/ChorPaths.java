// Copyright (c) 2026 FRC Team 2052 - KnightKrawler
// https://github.com/frc2052/Pre-2027-Biocore
//
// Use of this source code is governed by an MIT-style
// license that can be found in the LICENSE file at
// the root directory of this project.

package first.robot.auto;

public enum ChorPaths {
  TEST_PATH("NewPath");

  private final String pathName;

  private ChorPaths(String pathName) {
    this.pathName = pathName;
  }

  public String getPathName() {
    return pathName;
  }
}
