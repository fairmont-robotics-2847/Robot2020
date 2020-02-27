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
        new Move(10.3, 0.7),
        new Move(-1.62, 0.5),
        new Turn(43.63),
        new Move(1.9),
        new Move(-25.06, 0.9),
        new Turn(136.37),
        new Shoot(5)
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