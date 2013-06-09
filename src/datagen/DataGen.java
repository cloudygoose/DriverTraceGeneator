package datagen;
import routegen.test.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
public class DataGen {
	public static Random random = new Random(567);
	public ArrayList<String> goodPlates;
	public ArrayList<String> badPlates;
	public ArrayList<String> allPlates;
	public ArrayList<APass> normalPasses;
	public ArrayList<APass> randomPasses;
	public void readNormalPasses() throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(new File("./DoublePassFilterOutput.txt")));
		int passN;
		String str = reader.readLine();
		passN = Integer.parseInt(str);
		normalPasses = new ArrayList<APass>();

		//TO BE changed
		//passN = 10002;
		
		double sumSumK = 0;
		for (int i = 0;i < passN;i++) {
			APass aPass = new APass();
			int n = Integer.parseInt(reader.readLine());
			double sumK = Double.parseDouble(reader.readLine());
			sumSumK += sumK;
			for (int j = 0;j < n;j++) {
				str = reader.readLine();
				String[] strs = str.split(" ");
				String id = strs[0];
				double lon = Double.parseDouble(strs[1]);
				double lat = Double.parseDouble(strs[2]);
				Boolean isCam;
				if (strs[3].equals("1"))
					isCam = true;
				else 
					isCam = false;
				double km = Double.parseDouble(strs[4]);
				Point point = new Point(id, lon, lat, isCam, km);
				aPass.add(point);
			}
			normalPasses.add(aPass);
			if (i % 10000 == 0)
				System.out.println("readNormalPasses : " + i);
		}
		System.out.println("average km : " + sumSumK / passN);
		System.out.println("readNormalPasses Complete");
		reader.close();
	}
	
	public void readRandomPasses() throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(new File("./NoFilterOutput.txt")));
		int passN;
		String str = reader.readLine();
		passN = Integer.parseInt(str);
		randomPasses = new ArrayList<APass>();

		//TO BE changed
		//passN = 10002;
		
		double sumSumK = 0;
		for (int i = 0;i < passN;i++) {
			APass aPass = new APass();
			int n = Integer.parseInt(reader.readLine());
			double sumK = Double.parseDouble(reader.readLine());
			sumSumK += sumK;
			for (int j = 0;j < n;j++) {
				str = reader.readLine();
				//System.out.println(str);
				String[] strs = str.split(" ");
				String id = strs[0];
				double lon = Double.parseDouble(strs[1]);
				double lat = Double.parseDouble(strs[2]);
				Boolean isCam;
				if (strs[3].equals("1"))
					isCam = true;
				else 
					isCam = false;
				double km = Double.parseDouble(strs[4]);
				Point point = new Point(id, lon, lat, isCam, km);
				aPass.add(point);
			}
			randomPasses.add(aPass);
			if (i % 10000 == 0)
				System.out.println("readRandomPasses : " + i);
		}
		System.out.println("average km : " + sumSumK / passN);
		System.out.println("readRandomPasses Complete");
		reader.close();
	}
	public void generateDay(String plate, MyTime begin, double speed, APass pass, BufferedWriter writer) throws IOException {
		for (int i = 0;i < pass.size();i++) {
			if (pass.get(i).isCam && random.nextInt(2) == 0) {
				//writer.write("|PLATE " + plate + "|TIME " + begin.getPrint() + "|PASS " + pass.get(i).getPrint() + "|");
				writer.write(plate + " " + begin.getPrint() + " " + pass.get(i).nodeId);
				writer.newLine();
			}
			begin.forward(pass.get(i).getKm() / speed);
		}
		begin.forward(7 * 60 * 60, 2 * 60 * 60);
		pass.reverse();
		for (int i = 0;i < pass.size();i++) {
			if (pass.get(i).isCam && random.nextInt(2) == 0) {
				//writer.write("|PLATE " + plate + "|TIME " + begin.getPrint() + "|PASS " + pass.get(i).getPrint() + "|");
				writer.write(plate + " " + begin.getPrint() + " " + pass.get(i).nodeId);
				writer.newLine();
			}
			begin.forward(pass.get(i).getKm() / (speed));
		}
	}
	public void readPlates() throws IOException {
		goodPlates = new ArrayList<String>();
		normalPasses = new ArrayList<APass>();
		
		BufferedReader reader = new BufferedReader(new FileReader(new File("./registration.txt")));
		reader.readLine();
		for (int i = 0;i < 10000;i++) {
			String str = reader.readLine();
			String[] ss = str.split("\t");
			goodPlates.add(ss[1]);
		}
		reader.close();
		
		badPlates = new ArrayList<String>();
		HashSet<String> bad = new HashSet<String>();
		int having = 0;
		while (having < 100) {
			String str = goodPlates.get(random.nextInt(10000));
			if (!bad.contains(str)) {
				having++;
				bad.add(str);
				badPlates.add(str);
			}
		}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("./badPlates.txt")));
		for (int i = 0;i < badPlates.size();i++) {
			writer.write(badPlates.get(i));
			writer.newLine();
		}
		writer.close();
		allPlates = new ArrayList<String>();
		allPlates.addAll(goodPlates);
		allPlates.addAll(badPlates);
	}
	public void generateIllegalPass() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("./IllegalOutput.txt")));
		System.out.println("badPlates number : " + badPlates.size());
		int nn = badPlates.size() * 7; //Change : give less data on wrong plates
		for (int i = 0;i < nn;i++) {
			String plate = badPlates.get(random.nextInt(badPlates.size()));
			APass Npass = normalPasses.get(random.nextInt(normalPasses.size()));
			for (int day = 1;day <= 30;day++) {
				if (random.nextInt(5) == 0)
					continue;
				MyTime begin;
				if (day % 7 == 0 || day % 7 == 6)
					begin = new MyTime(day, 12 * 60 * 60, 3 * 60 * 60);
				else
					begin = new MyTime(day, 8 * 60 * 60, 3 * 60 * 60);
				int j = random.nextInt(2);
				if (j == 0)
					generateDay(plate, begin, new Double(50) / new Double(3600), Npass, writer);
				else
					generateDay(plate, begin, new Double(50) / new Double(3600), 
							randomPasses.get(random.nextInt(randomPasses.size())), writer);
			}
		}
		System.out.println("generate Illegal Pass complete");
		writer.close();	
	}
	public void generateMoreRandomNoise() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("./NoiseOutput.txt")));
		int nn = 5000; //Change : give less data on wrong plates
		System.out.println("noise size : " + nn);
		for (int i = 0;i < nn;) {
			String plate = allPlates.get(random.nextInt(allPlates.size()));
			int day = 1 + random.nextInt(29);
			MyTime begin = new MyTime(day, 5*60*60, 0);
			begin.forward(random.nextInt(15 * 60 * 60));
			APass pass = normalPasses.get(random.nextInt(normalPasses.size()));
			int passI = random.nextInt(pass.size());
			if (pass.get(passI).isCam()) {
				writer.write(plate + " " + begin.getPrint() + " " + pass.get(passI).nodeId);
				writer.newLine();
				i++;
			}
		}
		System.out.println("generate Noise Pass complete");
		writer.close();	
	}
	public void generateLegalPass() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File("./LegalOutput.txt")));
		int nn = goodPlates.size();
		for (int i = 0;i < nn;i++) {
			String plate = goodPlates.get(i);
			APass pass = normalPasses.get(random.nextInt(normalPasses.size()));

			for (int day = 1;day <= 30;day++) {
				if (random.nextInt(8) == 0)
					continue;
				MyTime begin;
				if (day % 7 == 0 || day % 7 == 6)
					begin = new MyTime(day, 12 * 60 * 60, 3 * 60 * 60);
				else
					begin = new MyTime(day, 8 * 60 * 60, 3 * 60 * 60);
				if (random.nextInt(5) == 0)
					generateDay(plate, begin, new Double(30) / new Double(3600), 
							normalPasses.get(random.nextInt(normalPasses.size())),
							writer);
				else
				generateDay(plate, begin, new Double(30) / new Double(3600), pass, writer);
			}
		}
		System.out.println("generate Legal Pass complete");
		writer.close();	
	}
	public static void main(String[] args) throws Exception {
		Router router = new Router();
		router.doooo1();
		router.doooo2();
		
		DataGen dataGen = new DataGen();
		dataGen.readPlates();
		dataGen.readNormalPasses();
		dataGen.readRandomPasses();
		
		dataGen.generateLegalPass();
		dataGen.generateIllegalPass();
		dataGen.generateMoreRandomNoise();
		
	}
}
class APass {
	ArrayList<Point> route;
	public APass() {
		route = new ArrayList<Point>();
	}
	public Point get(int i) {
		return route.get(i);
	}
	public void add(Point p) {
		route.add(p);
	}
	public String getPrint() {
		String res = "";
		for (int i = 0;i < route.size();i++) {
			res += route.get(i).getPrint() + "\n";
		}
		return res;
	}
	public void reverse() {
		ArrayList<Point> arr = new ArrayList<Point>();
		for (int i = 0;i < route.size();++i) {
			arr.add(route.get(route.size() - i - 1));
			if (i < route.size() - 1)
				arr.get(i).setKm(route.get(route.size() - i - 2).getKm());
			else
				arr.get(i).setKm(0);
		}
		route = arr;
	}
	public int size() {
		return route.size();
	}
}
class Point {
	String nodeId;
	double lon, lat, km;
	boolean isCam;
	public Point(String id, double lo, double la, boolean isC, double k) {
		nodeId = id;
		lon = lo;
		lat = la;
		isCam = isC;
		km = k;
	}
	public String getNodeId () {
		return nodeId;
	}
	public double getLon() {
		return lon;
	}
	public double getLat() {
		return lat;
	}
	public double getKm() {
		return km;
	}
	public boolean isCam() {
		return isCam;
	}
	public String getPrint() {
		return nodeId + " " + lon + " " + lat + " " + isCam + " " + km;
	}
	public void setKm(double d) {
		km = d;
	}
}