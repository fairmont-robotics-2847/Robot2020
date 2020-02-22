package frc.robot;

public class Strategy2 implements IStrategy {
    Strategy2() {
        begin();
    }

    public String getName() {
        return "Flip a Uie";
    }

    IAction[] _actions = {
        new Move(5),
        new Turn(90),
        new Move(6),
        new Turn(-90),
        new Move(4),
        new Shoot(3)
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