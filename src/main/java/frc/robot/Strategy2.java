package frc.robot;

public class Strategy2 implements IStrategy {
    Strategy2() {
        begin();
    }

    public String getName() {
        return "Flip_a_uie";
    }

    IAction[] _actions = {
        new StartIntake(),
        new Move(3),
        new StopIntake(),
        new Turn(180),
        new Move(13),
        new Shoot(6)
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