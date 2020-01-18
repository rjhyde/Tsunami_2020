package frc.team5104.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import frc.team5104.util.managers.Subsystem;

public class Intake extends Subsystem {
	private static TalonSRX talon;
	private static DoubleSolenoid piston;
	
	//Loop
	public void update() {
		
	}

	//Internal Functions
	public void setPiston(boolean down) {
		piston.set(down ? Value.kForward : Value.kReverse);
	}
	public void setPercentOutput(double percent) {
		talon.set(ControlMode.PercentOutput, percent);
	}
	public void stop() {
		talon.set(ControlMode.Disabled, 0);
	}
	
	//Config
	public void init() {
		
	}

	//Reset
	public void reset() {
		stop();
	}
}