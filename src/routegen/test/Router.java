package routegen.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import routegen.draw.Demo;


public class Router {
	HashMap<String, Point> points;
	ArrayList<String> point_arr;
	HashSet<String> cameras;
	HashMap<String, LinkedList<String>> links;
	BufferedWriter output;
	static double sumsumKm = 0;
	static int passedRoute;
	public static Random random = new Random(789);
	public static int camHaving = 0; 
	public static final double EARTH_RADIUS = 6371.0;
	public double cosine(double a1, double a2, double b1, double b2) {
		double b = a1*a1 + a2*a2;
		double c = b1*b1 + b2*b2;
		double a = (b1-a1) * (b1 - a1) + (b2 - a2) * (b2 - a2);
		return (b + c - a) / (2 * Math.sqrt(b) * Math.sqrt(c));
	}
	public ArrayList<String> getRoute(String ss) {
		ArrayList<String> r = new ArrayList<String>();
		r.add(ss);
		Point now = points.get(ss);
		Vector direct = Vector.getRandomUnit();
		int maxStep = 5 + random.nextInt(12); 
		for (int step = 0;step < maxStep;step++) {
			double min = 10000;
			String minx = null;
			if (links.get(ss) == null || links.get(ss).size() == 0) //Not a good node
				return null;
			Iterator<String> iter = links.get(ss).iterator();
			while (iter.hasNext()) {
				String next = iter.next();
				Point now2 = points.get(next);
				Vector d = new Vector(now2.lat - now.lat, now2.lon - now.lon);
				double mul = Vector.calMul(direct, d);
				if (mul < min) {
					min = mul;
					minx = next;
				}
 			}
			ss = minx;
			now = points.get(ss);
			r.add(ss);
		}
		return r;
	}
	public ArrayList<String> getGoodRoute() {
		ArrayList<String> r = new ArrayList<String>();
		int kk = random.nextInt();
		if (kk < 0) kk = -kk;
		String ss = point_arr.get(kk % point_arr.size());
		while (links.get(ss) == null) {
			kk = random.nextInt();
			if (kk < 0) kk = -kk;
			ss = point_arr.get(kk % point_arr.size());
		}
		r.add(ss);
		int maxPath = 1 + random.nextInt(15);
		for (int path = 0;path < maxPath;path++) {
			ArrayList<String> s = getRoute(ss);
			if (s == null) //Outside MinHang
				break;
			ss = s.get(s.size() - 1);
			s.remove(0);
			r.addAll(s);
		}
		return r;	
	}
	public void processRoute(ArrayList<String> rout, String fileName, boolean draw, 
			boolean writeToFile, 
			Filter filter) throws IOException {
		
		
		ArrayList<Point> prout = new ArrayList<Point>();
		double sumKm = 0;
		for (int i = 1;i < rout.size();i++) {
			double y = Math.abs(points.get(rout.get(i)).lon - 
					points.get(rout.get(i - 1)).lon);
			double x = Math.abs(points.get(rout.get(i)).lat - 
					points.get(rout.get(i - 1)).lat);
			sumKm += Math.sqrt((y * y * 111.2 * 111.2) + (x * x * 96.3 * 96.3)); 
		}
		for (int i = 0;i < rout.size();i++)
		{
			prout.add(points.get(rout.get(i)));
			if (cameras.contains(rout.get(i)))
			{
				//System.out.println(fileName + " " + "cam!");
				camHaving++;
			}
		}
		if (filter.pass(rout, this)) {
			System.out.println(fileName + " sumKm : " + sumKm);
			passedRoute++;
			//if (draw)
			//	Demo.drawLines(prout, fileName);
			if (writeToFile) {
				output.write(new Integer(rout.size()).toString());
				output.newLine();
				output.write(new Double(sumKm).toString());
				output.newLine();
				sumsumKm += sumKm;
				for (int i = 0;i < rout.size();i++) {
					double km = 0;
					if (i != rout.size() - 1) {
						/*
						double y = Math.abs(points.get(rout.get(i)).lon - 
								points.get(rout.get(i + 1)).lon);
						double x = Math.abs(points.get(rout.get(i)).lat - 
								points.get(rout.get(i + 1)).lat);
						km = Math.sqrt((y * y * 111.2 * 111.2) + (x * x * 96.3 * 96.3));
						*/
						km = this.calc(points.get(rout.get(i)).lat, points.get(rout.get(i)).lon, 
								points.get(rout.get(i + 1)).lat, points.get(rout.get(i + 1)).lon);
					}
					String str = rout.get(i);
					int isCam = 0;
					if (cameras.contains(str)) 
						isCam = 1;
					output.write(str + " " + points.get(str).lon + " " + points.get(str).lat +
							" " + isCam + " " + km);
					output.newLine();
				}
			}
		}
		
	}
	public void doooo1() throws Exception {
		Test parse = new Test();   
		parse.init();
		//parse.viewXMLtoNodeOutputFile("map.xml");
		parse.readCameraFile();
		parse.readNodeFile();
		parse.viewXMLtoWayOutputFile("map.xml");
		parse.shit();
		points = parse.points;
		cameras = parse.cameras;
		links = parse.links;
		point_arr = new ArrayList<String>();

		Iterator<String> iter = points.keySet().iterator();
		while (iter.hasNext()) point_arr.add(iter.next());
		
		passedRoute = 0;
		
		File f = new File("./NoFilterOutput.txt");
	    output = new BufferedWriter(new FileWriter(f));
	    
	    Integer nnn = 50000;
		output.write("50000");
		output.newLine();
		while (passedRoute < nnn)
			processRoute(getGoodRoute(), "newfile" + passedRoute + ".png", 
					true, true/*File*/,
					new Filter());
		System.out.println("Meeting Cameras : " + camHaving);
		closeOutputFile();
		System.out.println("average Km : " + sumsumKm / nnn);
		/*		

		Demo.drawLines(router.getPointRoute(), "newfile1.png");
		Demo.drawLines(router.getPointRoute(), "newfile2.png");
		Demo.drawLines(router.getPointRoute(), "newfile3.png");
		Demo.drawLines(router.getPointRoute(), "newfile4.png");
		Demo.drawLines(router.getPointRoute(), "newfile5.png");
		Demo.drawLines(router.getPointRoute(), "newfile6.png");
*/
	/*
		ArrayList<Point> points = new ArrayList<Point>();
		points.add(new Point(1, 1));
		points.add(new Point(2, 2));
		points.add(new Point(0, 10));
		points.add(new Point(0, 20));
		Demo.drawLines(points);
	*/
//		System.out.println(Vector.getRandomUnit());

	}

	public void doooo2() throws Exception {
		Test parse = new Test();   
		parse.init();
		//parse.viewXMLtoNodeOutputFile("map.xml");
		parse.readCameraFile();
		parse.readNodeFile();
		parse.viewXMLtoWayOutputFile("map.xml");
		parse.shit();
		points = parse.points;
		cameras = parse.cameras;
		links = parse.links;
		point_arr = new ArrayList<String>();

		Iterator<String> iter = points.keySet().iterator();
		while (iter.hasNext()) point_arr.add(iter.next());
		
		passedRoute = 0;

		File f = new File("./DoublePassFilterOutput.txt");
	    output = new BufferedWriter(new FileWriter(f));
		
	    Integer nnn = 50000;
		output.write("50000");
		output.newLine();
		while (passedRoute < nnn)
			processRoute(getGoodRoute(), "newfile" + passedRoute + ".png", 
					true, true/*File*/,
					new DoublePassFilter());
					//new Filter());
		System.out.println("Meeting Cameras : " + camHaving);
		closeOutputFile();
		System.out.println("average Km : " + sumsumKm / nnn);
	}

	public double calc(double latt1, double lonn1, double latt2, double lonn2) {
    	double R = EARTH_RADIUS; // earth radius in km
    	double lat1 = Math.toRadians(latt1), lon1 = Math.toRadians(lonn1);  
    	double lat2 = Math.toRadians(latt2), lon2 = Math.toRadians(lonn2);  
    	double dLat = lat2 - lat1;  
    	double dLon = lon2 - lon1;  
    	double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon/2) * Math.sin(dLon/2);  
    	double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));  
    	return R * c;
	}
	public void closeOutputFile() throws IOException {
		output.close();
	}
}
