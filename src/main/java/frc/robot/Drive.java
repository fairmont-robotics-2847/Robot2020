package frc.robot;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

public class Drive{
    WPI_TalonSRX _frontRight = new WPI_TalonSRX(1);
    WPI_VictorSPX _rearRight = new WPI_VictorSPX(1);
    WPI_TalonSRX _frontLeft = new WPI_TalonSRX(0);
	WPI_VictorSPX _rearLeft = new WPI_VictorSPX(0);
    SpeedControllerGroup _left = new SpeedControllerGroup(_frontLeft, _rearLeft);    
    SpeedControllerGroup _right = new SpeedControllerGroup(_frontRight, _rearRight);
    DifferentialDrive _drive = new DifferentialDrive(_left, _right);
    
    public void init(){
        /*_frontLeft.configFactoryDefault();
		_rearLeft.configFactoryDefault();
		_frontRight.configFactoryDefault();
		_rearRight.configFactoryDefault();*/
    }

    public void set(double speed,double rotation) {
        _drive.arcadeDrive(speed, rotation);
    }
}