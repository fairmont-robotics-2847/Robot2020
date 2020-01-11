package frc.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.SPI;

public class Gyro {
    ADXRS450_Gyro _gyro = new ADXRS450_Gyro(SPI.Port.kOnboardCS0);

    public Gyro() {
        _gyro.calibrate();
        _gyro.reset();
    }

    public double getAngle() {
        return _gyro.getAngle();
    }
}