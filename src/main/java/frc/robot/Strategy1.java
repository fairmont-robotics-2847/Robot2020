package frc.robot;

// This is an example of a simple strategy that causes the robot to drive in a square

public class Strategy1 implements IStrategy {
    Strategy1() {
        begin();
    }

    public String getName() {
        return "Drive forward, hit up the trench";
    }

    IAction[] _actions = {
        new StartIntake(),
        new Move (9),
        new Shoot(3),
        new Move(-3),
        new Turn(90),
        new Move (5),
        new Turn(90),
        new Move(24)
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