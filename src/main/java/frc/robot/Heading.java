package frc.robot;

public class Heading {
  private double _distance;
  public double getDistance() { return _distance; }

  public void start(double distance) {
    _distance = distance;
  }

  public double getHeading(double travelled, double angle) {
    double sign = angle > 0 ? -1 : 1; // the correction will be in the opposite direction as the error
    angle = Math.toRadians(Math.abs(angle)); // Java math trig functions use radians
    double xDistance = travelled * Math.sin(angle);
    double yDistance = travelled * Math.sin(Math.PI / 2.0 - angle);
    double xRemaining = _distance - yDistance;
    _distance = Math.sqrt(Math.pow(xDistance, 2.0) + Math.pow(xRemaining, 2.0));
    return sign * (Math.toDegrees(Math.asin(xDistance / _distance) + angle));
  }
}