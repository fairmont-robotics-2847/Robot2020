package frc.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;

public class Robot extends TimedRobot implements IChoreographer {
	Joystick _joy = new Joystick(0);
	Drive _drive = new Drive(this);
	Ball _ball = new Ball();
	IPerformer[] _performers = {
		_drive,
		_ball
	};
	SquarePerformance _squarePerformance = new SquarePerformance();
	IPerformance _performance;

  public void robotInit() {
		initCamera();
		_drive.init();
		_ball.init();
	}

	public void autonomousInit() {
		_performance = _squarePerformance; // can be substituted with another performance
		if (_performance.more()) perform(_performance.next());
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
		_performance.completed(action);
		if (_performance.more()) perform(_performance.next());
	}

	private void perform(IAction action) {
		for (IPerformer performer : _performers) {
			if (performer.perform(action)) break;
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