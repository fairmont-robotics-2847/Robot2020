package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {
	// Driver control
	Joystick _joy = new Joystick(0);
	// Drive
	Drive _drive = new Drive();
	//Ball Intake and Expel
	Ball _ball = new Ball();
	// Rotation
	Gyro _gyro = new Gyro();


	//Talon _motor = new Talon(0);

  	public void robotInit() {
		initCamera();
		_drive.init();
		_ball.init();
		UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
		camera.setResolution(640, 480);
	}

	public void autonomousPeriodic() {
		// TODO: implement autonomous operation
	}
	
  	public void teleopPeriodic() {
		// Drive control
		double speed = _joy.getY() * -.7;
		double rotation = _joy.getZ() * .7;
		_drive.set(speed, rotation);
		
		//Ball Control
		boolean intakeBall = _joy.getRawButton(5);
		boolean expelBall = _joy.getRawButton(7);
		_ball.set(intakeBall, expelBall);

		//_motor.set(_joy.getY());

		// SmartDashboard
		SmartDashboard.putNumber("Gyro Output", _gyro.getAngle());
	}

	private void initCamera() {
		UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
		camera.setResolution(256, 144);
	}
}