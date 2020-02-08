package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.DigitalInput;

public class Ball implements IActor {
    WPI_VictorSPX _intakeMotor = new WPI_VictorSPX(2);
    WPI_VictorSPX _conveyorMotor = new WPI_VictorSPX(3);
    WPI_VictorSPX _flyWheelMotor = new WPI_VictorSPX(4);

    static final double kIntakeMotorSpeed = 1.0;
    static final double kConveyorMotorSpeed = 1.0;
    static final double kFlyWheelMotorSpeed = 0.7;

    DigitalInput[] _ballReadyToConvey = {
        new DigitalInput(0), // prox. positioned near "back" of the ball in the path
        new DigitalInput(1)  // prox. positioned near "front" of the ball in the path
    };

    boolean ballReadyToConvey() {
        return _ballReadyToConvey[0].get() && _ballReadyToConvey[1].get();
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
    }

    public boolean perform(IAction action) {
        return false; // TODO: define actions that this actor can perform (e.g. run ball intake, launch ball, etc.)
    }
}
