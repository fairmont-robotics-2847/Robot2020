/*package frc.robot;

public class Move implements IAction {
  Move(double distance) {
    _goal = distance;
    _travelled = 0;
  }

  private double _goal;
  public double getGoal() { return _goal; }

  public void complete(double travelled, double heading) {
    _travelled = travelled;
    _heading = heading;
  }

  // How'd I do?
  private double _travelled;
  public double getTravelled() { return _travelled; }
  private double _heading; // in degrees
  public double getHeading() { return _heading; }
}*/

package frc.robot;

public class Shoot implements IAction {
  Shoot (double time, double speed) {
    _time = time;
    _speed = speed;
  }
  
   private double _time;
   public double getTime() { return _time; }

   private double _speed;
   public double getSpeed() { return _speed; }

}