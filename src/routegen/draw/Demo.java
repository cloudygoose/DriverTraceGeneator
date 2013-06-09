package routegen.draw;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;

import routegen.test.*;

public class Demo {
    public static void ttest() {
        BufferedImage ImageNew = new BufferedImage(500, 500,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = (Graphics2D) ImageNew.getGraphics();
        g2.setColor(Color.white);
        g2.fillRect(0, 0, 500, 500);

        g2.setStroke(new BasicStroke(4.0f));
        g2.setColor(Color.blue);
        g2.drawLine(100, 100, 150, 100);
		g2.setColor(Color.RED);
		g2.setStroke(new BasicStroke(10f));
		g2.drawOval(50, 50, 12, 13);
        File outFile = new File("./newfile.png");
        try {
            ImageIO.write(ImageNew, "png", outFile);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }// дͼƬ
    }
    public static void drawLines(ArrayList<Point> points, String fileName) {
        int width = 2000;
        int height = 2000;
    	BufferedImage ImageNew = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);//
        Graphics2D g2 = (Graphics2D) ImageNew.getGraphics();
        g2.setColor(Color.white);//
        g2.fillRect(0, 0, width, height);

        g2.setStroke(new BasicStroke(4.0f));//
        g2.setColor(Color.blue);//
        double sx = width / 2, sy = height / 2;
        for (int i = 1;i < points.size();i++) {
        	double x1 = points.get(i).lat - points.get(i-1).lat;
        	double y1 = points.get(i).lon - points.get(i-1).lon;
        	while (x1 * x1 + y1 * y1 < 200) {
        		x1 = x1 * 5;
        		y1 = y1 * 5;
        	}
        	g2.drawString(i+"!", (int)Math.round(sx), (int)Math.round(sy));
        	g2.drawLine((int)Math.round(sx), (int)Math.round(sy),
        			(int)Math.round(sx + x1), (int)Math.round(sy + y1));
        	sx = sx + x1;
        	sy = sy + y1;
        }
        File outFile = new File("./" + fileName);
        try {
            ImageIO.write(ImageNew, "png", outFile);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }// дͼƬ
    }
}