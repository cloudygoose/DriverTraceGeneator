package routegen.test;
import java.util.*;
public class DoublePassFilter extends Filter {
	public boolean pass(ArrayList<String> list, Router r) {
		double d = 0;
		HashSet<String> set = new HashSet<String>();
		for (int i = 0;i < list.size();i++) {
			if (set.contains(list.get(i))) {
				d++;
			}
			set.add(list.get(i));
		}
		double sumKm = 0;
		for (int i = 1;i < list.size();i++) {
			double y = Math.abs(r.points.get(list.get(i)).lon - 
					r.points.get(list.get(i - 1)).lon);
			double x = Math.abs(r.points.get(list.get(i)).lat - 
					r.points.get(list.get(i - 1)).lat);
			sumKm += Math.sqrt((y * y * 111.2 * 111.2) + (x * x * 96.3 * 96.3)); 
		}
		if (d / list.size() < 0.05 && sumKm > 5)
			return true;
		else
		return false;
	}
}
