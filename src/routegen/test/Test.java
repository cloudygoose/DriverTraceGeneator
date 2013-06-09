package routegen.test;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
 
public class Test {   
	HashMap<String, Point> points;
	HashSet<String> cameras;
	HashMap<String, LinkedList<String>> links;
	BufferedWriter output;
	private Document doc = null;   
 
	public void init() throws Exception {   
		DocumentBuilderFactory factory = DocumentBuilderFactory
		     .newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		doc = builder.parse(new File("./map.xml"));
		points = new HashMap<String, Point>();
		cameras = new HashSet<String>(); 
		links = new HashMap<String, LinkedList<String>>();
	}   
 
	public void viewXMLtoNodeOutputFile(String xmlFile) throws Exception {   
		File f = new File("./readingnodeoutput.txt");

	    output = new BufferedWriter(new FileWriter(f));

		Element element = doc.getDocumentElement();   
		
		NodeList nodeList = doc.getElementsByTagName("node");   
		System.out.println("node Length:" + nodeList.getLength());   
		output.write(new Integer(nodeList.getLength()).toString());
		output.newLine();
		for (int i = 0;i < nodeList.getLength();i++) {
			Node fatherNode = nodeList.item(i);    
			NamedNodeMap attributes = fatherNode.getAttributes();   
			String id = attributes.item(1).getNodeValue();
			output.write(id + " ");
			output.write(attributes.item(2).getNodeValue() + " ");
			output.write(attributes.item(3).getNodeValue());
			output.newLine();
			double lat = Double.parseDouble(attributes.item(2).getNodeValue());
			double lon = Double.parseDouble(attributes.item(3).getNodeValue());
			if (i % 1000 == 0)
				System.out.println("Reading node : " +  i);
		}
		output.close();
		System.out.println("Node output complete.");
	}   
	public void viewXMLtoWayOutputFile(String xmlFile) throws Exception {   
		File f = new File("./readingwayoutput.txt");

	    output = new BufferedWriter(new FileWriter(f));

		Element element = doc.getDocumentElement();   
		
		NodeList wayList = doc.getElementsByTagName("way");   
		System.out.println("way Number:" + wayList.getLength());   
		
		int highWayNum = 0, waterWay = 0, railWay = 0, landUse = 0, power = 0, route = 0;
		int wayNodeNum = 0, wayCameraNum = 0;
		for (int i = 0;i < wayList.getLength();i++) {
			Node fatherNode = wayList.item(i);    
			NodeList childList = fatherNode.getChildNodes();
			//System.out.println("l:" + childList.getLength());
			//barrier
			//railway
			//waterway
			boolean isHighWay = false;
			for (int j = 0;j < childList.getLength();j++) {
				Node node = childList.item(j);
				if (node.getNodeName().equals("tag")) {
					NamedNodeMap attributes = node.getAttributes();
					for (int k = 0;k <= 1;k++) {
						if (attributes.item(k).getNodeValue().equals("highway")) 
							isHighWay = true;
						if (attributes.item(k).getNodeValue().equals("waterway")) 
							waterWay++;
						if (attributes.item(k).getNodeValue().equals("railway")) 
							railWay++;
						if (attributes.item(k).getNodeValue().equals("landuse"))
							landUse++;
						if (attributes.item(k).getNodeValue().equals("power"))
							power++;
					}
				}
			}
			if (!isHighWay) continue;
			String last = null;
			for (int j = 0;j < childList.getLength();j++) {	
				Node node = childList.item(j);
				if (node.getNodeName().equals("nd")) {
					NamedNodeMap attributes = node.getAttributes();
					if (attributes.item(0).getNodeName().equals("ref")) {
						String now = attributes.item(0).getNodeValue();
						wayNodeNum++;
						if (cameras.contains(now)) 
							wayCameraNum++;
						if (last == null) {
							last = now;
							continue;
						}
						addPairToLinks(last, now);
						addPairToLinks(now, last);
						last = now;
					}
				}
			}
			if (isHighWay) 
				highWayNum++;
		}
		System.out.println("	highway : " + highWayNum);
		System.out.println("	waterway : " + waterWay);
		System.out.println("	railway : " + railWay);
		System.out.println("	landuse : " + landUse);
		System.out.println("	power : " + power);
		System.out.println("wayNodeNum : " + wayNodeNum);
		System.out.println("wayCameraNum : " + wayCameraNum);
		Iterator<String> iter = links.keySet().iterator();
		int a[] = new int[10];
		for (int i = 0;i < 10;i++) a[i] = 0;
		while (iter.hasNext()) {
			a[links.get(iter.next()).size()]++;
		}
		for (int i = 0;i < 8;i++)
			System.out.println("node degree " + i + " : " + a[i]);
		output.close();
		System.out.println("Node output complete.");
	}   
	public void addPairToLinks(String aa, String bb) {
		LinkedList<String> kk = links.get(aa);
		if (kk == null) { 
			kk = new LinkedList<String>();
			links.put(aa, kk);
		}
		kk.add(bb);
	}
	public void readCameraFile() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File("./camera-locations.txt")));
	    System.out.println("Begin to read cameras"); 
		String ln = reader.readLine();
	     int cameraNum = Integer.parseInt(ln);
	     int NoSum = 0;
	     for (int i = 1;i <= cameraNum;i++) {
	    	 ln = reader.readLine();
	    	 String[] str = ln.split(",");
	    	 cameras.add(str[0]);
	    	 if (points.get(str[0]) == null) {
	    	 	 NoSum++;
	    	 }
	     }
	     System.out.println("Tested : all " + cameraNum + " camera contianed in osm");
	     reader.close();
	}
	public void shit() throws IOException {
		if (output != null)
			output.close();
	}
	public void readNodeFile() throws IOException {
	     BufferedReader reader = new BufferedReader(new FileReader(new File("./readingnodeoutput.txt")));
	     String ln = reader.readLine();
	     int pointNum = Integer.parseInt(ln);
	     for (int i = 1;i <= pointNum;i++) {
	    	 ln = reader.readLine();
	    	 String[] str = ln.split(" ");
	    	 double p1 = Double.parseDouble(str[1]);
	    	 double p2 = Double.parseDouble(str[2]);
	    	 String p0 = str[0];
	    	 points.put(p0, new Point(p1, p2));
	    	 if (i % 10000 == 0)
	    		 System.out.println("reading outputFile " + i);
	     }
	     reader.close();
	}
}   