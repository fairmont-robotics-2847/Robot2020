package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

public class Climber {
    WPI_VictorSPX _climberMotor = new WPI_VictorSPX(7);

    static final double kElevatorSpeed = .6;

    public enum Direction {
        up,
        down,
        stopped
    }

    public void teleopPeriodic(Direction direction) {
		if (direction == Direction.up) {
			_climberMotor.set(-kElevatorSpeed);
		} else if (direction == Direction.down) {
			_climberMotor.set(kElevatorSpeed);
		} else {
			_climberMotor.set(-0.1);
		}
    }     
}