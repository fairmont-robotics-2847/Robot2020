package frc.robot;

public interface IPerformance {
  void begin();
  void completed(IAction action); // gives the performance a chance to react to how well the last action was executed
  boolean more();
  IAction next();
}