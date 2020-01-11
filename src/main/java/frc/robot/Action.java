package frc.robot;

public class Action {
    public enum ActionType {
        kMove,
        kRotate
    }

    Action(ActionType type, double amount) {
        _type = type;
        _amount = amount;
    }

    private ActionType _type;
    public ActionType getType() { return _type; }

    private double _amount;
    public double getAmount() { return _amount; }
}