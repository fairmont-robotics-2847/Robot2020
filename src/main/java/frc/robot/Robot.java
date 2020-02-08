package frc.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.*;

public class Robot extends TimedRobot implements ICommander {
	Joystick _joy = new Joystick(0);
	Drive _drive = new Drive(this);
	Ball _ball = new Ball();

	// Motor controllers should be declared and used in a separate class (e.g. Drive.java or Ball.java)
    /*WPI_VictorSPX _victor5 = new WPI_VictorSPX(5);
    WPI_VictorSPX _victor6 = new WPI_VictorSPX(6);
	WPI_VictorSPX _victor7 = new WPI_VictorSPX(7);*/

	IActor[] _actors = {
		_drive,
		_ball
	};
	SquareStrategy _squareStrategy = new SquareStrategy();
	LineStrategy _lineStrategy = new LineStrategy();
	IStrategy _strategy;
	SendableChooser<Integer> _autoChooser = new SendableChooser<>();

  	public void robotInit() {
		initCamera();
		_drive.init();
		_ball.init();
		_autoChooser.setDefaultOption("Square", 1);
  		_autoChooser.addOption("Line", 2);
		SmartDashboard.putData("Strategy", _autoChooser);
	}

	public void autonomousInit() {
		switch (_autoChooser.getSelected()) {
			case 1: _strategy = _squareStrategy; break;
			case 2: _strategy = _lineStrategy; break;
		}
		if (_strategy.more()) perform(_strategy.next());
	}

	public void autonomousPeriodic() {
		_drive.autonomousPeriodic();
		_ball.autonomousPeriodic();
	}
	
  	public void teleopPeriodic() {
		// Drive control
		double speed = _joy.getY() * -0.7;
		double rotation = _joy.getZ() * 0.7;
		_drive.teleopPeriodic(speed, rotation);
		
		// Ball Control
		boolean intakeBall = _joy.getRawButton(5);
		boolean expelBall = _joy.getRawButton(7);
		_ball.teleopPeriodic(intakeBall, expelBall);
	}

	private void initCamera() {
		//UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
		//camera.setResolution(640, 480);
	}

	public void completed(IAction action) {
		logAction(action);
		_strategy.completed(action);
		if (_strategy.more()) perform(_strategy.next());
	}

	private void perform(IAction action) {
		for (IActor actor : _actors) {
			if (actor.perform(action)) break;
		}
	}

	private void logAction(IAction action) {
		if (action instanceof Move) {
			Move move = (Move)action;
			System.out.println("Move [Travelled: " + move.getTravelled() + "; heading: " + move.getHeading() + "]");
		} else if (action instanceof Turn) {
			Turn turn = (Turn)action;
			System.out.println("Turn [Heading: " + turn.getHeading() + "]");
		}
	}
}