package frc.robot;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.SpeedControllerGroup;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;

import edu.wpi.first.wpilibj.Timer;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Drive implements IActor {
    Drive(ICommander commander) {
        _commander = commander;
        _action = null;
        _positionRef = 0;
    }
    
    ICommander _commander;
    WPI_TalonSRX _frontRight = new WPI_TalonSRX(0);
    WPI_VictorSPX _rearRight = new WPI_VictorSPX(0);
    WPI_TalonSRX _frontLeft = new WPI_TalonSRX(1);
    WPI_VictorSPX _rearLeft = new WPI_VictorSPX(1);

    Timer _timer = new Timer();

    SpeedControllerGroup _left = new SpeedControllerGroup(_frontLeft, _rearLeft);    
    SpeedControllerGroup _right = new SpeedControllerGroup(_frontRight, _rearRight);
    DifferentialDrive _drive = new DifferentialDrive(_left, _right);
    ADXRS450_Gyro _gyro = new ADXRS450_Gyro(SPI.Port.kOnboardCS0);
    PigeonIMU _gyroPIMU = new PigeonIMU(0);
    IAction _action;
    int _positionRef;

    private static final double kTurnAnticipationAngle = 30.0;
    private static final double kTurnFastSpeed = 0.65;
    private static final double kTurnSlowSpeed = 0.4;

    private static final double kMoveAnticipationDistance = 2.0;
    private static final double kMoveFastSpeed = 0.59;
    private static final double kMoveSlowSpeed = 0.41;
    private static final double kMoveTurnAdjustment = 0.225;
    private static final double kClosedLoopRampTime = 0.5; // in seconds
    private static final double kOpenLoopRampTime = 0.5; // in seconds

    public void init() {
        _gyro.calibrate();
        _gyro.reset();

        _frontRight.configClosedloopRamp(kClosedLoopRampTime);
        _frontLeft.configClosedloopRamp(kClosedLoopRampTime);
        _rearRight.configClosedloopRamp(kClosedLoopRampTime);
        _rearLeft.configClosedloopRamp(kClosedLoopRampTime);

        _frontRight.configOpenloopRamp(kOpenLoopRampTime);
        _frontLeft.configOpenloopRamp(kOpenLoopRampTime);
        _rearRight.configOpenloopRamp(kOpenLoopRampTime);
        _rearLeft.configOpenloopRamp(kOpenLoopRampTime);
    }

    public void initTeleop() {
        _frontRight.setNeutralMode(NeutralMode.Coast);
        _frontLeft.setNeutralMode(NeutralMode.Coast);
        _rearRight.setNeutralMode(NeutralMode.Coast);
        _rearLeft.setNeutralMode(NeutralMode.Coast);
    }

    public void initAuto() {
        _frontRight.setNeutralMode(NeutralMode.Brake);
        _frontLeft.setNeutralMode(NeutralMode.Brake);
        _rearRight.setNeutralMode(NeutralMode.Brake);
        _rearLeft.setNeutralMode(NeutralMode.Brake);
    }

    public void teleopPeriodic(double speed, double rotation) {
        _drive.arcadeDrive(speed * 0.7, rotation * 0.7);
        reportDiagnostics();
    }

    // Autonomous functionality
    public void autonomousPeriodic() {
        if (_action == null) {
            _drive.arcadeDrive(0, 0);
        } else if (_action instanceof Move) {
            Move move = (Move)_action;
            double position = getPosition();
            double heading = getHeading();
            
            // Slow down when we our near the target position
            double speed = Math.abs(move.getGoal() - position) < kMoveAnticipationDistance ? kMoveSlowSpeed : kMoveFastSpeed;

            // Add rotation to correct heading errors
            double rotation;
            if (heading < 0) rotation = kMoveTurnAdjustment;
            else if (heading > 0) rotation = -kMoveTurnAdjustment;
            else rotation = 0;
            
            if (move.getGoal() > 0 && position < move.getGoal()) {
                _drive.arcadeDrive(speed, rotation);
            } else if (move.getGoal() < 0 && position > move.getGoal()) {
                _drive.arcadeDrive(-speed, -rotation);
            } else {
                move.complete(position, heading);
                _action = null;
                _commander.completed((IAction)move);
            }
        } else if (_action instanceof Turn) {
            Turn turn = (Turn)_action;
            double heading = getHeading();
            
            // Slow down when we are near the heading target 
            double rotation = Math.abs(turn.getGoal() - heading) > kTurnAnticipationAngle ? kTurnFastSpeed : kTurnSlowSpeed; // was .4 & .3

            if (turn.getGoal() > 0 && heading < turn.getGoal() - 1.0) {
                _drive.arcadeDrive(0, rotation);
            } else if (turn.getGoal() < 0 && heading > turn.getGoal() + 1.0) {
                _drive.arcadeDrive(0, -rotation);
            } else {
                turn.complete(heading);
                _action = null;
                _commander.completed((IAction)turn);
            }
        } else if (_action instanceof Sleep) {
            Sleep sleep = (Sleep)_action;
            System.out.println(_timer.get());
            if (doneSleeping(sleep.getDuration())) {
                System.out.println("End sleep");
                _action = null;
                _commander.completed((IAction)sleep);
            }
        }
        reportDiagnostics();
    }

    public boolean perform(IAction action) {
        if (action instanceof Move ||
            action instanceof Turn) {
            _action = action;
            resetPosition();
            resetHeading();
            return true;
        } else if (action instanceof Sleep) {
            _action = action;
            System.out.println("Start sleep");
            _timer.start();
            return true;
        } else {
            return false;
        }
    }

    private boolean doneSleeping(double sleep) {
        return _timer.get() > sleep;
    }

    private double getPosition() {
        return (_frontRight.getSensorCollection().getQuadraturePosition() - _positionRef) / 2600.0;
    }

    private void resetPosition() {
        _positionRef = _frontRight.getSensorCollection().getQuadraturePosition();
    }

    private double getHeading() {
        // If using the ADXRS450 Gyro
        //return _gyro.getAngle();
        
        // If using the Pigeon IMU
        double[] ypr = new double[3];
        _gyroPIMU.getYawPitchRoll(ypr);
        return -ypr[0];
    }

    private void resetHeading() {
        // If using the ADXRS450 Gyro
        //_gyro.reset();

        // If using the Pigeon IMU
        _gyroPIMU.setYaw(0);
    }

    private void reportDiagnostics() {
        SmartDashboard.putNumber("R-Quad", _frontRight.getSensorCollection().getQuadraturePosition());
        SmartDashboard.putNumber("L-Quad", _frontLeft.getSensorCollection().getQuadraturePosition());
        SmartDashboard.putNumber("Rot (ADXRS450)", _gyro.getAngle());
        SmartDashboard.putNumber("Rot", getHeading());
    }
}
