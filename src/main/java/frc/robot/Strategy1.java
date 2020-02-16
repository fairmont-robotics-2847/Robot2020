package frc.robot;

// This is an example of a simple strategy that causes the robot to drive in a square

public class Strategy1 implements IStrategy {
    Strategy1() {
        begin();
    }

    public String getName() {
        return "Move_and_Shoot";
    }

    IAction[] _actions = {
        new Move(10),
        new Shoot(4)
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