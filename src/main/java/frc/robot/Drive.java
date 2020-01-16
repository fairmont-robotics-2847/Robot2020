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
    private WPI_TalonSRX _frontRight = new WPI_TalonSRX(1);
    private WPI_VictorSPX _rearRight = new WPI_VictorSPX(1);
    private WPI_TalonSRX _frontLeft = new WPI_TalonSRX(0);
	private WPI_VictorSPX _rearLeft = new WPI_VictorSPX(0);
    private SpeedControllerGroup _left = new SpeedControllerGroup(_frontLeft, _rearLeft);    
    private SpeedControllerGroup _right = new SpeedControllerGroup(_frontRight, _rearRight);
    private DifferentialDrive _drive = new DifferentialDrive(_left, _right);
    private ADXRS450_Gyro _gyro = new ADXRS450_Gyro(SPI.Port.kOnboardCS0);
    private PigeonIMU _gyroPIMU = new PigeonIMU(3);
    private boolean _moving;
    private boolean _rotating;
    private int _startPosition;
    Heading _heading = new Heading();
    private double _targetRotation;

    Drive(IDriveDelegate delegate) {
        _delegate = delegate;
    }

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

    }

    public void teleopPeriodic(double speed, double rotation) {
        _drive.arcadeDrive(speed, rotation);
        
        reportDiagnostics();
    }

    // Autonomous functionality

    public void autonomousPeriodic() {
        if (_moving) {
            double angle = _heading.getHeading(getPosition(), getRotation());
            SmartDashboard.putNumber("Distance", _heading.getDistance());
            SmartDashboard.putNumber("Angle", angle);
            if (_heading.getDistance() > 0) {
                resetPosition();
                resetRotation();
                // The amount that we turn is proportional to the error in our heading
                _drive.arcadeDrive(0.4, clip(angle / 10, -1.0, 1.0));
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

    private double clip(double value, double min, double max) {
        return Math.max(Math.min(value, max), min);
    }

    public void doAction(Action action) {
        double amount = action.getAmount();
		switch (action.getType()) {
			case kMove: move(amount); break;
			case kRotate: rotate(amount); break;
        }
    }

    private void move(double feet) {
        resetPosition();
        resetRotation();
        _heading.start(feet);
        _moving = true;
    }

    private double getPosition() {
        int position = _frontRight.getSensorCollection().getQuadraturePosition() - _startPosition;
        return position / 2434.0; // 2434 obtained through experimentation
    }

    private void resetPosition() {
        _startPosition = _frontRight.getSensorCollection().getQuadraturePosition();
    }

    private void rotate(double degrees) {
        resetRotation();
        double anticipation = degrees > 0 ? -7 : 7;
        _targetRotation = degrees + anticipation;
        _rotating = true;
    }

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