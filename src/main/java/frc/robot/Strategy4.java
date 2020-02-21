package frc.robot;

public class Strategy4 implements IStrategy {
    Strategy4() {
        begin();
    }

    public String getName() {
        return "Skrrrrrt route";
    }

    IAction[] _actions = {
        new StartIntake(),
        new Move(0),
        new Turn(-90),
        new Move(5.08),
        new Turn(-90),
        new StopIntake(),
        new Move(
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