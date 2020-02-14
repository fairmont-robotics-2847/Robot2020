package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.DigitalInput;

import edu.wpi.first.wpilibj.Timer;

public class Ball implements IActor {
    Ball(ICommander commander) {
        _commander = commander;
    }

    ICommander _commander;
    IAction _action;
     WPI_VictorSPX _intakeMotor = new WPI_VictorSPX(2);
    WPI_VictorSPX _conveyorMotor = new WPI_VictorSPX(3);
    WPI_VictorSPX _flyWheelMotor = new WPI_VictorSPX(4);
    
    Timer _timer = new Timer();
    double _shootStartedAt;

    static final double kIntakeMotorSpeed = -1.0;
    static final double kConveyorMotorSpeed = 1.0;
    static final double kFlyWheelMotorSpeed = 0.7;

    DigitalInput[] _ballReadyToConvey = {
        new DigitalInput(0), // prox. positioned near "back" of the ball in the path
        new DigitalInput(1)  // prox. positioned near "front" of the ball in the path
    };

    boolean ballReadyToConvey() {
        return !_ballReadyToConvey[0].get() && !_ballReadyToConvey[1].get();  // sensors output false when detecting balls
    }

    public void init() {
        _conveyorMotor.set(0);
        _flyWheelMotor.set(0);
        _intakeMotor.set(0);
    }
    
    public void teleopPeriodic(boolean intake, boolean launch){ 
        boolean runIntake = intake;
        boolean runConveyor = ballReadyToConvey() || launch;
        boolean runFlyWheel = launch;

        _intakeMotor.set(runIntake ? kIntakeMotorSpeed : 0.0);
        _conveyorMotor.set(runConveyor ? kConveyorMotorSpeed : 0.0);
        _flyWheelMotor.set(runFlyWheel ? kFlyWheelMotorSpeed : 0.0);
    }

    public void autonomousPeriodic() {
        if (_action instanceof Shoot) {
            Shoot shoot = (Shoot)_action;
            double elapsedSinceStart = _timer.get() - _shootStartedAt; 
            if (elapsedSinceStart < 1) { // ramp time
                _flyWheelMotor.set(1); // Max speed to get flywheel up to speed as quickly as possible
                _conveyorMotor.set(0);
            } else if (elapsedSinceStart < shoot.getTime()) {
                _flyWheelMotor.set(kFlyWheelMotorSpeed);
                _conveyorMotor.set(kConveyorMotorSpeed);
            } else {
                _flyWheelMotor.set(0);
                _conveyorMotor.set(0);
                completed();
            }
        } else if (_action instanceof StartIntake) {
            _conveyorMotor.set(kConveyorMotorSpeed);
            completed();
        } else if (_action instanceof StopIntake) {
            _conveyorMotor.set(0);
            completed();
        }
    }

    void completed() {
        IAction action = _action;
        _action = null;
        _commander.completed(action);
    }

    public boolean perform(IAction action) {
        if (action instanceof Shoot) {
            _action = action;
            _shootStartedAt = _timer.get();
            return true;
        } else if (action instanceof StartIntake) {
            _action = action;
            return true;
        } else if (action instanceof StopIntake) {
            _action = action;
            return true;
        } else {
            return false;
        }
    }
}
