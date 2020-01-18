package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

public class Ball implements IPerformer {
    WPI_VictorSPX _ballControl = new WPI_VictorSPX(6);

    public void init() {

    }
    
    public void teleopPeriodic(boolean intake, boolean expel){
        if (intake) {
			_ballControl.set(0.5);
		} else if (expel) {
			_ballControl.set(-0.5);
		} else {
			_ballControl.set(0);
		}
    }

    public void autonomousPeriodic() {
        // TODO:
    }

    public boolean perform(IAction action) {
        return false; // TODO:
    }
}


