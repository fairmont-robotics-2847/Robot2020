package frc.robot;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Drive {
    private IDriveDelegate _delegate;

    Drive(IDriveDelegate delegate) {
        _delegate = delegate;
    }
    
    WPI_TalonSRX _frontRight = new WPI_TalonSRX(1);
    WPI_VictorSPX _rearRight = new WPI_VictorSPX(1);
    WPI_TalonSRX _frontLeft = new WPI_TalonSRX(0);
	WPI_VictorSPX _rearLeft = new WPI_VictorSPX(0);
    SpeedControllerGroup _left = new SpeedControllerGroup(_frontLeft, _rearLeft);    
    SpeedControllerGroup _right = new SpeedControllerGroup(_frontRight, _rearRight);
    DifferentialDrive _drive = new DifferentialDrive(_left, _right);
    ADXRS450_Gyro _gyro = new ADXRS450_Gyro(SPI.Port.kOnboardCS0);

    public void init() {
        _gyro.calibrate();
        _gyro.reset();
        resetPosition();
        /*_frontLeft.configFactoryDefault();
		_rearLeft.configFactoryDefault();
		_frontRight.configFactoryDefault();
		_rearRight.configFactoryDefault();*/
    }

    private void reportDiagnostics() {
        SmartDashboard.putNumber("R-Quad", _frontRight.getSensorCollection().getQuadraturePosition());
        SmartDashboard.putNumber("L-Quad", _frontLeft.getSensorCollection().getQuadraturePosition());
        SmartDashboard.putNumber("Rot", _gyro.getAngle());
        SmartDashboard.putNumber("T Pos", _targetPosition);
        SmartDashboard.putNumber("T Rot", _targetRotation);
    }

    public void teleopPeriodic(double speed, double rotation) {
        _drive.arcadeDrive(speed, rotation);
        reportDiagnostics();
    }

    // Autonomous functionality

    public void autonomousPeriodic() {
        double position = getPosition();
        if (_targetPosition > 0 && position < _targetPosition) {
            _drive.arcadeDrive(0.4, 0);
        } else if (_targetPosition < 0 && position > _targetPosition) {
            _drive.arcadeDrive(-0.4, 0);
        } else if (_targetPosition != 0) {         
            _targetPosition = 0;
            resetPosition();
            _delegate.operationComplete();
        }

        double rotation = getRotation();
        if (_targetRotation > 0 && rotation < _targetRotation) {
            _drive.arcadeDrive(0, 0.4);
        } else if (_targetRotation < 0 && rotation > _targetRotation) {
            _drive.arcadeDrive(0, -0.4);
        } else if (_targetRotation != 0) {
            _targetRotation = 0;
            _delegate.operationComplete();
        }

        if (_targetPosition == 0 && _targetRotation == 0) {
            _drive.arcadeDrive(0, 0);
        }

        reportDiagnostics();
    }

    public void move(double feet) {
        resetPosition();
        _targetPosition = feet;
    }

    private double _targetPosition;

    private double getPosition() {
        int rightPos = _frontRight.getSensorCollection().getQuadraturePosition(); // positive going forward
        int leftPos = _frontLeft.getSensorCollection().getQuadraturePosition(); // negative going forward
        return ((rightPos - leftPos) / 2) / 2434; // 2434 obtained through experimentation
    }

    private void resetPosition() {
        _frontRight.getSensorCollection().setQuadraturePosition(0, 10000);
        _frontLeft.getSensorCollection().setQuadraturePosition(0, 10000);
    }

    public void rotate(double degrees) {
        resetRotation();
        double anticipation = degrees > 0 ? -4 : 4;
        _targetRotation = degrees + anticipation;
    }

    private double _targetRotation;

    private double getRotation() {
        return _gyro.getAngle();
    }

    private void resetRotation() {
        _gyro.reset();
    }
}