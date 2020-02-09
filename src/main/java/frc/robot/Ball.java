package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.DigitalInput;

import edu.wpi.first.wpilibj.Timer;

public class Ball implements IActor {
    /* TODO: define a constructor that takes an ICommander as a parameter (see the constructor for
       Drive as an example) */

    WPI_VictorSPX _intakeMotor = new WPI_VictorSPX(2);
    WPI_VictorSPX _conveyorMotor = new WPI_VictorSPX(3);
    WPI_VictorSPX _flyWheelMotor = new WPI_VictorSPX(4);
    
    Timer _timer = new Timer();

    static final double kIntakeMotorSpeed = -1.0;
    static final double kConveyorMotorSpeed = 1.0;
    static final double kFlyWheelMotorSpeed = 0.7;

    DigitalInput[] _ballReadyToConvey = {
        new DigitalInput(0), // prox. positioned near "back" of the ball in the path
        new DigitalInput(1)  // prox. positioned near "front" of the ball in the path
    };

    boolean ballReadyToConvey() {
        return !_ballReadyToConvey[0].get() && !_ballReadyToConvey[1].get();  // sensors output false when detecting balls
    }

    public void init() {

    }
    
    public void teleopPeriodic(boolean intake, boolean launch){ 
        boolean runIntake = intake;
        boolean runConveyor = ballReadyToConvey() || launch;
        boolean runFlyWheel = launch;

        _intakeMotor.set(runIntake ? kIntakeMotorSpeed : 0.0);
        _conveyorMotor.set(runConveyor ? kConveyorMotorSpeed : 0.0);
        _flyWheelMotor.set(runFlyWheel ? kFlyWheelMotorSpeed : 0.0);
    }

    public void autonomousPeriodic() {
        // TODO: define autonomous operation
        /* TODO: call complete(IAction) on the ICommander (set in the constructor) when an action
           is completed */
    }

    public boolean perform(IAction action) {
        return false;
    }

    boolean firstRun = true;
    /* TODO: performance parameters (e.g. speed and duration) should be stored in the Action (in
       this case, Shoot.java), not passed in as parameters to perform since the signature for the
       perform method on an IActor takes a single parameter (i.e. an IAction). this code should
       either be moved into the perform(IAction) above, or the parameters should be removed from
       this method's signature and the other perform method above should be removed. */
    public boolean perform(IAction action, double speed, double duration) {
        if (action instanceof Shoot) {
            // TODO: when Shooting a ball, I believe that the conveyor should also run
            double startingTime = firstRun ? _timer.get() : -1;
            if (startingTime != -1) {firstRun = false;}

            if (_timer.get() < startingTime + duration) {
                _flyWheelMotor.set(speed);
                return false;
            } else {
                _flyWheelMotor.set(0);
                return true;
            }
        /* TODO: we also need a set of actions for ball intake (maybe something like StartIntake
           and StopIntake). When picking up balls, the intake motor should run, but the conveyor
           should only run when indexing a ball into the path, which the robot should do when it
           senses that a ball is in position (using the proximity sensors: see ballReadyToConvey). */
        } else {
            return false;
        }
    }

    /* TODO: does the flywheel speed need to be adjusted, or can this be set as a constant (i.e.
       see kFlyWheelMotorSpeed defined above)? */
    public void setFlyWheelSpeed(double speed) {
        _flyWheelMotor.set(speed);
    }
}
