package frc.robot;

import java.util.Queue;

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
import frc.robot.Action.ActionType;

import edu.wpi.first.wpilibj.Timer;

public class Robot extends TimedRobot implements IDriveDelegate {
	// Driver input
	Joystick _joy = new Joystick(0);
	// Drive
	Drive _drive = new Drive(this);
	//Ball Intake and Expel
	Ball _ball = new Ball();
	//
	Action[] _autoActions = {
		new Action(ActionType.kMove, 3),
		new Action(ActionType.kRotate, 90),
		new Action(ActionType.kMove, 3),
		new Action(ActionType.kRotate, 90),
		new Action(ActionType.kMove, 3),
		new Action(ActionType.kRotate, 90),
		new Action(ActionType.kMove, 3),
		new Action(ActionType.kRotate, 90)
	};
	int _autoActionStep = 0;

  	public void robotInit() {
		initCamera();
		_drive.init();
		_ball.init();
	}

	public void autonomousInit() {
		nextStep(_autoActionStep = 0);
	}

	public void autonomousPeriodic() {
		_drive.autonomousPeriodic();

		reportDiagnostics();
	}
	
  	public void teleopPeriodic() {
		// Drive control
		double speed = _joy.getY() * -.7;
		double rotation = _joy.getZ() * .7;
		_drive.teleopPeriodic(speed, rotation);
		
		//Ball Control
		boolean intakeBall = _joy.getRawButton(5);
		boolean expelBall = _joy.getRawButton(7);
		_ball.set(intakeBall, expelBall);

		reportDiagnostics();
	}

	private void initCamera() {
		//UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
		//camera.setResolution(640, 480);
	}

	public void operationComplete() {
		if (_autoActionStep < _autoActions.length - 1) {
			nextStep(++_autoActionStep);
		}
	}

	private void nextStep(int step) {
		_drive.doAction(_autoActions[step]);
	}

	private void reportDiagnostics() {
		SmartDashboard.putNumber("Step", _autoActionStep);
	}
}