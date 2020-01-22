package frc.robot;

public class SquareStrategy implements IStrategy {
  SquareStrategy() {
    begin();
  }

  IAction[] _actions = {
		new Move(3), new Turn(90),
		new Move(3), new Turn(90),
		new Move(3), new Turn(90),
		new Move(3), new Turn(90)
	};
	int _action;

  public void begin() {
    _action = 0;
  }

  public void completed(IAction action) {
    
  }

  public boolean more() {
    return _action < _actions.length;
  }

  public IAction next() {
    return _actions[_action++];
  }
}