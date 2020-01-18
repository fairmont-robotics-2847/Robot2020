package frc.robot;

public class Turn implements IAction {
  Turn(double angle) {
    _goal = angle;
    _heading = 0;
  }

  private double _goal;
  public double getGoal() { return _goal; }

  public void complete(double heading) {
    _heading = heading;
  }

  // How'd I do?
  private double _heading; // in degrees
  public double getHeading() { return _heading; }
}