package frc.robot;

public class Sleep implements IAction {
  Sleep (double duration) {
      _duration = duration;
  }

  private double _duration;
  public double getDuration() { return _duration; }
}

