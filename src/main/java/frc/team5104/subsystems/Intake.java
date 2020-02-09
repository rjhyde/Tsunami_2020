package frc.team5104.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import frc.team5104.Constants;
import frc.team5104.Ports;
import frc.team5104.Superstructure;
import frc.team5104.Superstructure.Mode;
import frc.team5104.Superstructure.SystemState;
import frc.team5104.util.managers.Subsystem;

public class Intake extends Subsystem {
	private static VictorSPX victor;
	private static DoubleSolenoid piston;
	
	//Loop
	public void update() {
		setPiston(true);
		if (Superstructure.getSystemState() == SystemState.AUTOMATIC ||
			Superstructure.getSystemState() == SystemState.MANUAL) {
			if (Superstructure.getMode() == Mode.INTAKE) {
				setPercentOutput(Constants.INTAKE_SPEED);
			}
			else stop();
		}
		else stop();
	}

	//Internal Functions
	public void setPiston(boolean position) {
		piston.set(position ? Value.kForward : Value.kReverse);
	}
	public void setPercentOutput(double percent) {
		victor.set(ControlMode.PercentOutput, percent);
	}
	public void stop() {
		setPiston(false);
		victor.set(ControlMode.Disabled, 0.0);
	}

	//Config
	public void init() {
		piston = new DoubleSolenoid(Ports.INTAKE_DEPLOYER_FORWARD, Ports.INTAKE_DEPLOYER_REVERSE);
		
		victor = new VictorSPX(Ports.INTAKE_VICTOR);
		victor.configFactoryDefault();
	}

	//Reset
	public void reset() {
		stop();
	}
}