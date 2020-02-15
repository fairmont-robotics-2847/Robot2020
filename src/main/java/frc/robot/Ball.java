package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.DigitalInput;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
    boolean _previousShoot = false;
    boolean _useBallSensor = true;
    public void setUseBallSensor(boolean value) {
        _useBallSensor = value;
    }

    public boolean getUseBallSensor() {
        return _useBallSensor;
    }

    static final double kIntakeMotorSpeed = -1.0;
    static final double kConveyorMotorSpeed = -1.0;
    static final double kFlyWheelMotorSpeed = 0.7;
    static final double kFlywheelRampTime = 1.0; // in seconds

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
    
    public void teleopPeriodic(boolean intake, boolean shoot, boolean reverseIntake, boolean advanceBall) { 
        if (shoot && !_previousShoot) {
            System.out.println("Start shooting");
            _timer.start();
        }
        _previousShoot = shoot;
        double intakeMotorSpeed;
        if (reverseIntake) intakeMotorSpeed = -kIntakeMotorSpeed;
        else if (intake) intakeMotorSpeed = kIntakeMotorSpeed;
        else intakeMotorSpeed = 0;
        _intakeMotor.set(intakeMotorSpeed);
        if (shoot) {
            runShooter(_timer.get());
            SmartDashboard.putNumber("time", _timer.get());
        } else {
            boolean runConveyor = advanceBall || (_useBallSensor && ballReadyToConvey());
            _conveyorMotor.set(runConveyor ? kConveyorMotorSpeed : 0.0);
            stopShooter();
        }
    }

    private void runShooter(double elapsed) { 
        if (elapsed < kFlywheelRampTime) { // ramp time
            _flyWheelMotor.set(1); // Max speed to get flywheel up to speed as quickly as possible
            _conveyorMotor.set(0);
        } else {
            _flyWheelMotor.set(kFlyWheelMotorSpeed);
            _conveyorMotor.set(kConveyorMotorSpeed);
        }
    }

    private void stopShooter() {
        _flyWheelMotor.set(0);
    }

    public void autonomousPeriodic() {
        if (_action == null) {
            _conveyorMotor.set(ballReadyToConvey() ? kConveyorMotorSpeed : 0.0);
        } else if (_action instanceof Shoot) {
            Shoot shoot = (Shoot)_action;
            double elapsed = _timer.get();
            if (elapsed < shoot.getTime()) {
                runShooter(elapsed);
            } else {
                stopShooter();
                completed();
            }
        } else if (_action instanceof StartIntake) {
            _intakeMotor.set(kConveyorMotorSpeed);
            completed();
        } else if (_action instanceof StopIntake) {
            _intakeMotor.set(0);
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
            _timer.start();
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
