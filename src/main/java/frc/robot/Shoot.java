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