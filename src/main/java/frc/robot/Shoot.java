package frc.robot;

public class Shoot implements IAction {
    Shoot (double time) {
        _time = time;
    }
    
    private double _time;
    public double getTime() { return _time; }
}