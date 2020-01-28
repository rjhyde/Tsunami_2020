/*BreakerBots Robotics Team 2020*/
package frc.team5104;

import frc.team5104.subsystems.Hopper;
import frc.team5104.subsystems.Paneler;
import frc.team5104.util.console;
import frc.team5104.util.console.c;

/** 
 * The Superstructure is a massive state machine that handles the Panel, Climb, and of course, Ballz
 * The Superstructure only controls the states... its up the subsystems to figure out what to do
 * based on the state of the Superstructure.
 */
public class Superstructure {
	//States and Variables
	public static enum SystemState { 
		DISABLED, CALIBRATING, MANUAL, AUTOMATIC 
	}
	public static enum Mode {
		IDLE, INTAKE, SHOOTING, UNJAM, CLIMBING, 
		PANELING, PANEL_DEPLOYING
	}
	public static enum PanelState { ROTATION, POSITION };
	public static enum FlywheelState { STOPPED, SPINNING };
	public static enum Target { LOW, HIGH };
	
	private static SystemState systemState = SystemState.DISABLED;
	private static Mode mode = Mode.IDLE;
	private static PanelState panelState = PanelState.ROTATION;
	private static FlywheelState shooterWheelState = FlywheelState.STOPPED;
	private static Target target = Target.HIGH;
	
	//External Functions
	public static SystemState getSystemState() { return systemState; }
	public static void setSystemState(SystemState systemState) {  Superstructure.systemState = systemState; }
	public static Mode getMode() { return mode; }
	public static void setMode(Mode mode) { Superstructure.mode = mode; }
	public static void setPanelState(PanelState panelState) { Superstructure.panelState = panelState; }
	public static PanelState getPanelState() { return panelState; }
	public static void setFlywheelState(FlywheelState shooterWheelState) { Superstructure.shooterWheelState = shooterWheelState; }
	public static FlywheelState getFlywheelState() { return shooterWheelState; }
	public static void setTarget(Target target) { Superstructure.target = target; }
	public static Target getTarget() { return target; }
	
	//Loop
	protected static void update() {

		//Exit Paneling
		if (Superstructure.getMode() == Mode.PANELING && Paneler.isFinished()) {
			Superstructure.setMode(Mode.IDLE);
			console.log(c.SUPERSTRUCTURE, "finished paneling... idling");
		}
		
		//Exit Intake
		if (getMode() == Mode.INTAKE && Hopper.isFull()) {
			setMode(Mode.IDLE);
			console.log(c.SUPERSTRUCTURE, "Hopper full! No mas!!");
		}
		
		//Spin Flywheel while Shooting
		if (getMode() == Mode.SHOOTING) {
			setFlywheelState(FlywheelState.SPINNING);
		}
	}
	
	//Reset
	protected static void reset() {
		setSystemState(SystemState.CALIBRATING); 
		setMode(Mode.IDLE);
		setPanelState(PanelState.ROTATION);
		setFlywheelState(FlywheelState.STOPPED);
		setTarget(Target.HIGH);
	}
}