package frc.robot;

public class Sleep implements IAction {
  Sleep (double time) {
      _time = time;
  }
  private double _time;
  public double getTime() { return _time; }
}

