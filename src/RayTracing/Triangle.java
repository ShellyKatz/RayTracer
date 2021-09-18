package RayTracing;

import RayTracing.Primitive;

public class Triangle extends Primitive{
	
	Point p0,p1,p2;
	

	public Triangle(Point p0, Point p1, Point p2, int material) {
		super(material);
		this.p0 = p0;
		this.p1 = p1;
		this.p2 = p2;
	}

	public Triangle(double x0, double y0, double z0, double x1, double y1, double z1, double x2, double y2, double z2, int material) {
		this(new Point(x0,y0,z0), new Point(x1,y1,z1),new Point(x2,y2,z2),material);
	}
	




	public Point getP0() {
		return p0;
	}

	public void setP0(Point p0) {
		this.p0 = p0;
	}

	public Point getP1() {
		return p1;
	}

	public void setP1(Point p1) {
		this.p1 = p1;
	}

	public Point getP2() {
		return p2;
	}

	public void setP2(Point p2) {
		this.p2 = p2;
	}

	@Override
	public Intersection intersection(Ray r) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector normalWithIntersection(Intersection hit) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
