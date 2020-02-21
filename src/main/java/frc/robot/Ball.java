package frc.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
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
    Timer _timer2 = new Timer();
    boolean _previousShoot = false;
    boolean _useBallSensor = true;
    boolean _previousConveyor = false;
    boolean _currentlyRunningConveyor = false;
    public void setUseBallSensor(boolean value) {
        _useBallSensor = value;
    }

    public boolean getUseBallSensor() {
        return _useBallSensor;
    }

    static final double kIntakeMotorSpeed = 1.0;
    static final double kConveyorMotorSpeed = 0.7;
    static final double kFlyWheelMotorSpeed = 1.0;
    static final double kShootConveyorSpeed = 0.7;

    static final double kConveyorSensorDelay = 0.275; // in seconds
    static final double kFlywheelRampTime = 1.0; // in seconds

    DigitalInput[] _ballReadyToConvey = {
        new DigitalInput(0), // prox. positioned near "back" of the ball in the path
        new DigitalInput(1)  // prox. positioned near "front" of the ball in the path
    };

    boolean ballReadyToConvey() {
        return !_ballReadyToConvey[0].get();  // sensors output false when detecting balls
        //return false;
    }

    public void init() {
        _conveyorMotor.set(0);
        _flyWheelMotor.set(0);
        _intakeMotor.set(0);

        _conveyorMotor.setInverted(true);
        _intakeMotor.setInverted(true);

        _conveyorMotor.setNeutralMode(NeutralMode.Brake);
        _flyWheelMotor.setNeutralMode(NeutralMode.Coast);
        _intakeMotor.setNeutralMode(NeutralMode.Coast);
    }
    
    public void teleopPeriodic(boolean intake, boolean shoot, boolean reverseIntake, boolean advanceBall, boolean reverseBall) { 
        SmartDashboard.putBoolean("Use Ball Sensor", _useBallSensor);
        SmartDashboard.putBoolean("Ball Sens 1", _ballReadyToConvey[0].get());
        //SmartDashboard.putBoolean("Ball Sens 2", _ballReadyToConvey[1].get());
        
        if (shoot && !_previousShoot) {
            System.out.println("Start shooting");
            _timer.start();
        }

        boolean runConveyor = advanceBall || (_useBallSensor && ballReadyToConvey());
        if ((runConveyor && !_previousConveyor) && !reverseBall) {
            System.out.println("Start conveyor");
            _timer2.start();
        }


        _previousShoot = shoot;
        _previousConveyor = runConveyor;
        double intakeMotorSpeed;
        if (reverseIntake) intakeMotorSpeed = -kIntakeMotorSpeed;
        else if (intake) intakeMotorSpeed = kIntakeMotorSpeed;
        else intakeMotorSpeed = 0;
        _intakeMotor.set(intakeMotorSpeed);

        if (shoot) {
            runShooter(_timer.get());
            SmartDashboard.putNumber("time", _timer.get());
        } else {
            stopShooter();
        }

        if (reverseBall) {
            _conveyorMotor.set(-kConveyorMotorSpeed);
        } else if (runConveyor || _currentlyRunningConveyor) {
            _currentlyRunningConveyor = runConveyor(_timer2.get());
        } else if (!shoot) {
            _conveyorMotor.set(0);
        }
    }
    private boolean runConveyor(double elapsed) {
        if (elapsed < kConveyorSensorDelay) {
            _conveyorMotor.set(kConveyorMotorSpeed);
            return true;
        } else {
            return false;
        }
    }

    private void runShooter(double elapsed) { 
        if (elapsed < kFlywheelRampTime) { // ramp time
            _flyWheelMotor.set(1); // Max speed to get flywheel up to speed as quickly as possible
            _conveyorMotor.set(0);
        } else {
            _flyWheelMotor.set(kFlyWheelMotorSpeed);
            _conveyorMotor.set(kShootConveyorSpeed);
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
            if (elapsed < shoot.getDuration()) {
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
