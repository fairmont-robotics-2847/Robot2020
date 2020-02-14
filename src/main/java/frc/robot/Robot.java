package frc.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.*;

public class Robot extends TimedRobot implements ICommander {
	Joystick _joy = new Joystick(0);
	Drive _drive = new Drive(this);
	Ball _ball = new Ball(this);
	Climber _climber = new Climber();

	
	// Motor controllers should be declared and used in a separate class (e.g. Drive.java or Ball.java)
    /*WPI_VictorSPX _victor5 = new WPI_VictorSPX(5);
    WPI_VictorSPX _victor6 = new WPI_VictorSPX(6);
	WPI_VictorSPX _victor7 = new WPI_VictorSPX(7);*/

	IActor[] _actors = {
		_drive,
		_ball
	};

	IStrategy[] _strategies = {
		new SquareStrategy(),
		new LineStrategy()
	};
	
	IStrategy _strategy;
	
	SendableChooser<Integer> _strategyChooser = new SendableChooser<>();

    public void robotInit() {
		initCamera();
		_drive.init();
		_ball.init();
		_strategyChooser.setDefaultOption("Square", 1);
  	    _strategyChooser.addOption("Line", 2);
		SmartDashboard.putData("Strategy", _strategyChooser);
	}

	public void autonomousInit() {
		_ball.init();
		if (_strategyChooser.getSelected() > 0 &&
		    _strategyChooser.getSelected() <= _strategies.length) {
			_strategy = _strategies[_strategyChooser.getSelected() - 1];
			if (_strategy.more()) perform(_strategy.next());
		}
	}

	public void autonomousPeriodic() {
		_drive.autonomousPeriodic();
		_ball.autonomousPeriodic();
	}

	public void teleopInit() {
		_ball.init();
	}

    public void teleopPeriodic() {
		// Drive control
		double speed = -_joy.getY();
		double rotation = _joy.getZ();
		_drive.teleopPeriodic(speed, rotation);
		
		// Ball Control
		boolean intake = _joy.getRawButton(5);
		boolean launch = _joy.getRawButton(7);
		_ball.teleopPeriodic(intake, launch);

		boolean up = _joy.getRawButton(8);
		boolean down = _joy.getRawButton(6);
		Climber.Direction direction;
		if (up) {
			direction = Climber.Direction.up;
		} else if (down) {
			direction = Climber.Direction.down;
		} else {
			direction = Climber.Direction.stopped;
		}
		_climber.teleopPeriodic(direction);
	}


	private void initCamera() {
		// TODO: uncomment these lines when a camera is attached
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
		// Might be helpful for debugging autonomous routines
		if (action instanceof Move) {
			Move move = (Move)action;
			System.out.println("Move [Travelled: " + move.getTravelled() + "; heading: " + move.getHeading() + "]");
		} else if (action instanceof Turn) {
			Turn turn = (Turn)action;
			System.out.println("Turn [Heading: " + turn.getHeading() + "]");
		}
	}
}
