package frc.team5104.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import frc.team5104.Constants;
import frc.team5104.Ports;
import frc.team5104.Superstructure;
import frc.team5104.Superstructure.Mode;
import frc.team5104.Superstructure.SystemState;
import frc.team5104.Superstructure.Target;
import frc.team5104.util.PositionController;
import frc.team5104.util.Tuner;
import frc.team5104.util.console;
import frc.team5104.util.console.c;
import frc.team5104.util.Limelight;
import frc.team5104.util.BreakerMath;
import frc.team5104.util.Filer;
import frc.team5104.util.MovingAverage;
import frc.team5104.util.managers.Subsystem;

public class Hood extends Subsystem {
	private static TalonSRX motor;
	private static MovingAverage visionFilter;
	private static PositionController controller;
	private static double targetAngle = 0;//, tunerTargetAngle;

	//Loop
	public void update() {
		//Automatic
		if (Superstructure.getSystemState() == SystemState.AUTOMATIC) {
			//Calibrating
			if (isCalibrating()) {
				if (getTimeInCalibration() > 15000)
					setPercentOutput(-Constants.HOOD_CALIBRATE_SPEED);
				else emergencyStop();
			}
			
			//Low
			else if (Superstructure.getTarget() == Target.LOW)
				setTargetAngle(40);
			
			//Vision
			else if (Superstructure.getMode() == Mode.AIMING || Superstructure.getMode() == Mode.SHOOTING) {
				//setTargetAngle(tunerTargetAngle); //TODO DELETE ME!!!
				if (Limelight.hasTarget()) {
					visionFilter.update(Limelight.getTargetY());
					setTargetAngle(getTargetVisionAngle());
				}
			}
				
			//Pull Back
			else setTargetAngle(-1);
			
			setVoltage(controller.calculate(getAngle(), targetAngle));
		}
			
		//Disabled
		else stop();
		
		//Zero
		if (backLimitHit()) {
			resetEncoder();
			motor.configForwardSoftLimitEnable(true);
		}
		controller.setPID(getKP(), 0, Constants.HOOD_KD);
	}
	
	//Fast Loop
	public void fastUpdate() {
		if (isCalibrating() && backLimitHit()) {
			console.log(c.HOOD, "finished calibration!");
			stopCalibrating();
		}
	}
	
	//Debugging
	public void debug() {
		Tuner.setTunerOutput("Hood Angle", getAngle());
		Tuner.setTunerOutput("Hood Output", controller.getLastOutput());
		Tuner.setTunerOutput("Hood FF", controller.getLastFFOutput());
		Tuner.setTunerOutput("Hood PID", controller.getLastPIDOutput());
		Tuner.setTunerOutput("Hood KP", getKP());
		Constants.HOOD_KD = Tuner.getTunerInputDouble("Hood KD", Constants.HOOD_KD);
		//tunerTargetAngle = Tuner.getTunerInputDouble("Hood Target Vision Angle", 10);
	}

	//Internal Functions
	private void setTargetAngle(double degrees) {
		targetAngle = BreakerMath.clamp(degrees, -1, 40);
	}
	private void setVoltage(double volts) {
		volts = BreakerMath.clamp(volts, -6, 6); //TODO DELETE ME!!
		setPercentOutput(volts / motor.getBusVoltage());
	}
	private void setPercentOutput(double percent) {
		motor.set(ControlMode.PercentOutput, percent);
	}
	private void stop() {
		motor.set(ControlMode.Disabled, 0);
	}
	private void resetEncoder() {
		motor.setSelectedSensorPosition(0);
	}
	private double getKP() {
		double x = getAngle();
		return -0.000250 * x * x * x + 0.0136 * x * x - 0.209 * x + 1.5;
	}

	//External Functions
	public static double getAngle() {
		if (motor == null) return 0;
		return motor.getSelectedSensorPosition() / Constants.HOOD_TICKS_PER_REV * 360.0;
	}
	public static boolean backLimitHit() {
		if (motor == null) return true;
		return !motor.getSensorCollection().isRevLimitSwitchClosed();
	}
	public static boolean onTarget() {
		if (motor == null) return true;
		return Math.abs(getAngle() - getTargetVisionAngle()) < Constants.HOOD_TOL;
	}
	public static double getTargetVisionAngle() {
		double x = visionFilter.getDoubleOutput();
		return -0.00643 * x * x * x - 0.122 * x * x - 0.934 * x + 7.715;
	}

	//Config
	public void init() {
		motor = new TalonSRX(Ports.HOOD_MOTOR);
		motor.configFactoryDefault();
		motor.setInverted(false);
		motor.setSensorPhase(true);
		
		motor.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyClosed);
		motor.configForwardSoftLimitThreshold((int) (Constants.HOOD_TICKS_PER_REV * (38.0 / 360.0)));
		motor.configForwardSoftLimitEnable(false);
		
		controller = new PositionController(
				getKP(),
				0,
				Constants.HOOD_KD,
				Constants.HOOD_MAX_VEL,
				Constants.HOOD_MAX_ACC,
				Constants.HOOD_KS,
				Constants.HOOD_KV,
				Constants.HOOD_KA
			);
		visionFilter = new MovingAverage(3, 0);
		
		//Always calibrate at comp. Only calibrate once per roborio boot while not.
		if (Constants.AT_COMPETITION || !Filer.fileExists("/tmp/hood_calibrated.txt")) {
			Filer.createFile("/tmp/hood_calibrated.txt");
			startCalibrating();
			console.log(c.HOOD, "ready to calibrate!");
		}
	}

	//Reset
	public void disabled() {
		stop();
	}
}