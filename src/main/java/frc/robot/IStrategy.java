package frc.robot;

public interface IStrategy {
    String getName();
    void begin();
    void completed(IAction action);
    boolean more();
    IAction next();
}