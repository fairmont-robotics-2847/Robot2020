package frc.robot;
import edu.wpi.first.wpilibj.DigitalInput;

public class IIntake implements IAction {
    IIntake(ICommander commander) {
        _commander = commander;
    }
    ICommander _commander;
    IAction _action;

DigitalInput[] _ballReadyToConvey = {
            new DigitalInput(0), // prox. positioned near "back" of the ball in the path
            new DigitalInput(1)  // prox. positioned near "front" of the ball in the path
        };
    
        boolean ballReadyToConvey() {
            return !_ballReadyToConvey[0].get() && !_ballReadyToConvey[1].get(); // sensors output false when detecting balls
        }
    IIntake (double time, double speed) {

        if (_action instanceof IIntake) {
            //_conveyorMotor.set(kconveyorMotorSpeed);  //TODO: need to bring in the motors and kConveyor from the ball class
        }


        
        
    }
      
    
    private double _time;
    public double getTime() { return _time; }

    private double _speed;
    public double getSpeed() { return _speed; }
}