package first.robot.auto;


public enum ChoreoPaths {
  TEST_PATH("NewPath");

  private final String pathName;

  private ChorPaths(String pathName) {
    this.pathName = pathName;
  }

  public String getPathName() {
    return pathName;
  }
}

