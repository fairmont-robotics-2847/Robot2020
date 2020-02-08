package frc.robot;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Drive implements IActor {
    Drive(ICommander commander) {
        _commander = commander;
        _action = null;
        _positionRef = 0;
    }
    
    private ICommander _commander;
    WPI_TalonSRX _frontRight = new WPI_TalonSRX(0);
    WPI_VictorSPX _rearRight = new WPI_VictorSPX(0);
    WPI_TalonSRX _frontLeft = new WPI_TalonSRX(1);
    WPI_VictorSPX _rearLeft = new WPI_VictorSPX(1);

    SpeedControllerGroup _left = new SpeedControllerGroup(_frontLeft, _rearLeft);    
    SpeedControllerGroup _right = new SpeedControllerGroup(_frontRight, _rearRight);
    DifferentialDrive _drive = new DifferentialDrive(_left, _right);
    ADXRS450_Gyro _gyro = new ADXRS450_Gyro(SPI.Port.kOnboardCS0);
    PigeonIMU _gyroPIMU = new PigeonIMU(0);
    IAction _action;
    private int _positionRef;

    public void init() {
        _gyro.calibrate();
        _gyro.reset();
        
    }

    public void teleopPeriodic(double speed, double rotation) {
        _drive.arcadeDrive(speed, rotation);
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
            double speed = Math.abs(move.getGoal() - position) < 2.0 ? 0.4 : 0.6;

            // Add rotation to correct heading errors
            double rotation;
            if (heading < 0) rotation = 0.2;
            else if (heading > 0) rotation = -0.2;
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
            double rotation = Math.abs(turn.getGoal() - heading) > 15 ? 0.4 : 0.3;

            if (turn.getGoal() > 0 && heading < turn.getGoal() - 1.0) {
                _drive.arcadeDrive(0, rotation);
            } else if (turn.getGoal() < 0 && heading > turn.getGoal() + 1.0) {
                _drive.arcadeDrive(0, -rotation);
            } else {
                turn.complete(heading);
                _action = null;
                _commander.completed((IAction)turn);
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
        } else {
            return false;
        }
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