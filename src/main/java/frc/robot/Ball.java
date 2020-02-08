package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.DigitalInput;

public class Ball implements IActor {
    WPI_VictorSPX _victor2 = new WPI_VictorSPX(2);
    WPI_VictorSPX _victor3 = new WPI_VictorSPX(3);
    WPI_VictorSPX _victor4 = new WPI_VictorSPX(4);
    DigitalInput input0 = new DigitalInput(0);
    DigitalInput input1 = new DigitalInput(1);

    public void init() {

    }
    
    public void teleopPeriodic(boolean intake, boolean expel){ 
        double intakeSpeed = 1;
		
        if (input0.get() == false || (input1.get() == false)) {
			_victor2.set(intakeSpeed);
			_victor3.set(intakeSpeed);
        } else {
			_victor2.set(0);
			_victor3.set(0);
		}
    }

    public void autonomousPeriodic() {
        // TODO:
    }

    public boolean perform(IAction action) {
        return false; // TODO:
    }
}


