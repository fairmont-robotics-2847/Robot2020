package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

public class Climber {
    WPI_VictorSPX _climberMotor = new WPI_VictorSPX(7);

    static final double kElevatorSpeed = .6;

    public enum Operation {
        Reset,
        Climb,
        stopped
    }

    public void teleopPeriodic(Operation direction) {
		if (direction == Operation.Reset) {
			_climberMotor.set(-kElevatorSpeed);
		} else if (direction == Operation.Climb) {
			_climberMotor.set(kElevatorSpeed);
		} else {
			_climberMotor.set(-0.1); // This is because of climber slowly rising
		}
    }     
}