package frc.robot;

public class Shoot implements IAction {
    Shoot (double duration) {
        _duration = duration;
    }
    
    private double _duration;
    public double getDuration() { return _duration; }
}