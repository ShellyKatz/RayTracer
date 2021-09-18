package RayTracing;

import RayTracing.Primitive;

public class Sphere extends Primitive{
	
	double radius;
	Point center;
	
	public Sphere(double x, double y, double z, double radius , int material) {
		super(material);
		center = new Point(x,y,z);
		this.radius = radius;
		
	}
	
	public double getRadius() {
		return radius;
	}


	public void setRadius(double radius) {
		this.radius = radius;
	}


	public Point getCenter() {
		return this.center;
	}

	public void setCenter(Point c) {
		this.center = c;
	}

	@Override
	public Intersection intersection(Ray ray) {
		//solve the equation at^2 +bt + c = 0:
		double a = 1;
		Vector subt = ray.start.subtract(this.center);
		double b = ray.direction.dotProduct(subt)*2;
		double c = subt.dotProduct(subt) - (double)Math.pow(radius, 2);
//		System.out.println("a "+a+ " b "+b +" c "+ c);
		//double c = (double)Math.pow(ray.start.distance(this.center),2) - (double)Math.pow(radius, 2);
		double discr = (double)Math.pow(b, 2) - 4*a*c;
		if (discr < 0) {
			return null;
		}
		double t1 = (- b + (double)Math.sqrt(discr))/(2*a);
		double t2 = (- b - (double)Math.sqrt(discr))/(2*a);
		
//		System.out.println("t1 "+t1+ " t2 "+t1 +" disc "+ discr);

		
		if (t1 < 0 && t2 < 0) {
			return null;
		}
		
		if (t1 < 0) {
			Vector intersection2 = ray.start.addVectors(ray.direction.multScalar(t2));
			double distance2 = ray.start.distance(intersection2);
			return new Intersection(this, distance2, intersection2);
		}
		
		if (t2 < 0) {
			Vector intersection1 = ray.start.addVectors(ray.direction.multScalar(t1));
			double distance1 = ray.start.distance(intersection1);
			return new Intersection(this, distance1, intersection1);
		}
		
		Vector intersection1 = ray.start.addVectors(ray.direction.multScalar(t1));
		Vector intersection2 = ray.start.addVectors(ray.direction.multScalar(t2));

		double distance1 = ray.start.distance(intersection1);
		double distance2 = ray.start.distance(intersection2);
		
//		System.out.println("distance1 "+distance1+ " distance2 "+distance2);

		
		
		return distance1<distance2 ? new Intersection(this, distance1, intersection1) : new Intersection(this, distance2, intersection2);		
	}

	@Override
	public Vector normalWithIntersection(Intersection hit) {
		return hit.point_of_contact.subtract(this.center).normalize();
	}
	
	
}
