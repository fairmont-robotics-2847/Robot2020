package frc.robot;

public class Strategy2 implements IStrategy {
    Strategy2() {
        begin();
    }

    public String getName() {
        return "Facing Middle";
    }

    IAction[] _actions = {
        new StartIntake(),
        new Turn(36.67),
        new Move(9.35),
        new Turn(-36.67),
        new Shoot(3),
        new Move(-3),
        new Turn(90),
        new Move (5)
    };
    int _action;

    public void begin() {
        _action = 0;
    }

    public void completed(IAction action) {
        // This strategy doesn't use feedback
    }

    public boolean more() {
        return _action < _actions.length;
    }

    public IAction next() {
        return _actions[_action++];
    }
}