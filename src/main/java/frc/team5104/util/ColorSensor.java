package frc.team5104.util;

import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.I2C;

/**
 * A wrapper class for the REV Color Sensor V3 that rounds the color to the nearest of the constant colors.
 */
public class ColorSensor {
	public static enum PanelColor { 
		RED, GREEN, BLUE, YELLOW;
		public static PanelColor fromChar(char c) {
			if (c == 'R') return PanelColor.RED;
			if (c == 'G') return PanelColor.GREEN;
			if (c == 'B') return PanelColor.BLUE;
			return PanelColor.YELLOW;
		}
	}
	private static PercentColor[] colors = {
		new PercentColor(0.418, 0.395, 0.187),
		new PercentColor(0.211, 0.528, 0.260),
		new PercentColor(0.173, 0.485, 0.388),
		new PercentColor(0.311, 0.543, 0.146)
	};
	
	//
	private ColorSensorV3 colorSensor;
	
	//Constructors
	public ColorSensor() {
		colorSensor = new ColorSensorV3(I2C.Port.kOnboard);
	}
	
	//Getters
	public PanelColor getNearestColor() {
		PercentColor color = new PercentColor(colorSensor.getRed(), colorSensor.getGreen(), colorSensor.getBlue());
		int closestIndex = 0;
		for (int i = 0; i < colors.length; i++) {
			if (colors[i].distance(color) < colors[closestIndex].distance(color))
				closestIndex = i;
		}
		return PanelColor.values()[closestIndex];
	}
	
	//Percent Color
	private static class PercentColor {
		double r, g, b;
		public PercentColor(double r, double g, double b) {
			this.r = r;
			this.g = g;
			this.b = b;
		}
		public double distance(PercentColor other) {
			return Math.abs(r - other.r) + Math.abs(g - other.g) + Math.abs(b - other.b);
		}
		public String toString() {
			return "color: (r: " + r + ", g: " + g + ", b: " + b + ")";
		}
	}
}