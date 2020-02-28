package frc.robot;

public class Strategy4 implements IStrategy {
    Strategy4() {
        begin();
    }

    public String getName() {
        return "Facing Color Wheel";
    }

    IAction[] _actions = {
        new StartIntake(),
        new Move(10.33, 0.6),
        new Sleep(0.5),
        new Move(-1.62, 0.3),
        new Sleep(0.5),
        new Move(25.06, 0.9),
        new Turn(136.37),
        new Move(0.5, 0.9),
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