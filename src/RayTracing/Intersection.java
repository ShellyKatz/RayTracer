package RayTracing;

import java.util.LinkedList;
import java.util.List;

public class Intersection {
	
	Primitive p;
	double distance;
	Vector point_of_contact;

	public Intersection(Primitive p, double distance, Vector point_of_contact) {
		this.p = p;
		this.distance = distance;
		this.point_of_contact = point_of_contact;
	}
	
	public Primitive getP() {
		return p;
	}

	public void setP(Primitive p) {
		this.p = p;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	public Vector getPoint_of_contact() {
		return point_of_contact;
	}

	public void setPoint_of_contact(Vector point_of_contact) {
		this.point_of_contact = point_of_contact;
	}
	
	public List<Primitive> findPrimitivesBeforePrimitive(Ray ray) {
		List<Primitive> intersectingPrimitives = new LinkedList<Primitive>();
		
		/*find intersection between ray and primitive*/
		Intersection intersection = this.p.intersection(ray);
		if (intersection == null)
			return null;
		 double maxDistance = intersection.distance;
		
		/*finding all primitives that intersect with ray before "main" primitive (this.p)*/
		for (Primitive p : RayTracer.scene.getPrimitives()) { 
			if (p == this.p) continue;
			Intersection inter = p.intersection(ray);
			if (inter != null && inter.distance < maxDistance) {
				intersectingPrimitives.add(p);
			}
		}
		return intersectingPrimitives;
	}

}
