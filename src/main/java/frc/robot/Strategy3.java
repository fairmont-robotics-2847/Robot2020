package frc.robot;

public class Strategy3 implements IStrategy {
    Strategy3() {
        begin();
    }

    public String getName() {
        return "Strategy3";
    }

    IAction[] _actions = {
        new Move(10),
        // shoot 
        new Move(-3),
        new Turn(90),
        new Move(4),
        new Turn(90),
        new Move(12)
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