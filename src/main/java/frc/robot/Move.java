package frc.robot;

public class Move implements IAction {
    Move(double distance) {
        _goal = distance;
        _speed = Drive.kMoveFastSpeed;
        _travelled = 0;
    }

    Move(double distance, double speed) {
        _goal = distance;
        _speed = speed;
        _travelled = 0;
    }

    private double _goal;
    public double getGoal() { return _goal; }

    private double _speed;
    public double getSpeed() {
        return _speed;
    }

    public void complete(double travelled, double heading) {
        _travelled = travelled;
        _heading = heading;
    }

    // How'd I do?
    private double _travelled;
    public double getTravelled() { return _travelled; }
    private double _heading; // in degrees
    public double getHeading() { return _heading; }
}