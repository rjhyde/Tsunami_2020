/* BreakerBots Robotics Team (FRC 5104) 2020 */
package frc.team5104.util.managers;

import frc.team5104.util.CrashLogger;
import frc.team5104.util.CrashLogger.Crash;
import frc.team5104.util.console;
import frc.team5104.util.console.c;
import frc.team5104.util.console.t;

/** Manages the calls for all Subsystems given */
public class SubsystemManager {
	private static Subsystem[] targetSubsystems;
	
	/** Tell the Subsystem Manager what Subsystems to manage */
	public static void useSubsystems(Subsystem... availableSubsystems) {
		//Save all subsystems
		targetSubsystems = availableSubsystems;

		//Initialize Subsystem's Interface & Print out target subsystems
		String message = "Running Subsystems: ";
		for (Subsystem subsystem : targetSubsystems) {
			try {
				subsystem.isAttached = true;
				subsystem.init();
				message += subsystem.getClass().getSimpleName() + ", ";
			} catch (Exception e) { CrashLogger.logCrash(new Crash("main", e)); }
		}
		console.log(c.MAIN, t.INFO, message.substring(0, message.length()-2));
	}
	
	/** Call when the robot becomes enabled or disabled */
	public static void reset() {
		for (Subsystem subsystem : targetSubsystems) {
			try {
				subsystem.reset();
			} catch (Exception e) { CrashLogger.logCrash(new Crash("main", e)); }
		}
	}
	
	/** Call periodically when the robot is enabled */
	public static void update() {
		for (Subsystem subsystem : targetSubsystems) {
			try {
				subsystem.update();
			} catch (Exception e) { CrashLogger.logCrash(new Crash("main", e)); }
		}
	}
}
