
import java.util.ArrayList;
import java.util.List;

/**
 * A point quadtree: stores an element at a 2D position, 
 * with children at the subdivided quadrants
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, explicit rectangle
 * @author CBK, Fall 2016, generic with Point2D interface
 * 
 */
public class PointQuadtree<E extends Point2D> {
	private E point;							// the point anchoring this node
	private int x1, y1;							// upper-left corner of the region
	private int x2, y2;							// bottom-right corner of the region
	private PointQuadtree<E> c1, c2, c3, c4;	// children
	//public Geometry geometric = new Geometry();
	/**
	 * Initializes a leaf quadtree, holding the point in the rectangle
	 */
	public PointQuadtree(E point, int x1, int y1, int x2, int y2) {
		this.point = point;
		this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
	}

	// Getters
	
	public E getPoint() {
		return point;
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	/**
	 * Returns the child (if any) at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public PointQuadtree<E> getChild(int quadrant) {
		if (quadrant==1) return c1;
		if (quadrant==2) return c2;
		if (quadrant==3) return c3;
		if (quadrant==4) return c4;
		return null;
	}

	/**
	 * Returns whether or not there is a child at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public boolean hasChild(int quadrant) {
		return (quadrant==1 && c1!=null) || (quadrant==2 && c2!=null) || (quadrant==3 && c3!=null) || (quadrant==4 && c4!=null);
	}

	/**
	 * Inserts the point into the tree
	 */
	public void insert(E p2) {
		if (p2.getY() >= this.point.getY()) { // for quadrants 3, 4 
			if (p2.getX() > this.point.getX()) { // this is quadrant 4
				if (this.c4 == null) {
					this.c4 = new PointQuadtree<E>(p2,(int) this.point.getX(),(int) this.point.getY(), x2, y2);
					//System.out.println("Made a Pointquadtree in QUAD 4");
				}else {
					c4.insert(p2);
				}
			}if(p2.getX() < this.point.getX()) {// this is quadrant 3
				if (this.c3 == null) {
					this.c3 = new PointQuadtree<E>(p2, x1,(int) this.point.getY(), (int) this.point.getX(), y2);
					//System.out.println("Made a Pointquadtree in QUAD 3");
				}else {
					c3.insert(p2);
			}
		}
		}
		if (p2.getY() < this.point.getY()) { // for quadrants 1, 2
			if (p2.getX() < this.point.getX()) { // this is quadrant 2
				if (this.c2 == null) {
					this.c2 = new PointQuadtree<E>(p2, x1, y1, (int) this.point.getX(), (int) this.point.getY());
					//System.out.println("Made a Pointquadtree in QUAD 2");
				}else {
					c2.insert(p2);
				}
			}if(p2.getX() >= this.point.getX()) { // this is quadrant 1
				if (this.c1 == null) {
					this.c1 = new PointQuadtree<E>(p2, (int) this.point.getX(), y1, x2, (int) this.point.getY());
					//System.out.println("Made a Pointquadtree in QUAD 1");
				}else {
					c1.insert(p2);
			}
		}
		
	}
	}
	
	/**
	 * Finds the number of points in the quadtree (including its descendants)
	 */
	public int size() {
		int size = 1;
//		if (this.c1 == null && this.c2 == null && this.c3 == null && this.c4 == null) {
//			size = 1;
//			return size;
//		}
		
		if (this.c1 != null) {
			size = size + c1.size();
		}
		if (this.c2 != null) {
			size = size + c2.size();	
		}
		if (this.c3 != null) {
			size = size + c3.size();
		}
		if (this.c4 != null) {
			size = size + c4.size();
		}
		
		return size; 
		
	}
	
	/**
	 * Builds a list of all the points in the quadtree (including its descendants)
	 */
	public List<E> allPoints() {
		ArrayList<E> allPoints = new ArrayList<E>();
		allPoints.add(this.point);
//		if (this.c1 == null && this.c2 == null && this.c3 == null && this.c4 == null) {
//			allPoints.add(this.point);
//			return allPoints;
//		}
		
		if (this.c1 != null) {
			for (E eachPoint: c1.allPoints()) {
				allPoints.add(eachPoint);
		}}
		if (this.c2 != null) {
			for (E eachPoint: c2.allPoints()) {
				allPoints.add(eachPoint);	
		}}
		if (this.c3 != null) {
			for (E eachPoint: c3.allPoints()) {
				allPoints.add(eachPoint);
		}}
		if (this.c4 != null) {
			for (E eachPoint: c4.allPoints()) {
				allPoints.add(eachPoint);
		}}
		
		return allPoints; 
		
	}

	/**
	 * Uses the quadtree to find all points within the circle
	 * @param cx	circle center x
	 * @param cy  	circle center y
	 * @param cr  	circle radius
	 * @return    	the points in the circle (and the qt's rectangle)
	 */
	public List<E> findInCircle(double cx, double cy, double cr) {
		ArrayList<E> pointsInCircle = new ArrayList<E>();
		 //System.out.println("checking to see if rectangle" + this.x1+ " " + this.y1+ " " + this.x2+ " " + this.y2+ " intersects circle @ " + cx + " " + cy);
		if (Geometry.circleIntersectsRectangle(cx, cy, cr, this.x1, this.y1, this.x2, this.y2)) {
			//System.out.println("Okay, I'm gonna test a point " + this.point.getX() +" "+ this.point.getY()+ " on the circle");

			if (Geometry.pointInCircle(this.point.getX(), this.point.getY(), cx, cy, cr)) {
				pointsInCircle.add(this.point);
				//System.out.println ("I found a point in the circle, it's "+ this.point);
			}
				if (this.hasChild(1)) {
					for (E eachPoint: this.c1.findInCircle(cx, cy, cr)){
						pointsInCircle.add(eachPoint);
					}
				}
				if (this.hasChild(2)) {
					for (E eachPoint: this.c2.findInCircle(cx, cy, cr)){
						pointsInCircle.add(eachPoint);
					}
				}
				if (this.hasChild(3)) {
					for (E eachPoint: this.c3.findInCircle(cx, cy, cr)){
						pointsInCircle.add(eachPoint);
					}
				}
				if (this.hasChild(4)) {
					for (E eachPoint: this.c4.findInCircle(cx, cy, cr)){
						pointsInCircle.add(eachPoint);
					}
				}
			
	}
		//System.out.println("Welp, at least we got here");
		//System.out.println(pointsInCircle);
		return pointsInCircle;
	}

	// TODO: YOUR CODE HERE for any helper methods
}
