package RayTracing;

import RayTracing.Primitive;

public class Plane extends Primitive{
	
	Vector normal;
	double offset;

	public Plane(double x, double y, double z, double offset, int material) {
		super(material);
		this.normal = new Vector(x,y,z);
		this.offset = offset;
	
	}

	public Vector getNormal() {
		return normal;
	}




	public void setNormal(Vector normal) {
		this.normal = normal;
	}

	
	
	public double getOffset() {
		return offset;
	}

	public void setOffset(double offset) {
		this.offset = offset;
	}

	@Override
	public Intersection intersection(Ray r) {
		double denominator = (this.normal.dotProduct(r.direction));
		if (denominator == 0) {
			return null;
		}
		
		/* a point P on plane satisfies normal*P=offset -> normal*P - offset = 0 */
		double t = -(r.start.dotProduct(this.normal)-this.offset)/denominator;
		
		if (t < 0) {
			return null;
		}
		Vector P = r.start.addVectors(r.direction.multScalar(t));
		double distance = r.start.distance(P);
		return new Intersection(this, distance, P);
	}

	@Override
	public Vector normalWithIntersection(Intersection hit) {
		return this.normal.normalize();
	}
		
}

