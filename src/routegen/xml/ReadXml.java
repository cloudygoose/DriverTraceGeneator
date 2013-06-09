package routegen.xml;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
public class ReadXml {   
	
	private Document doc = null;   
	 
	public void init(String xmlFile) throws Exception {   
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();   
		DocumentBuilder db = dbf.newDocumentBuilder();   
		doc = db.parse(new File(xmlFile));   
	}   
	
	public void viewXML(String xmlFile) throws Exception {   
		this.init(xmlFile);   
		Element element = doc.getDocumentElement();   
		System.out.println(":" + element.getTagName());   
 
		NodeList nodeList = doc.getElementsByTagName("node");   
		System.out.println("node:" + nodeList.getLength());   
		int nodenum = nodeList.getLength();
		
		for (int i = 0;i < 5;i++) {
			Node node = nodeList.item(i);
			NamedNodeMap attributes = node.getAttributes();
		
			for (int j = 1;j < 4;j++) {
				Node attribute = attributes.item(j);
				System.out.println(attribute.getNodeName() + " : " + 
						attribute.getNodeValue());
			}
		}

		NodeList wayList = doc.getElementsByTagName("way");
		System.out.println("way:" + wayList.getLength());
		int waynum = wayList.getLength();
	}   
}   