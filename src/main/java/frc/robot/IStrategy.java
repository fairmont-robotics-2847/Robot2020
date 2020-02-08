package frc.robot;

public interface IStrategy {
  void begin();
  void completed(IAction action);
  boolean more();
  IAction next();
}