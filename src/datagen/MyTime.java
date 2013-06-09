package datagen;

import java.util.Random;

public class MyTime {
	double seconds;
	static double dayTime = 24 * 60 * 60;
	static int month = 6;
	int day;
	static Random rand = new Random(12345);
	public MyTime(int d, double s, int r) {
		if (r != 0)
			seconds = s + rand.nextInt() % r;
		else
			seconds = s;
		day = d;
	}
	public void setSeconds(double s) {
		seconds = s;
	}
	public void setSecondsWithRange(double s, int r) {
		seconds = s;
		seconds += rand.nextInt() % r;
	}
	public void forward(double s) {
		seconds += s;
	}
	public void forward(double s, int r) {
		seconds += s;
		seconds += rand.nextInt() % r;
		if (seconds > dayTime) {
			seconds -= dayTime;
			day++;
		}
	}
	public void dayForward() {
		day++;
	}
	public String getPrint() {
		int s = (int)Math.round(seconds);
		return "2013-" + month + "-" + day + " " + s / 3600 + ":" + s / 60 % 60 + ":" + s % 60;   
	}
}
