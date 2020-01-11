package frc.robot;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.ctre.phoenix.sensors.PigeonIMU;

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
    PigeonIMU _gyroPIMU = new PigeonIMU(3);

    public void init() {
        _gyro.calibrate();
        _gyro.reset();
        resetPosition();
        _moving = false;
        _rotating = false;
        /*_frontLeft.configFactoryDefault();
		_rearLeft.configFactoryDefault();
		_frontRight.configFactoryDefault();
		_rearRight.configFactoryDefault();*/
    }

    private void reportDiagnostics() {
        SmartDashboard.putNumber("R-Quad", _frontRight.getSensorCollection().getQuadraturePosition());
        SmartDashboard.putNumber("L-Quad", _frontLeft.getSensorCollection().getQuadraturePosition());
        SmartDashboard.putNumber("Rot (ADXRS450)", _gyro.getAngle());
        SmartDashboard.putNumber("Rot", getRotation());
        SmartDashboard.putNumber("T Pos", _targetPosition);
        SmartDashboard.putNumber("T Rot", _targetRotation);
    }

    public void teleopPeriodic(double speed, double rotation) {
        _drive.arcadeDrive(speed, rotation);
        reportDiagnostics();
    }

    // Autonomous functionality

    boolean _moving;
    boolean _rotating;

    public void autonomousPeriodic() {
        if (_moving) {
            double position = getPosition();
            if (_targetPosition > 0 && position < _targetPosition) {
                _drive.arcadeDrive(0.4, 0);
            } else if (_targetPosition < 0 && position > _targetPosition) {
                _drive.arcadeDrive(-0.4, 0);
            } else {         
                _moving = false;
                _delegate.operationComplete();
            }
        } else if (_rotating) {
            double rotation = getRotation();
            if (_targetRotation > 0 && rotation < _targetRotation) {
                _drive.arcadeDrive(0, 0.4);
            } else if (_targetRotation < 0 && rotation > _targetRotation) {
                _drive.arcadeDrive(0, -0.4);
            } else {
                _rotating = false;
                _delegate.operationComplete();
            }
        } else {
            _drive.arcadeDrive(0, 0);
        }

        reportDiagnostics();
    }

    public void doAction(Action action) {
        _moving = false;
        _targetPosition = 0;
        _rotating = false;
        _targetRotation = 0;
        double amount = action.getAmount();
		switch (action.getType()) {
			case kMove: move(amount); break;
			case kRotate: rotate(amount); break;
        }
    }

    private void move(double feet) {
        resetPosition();
        _targetPosition = feet;
        _moving = true;
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

    private void rotate(double degrees) {
        resetRotation();
        double anticipation = degrees > 0 ? -4 : 4;
        _targetRotation = degrees + anticipation;
        _rotating = true;
    }

    private double _targetRotation;

    private double getRotation() {
        //return _gyro.getAngle();
        double[] ypr = new double[3];
        _gyroPIMU.getYawPitchRoll(ypr);
        return -ypr[0];
    }

    private void resetRotation() {
        //_gyro.reset();
        _gyroPIMU.setYaw(0);
    }
}