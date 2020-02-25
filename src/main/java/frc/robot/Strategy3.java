package frc.robot;

public class Strategy3 implements IStrategy {
    Strategy3() {
        begin();
    }

    public String getName() {
        return "Facing Color Wheel (Left)";
    }

    IAction[] _actions = {
        new StartIntake(),
        new Move(8.8),
        new Move(-3),
        new Turn(30),
        new Move(4)
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