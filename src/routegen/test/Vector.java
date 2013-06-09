package routegen.test;

import java.util.Random;

public class Vector {
	public double x, y;
	public Vector(double x1, double y1) {
		x = x1;
		y = y1;
	}
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public static Vector getRandomUnit() {
		double a, b;
		int k = random.nextInt() % 360;
		return new Vector(Math.cos(2 * Math.PI * k / 360), Math.sin(2 * Math.PI * k / 360));
	}
	public static double calMul(Vector v1, Vector v2) {
		return v2.x * v1.x + v2.y * v1.y;
	}
	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}
	public static Random random = new Random(567);
}
