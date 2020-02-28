package frc.robot;

public class Strategy3 implements IStrategy {
    Strategy3() {
        begin();
    }

    public String getName() {
        return "Facing Trench";
    }

    IAction[] _actions = {
        new StartIntake(),
        new Move(5.0, 0.9),
        new Move(8.0, 0.6),
        new Move(-20.0, 0.8),
        new Turn(100),
        new Move(6.0, 0.5),
        new Turn(80),
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