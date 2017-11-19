package map.obstacle;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import espece.Espece;
import espece.capteur.Capteur;

public abstract class Obstacle implements ObstacleInterface{
	
	int x;
	int y;
	
	public Obstacle(int x, int y, int[] infos) {
		this.x = x;
		this.y = y;
	}
	
	public Point2D.Double[] getPoints(Espece e) {
		
		/*
		 *   0     1
		 * 
		 *   2     3
		 */
		
		
		double angle = e.tetha;
		double cos = Math.cos(angle);
		double sin = Math.sin(angle);
		
		Point2D.Double[] points = new Point2D.Double[4];
		
		double tx = -e.w/2;
		double ty = -e.h/2;
		points[0] = new Point2D.Double(e.x + tx * cos - ty * sin,  e.y + tx * sin + ty * cos);
		
		tx = e.w/2;
		ty = -e.h/2;
		points[1] = new Point2D.Double(e.x + tx * cos - ty * sin,  e.y + tx * sin + ty * cos);
		
		tx = -e.w/2;
		ty = e.h/2;
		points[2] = new Point2D.Double(e.x + tx * cos - ty * sin,  e.y + tx * sin + ty * cos);
		
		tx = e.w/2;
		ty = e.h/2;
		points[3] = new Point2D.Double(e.x + tx * cos - ty * sin,  e.y + tx * sin + ty * cos);
		
		return points;
	}
	
	public double getCapteurValueForRect(Capteur c, int w,int h) {
		
		double xRel = c.getXRealtive();
		double yRel = c.getYRealtive();
		
		Line2D.Double line = new Line2D.Double(c.getX1(), c.getY1(), c.getX1()+xRel, c.getY1()+yRel);
		
		double v = 1;
		
		double sx = (x-line.getX1())/xRel;
		double y3 = line.getY1() + (sx*yRel);
		if(y3 <=y+h && y3 >= y && sx <= 1 && sx >=0) {
			v = addValue(v, sx);
		}
		
		double sxw = (x+w-line.getX1())/xRel;
		double y4 = line.getY1() + (sxw*yRel);
		if(y4 <=y+h && y4 >= y && sxw <= 1 && sxw >=0) {
			v = addValue(v, sxw);
		}
		
		double sy = (y-line.getY1())/yRel;
		double x3 = line.getX1() + (sy*xRel);
		if(x3 <=x+w && x3 >= x && sy <= 1 && sy >=0) {
			//System.out.println(sy);
			v = addValue(v, sy);
		}
		
		double syh = (y+h-line.getY1())/yRel;
		double x4 = line.getX1() + (syh*xRel);
		if(x4 <=x+w && x4 >= x && syh <= 1 && syh >=0) {
			v = addValue(v, syh);
		}
		
		return v;
	}
	
	private double addValue(double value, double value2) {
		if(value > value2 ) {
			return value2;
		}
		return value;
	}

	
	
	
	

}
