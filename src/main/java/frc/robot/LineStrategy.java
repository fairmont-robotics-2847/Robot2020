package frc.robot;

public class LineStrategy implements IStrategy {
    LineStrategy() {
        begin();
    }

    IAction[] _actions = {
        new Move(10)  
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