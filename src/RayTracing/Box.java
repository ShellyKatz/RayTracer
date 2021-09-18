package RayTracing;

public class Box extends Primitive{

	Point position_center;
	double scale;
	Vector minBox, maxBox;
	
	public Box(double x, double y, double z, double scale,int material) {
		this(new Point(x,y,z), scale, material);
	}
	
	public Box(Point p, double scale, int material) {
		super(material);
		this.position_center = p;
		this.scale = scale;
		
		//compute two opposite corners of the box
		minBox = this.position_center.addScalar(-this.scale/2);
		maxBox = this.position_center.addScalar(this.scale/2);
	}

	
	//used the slabs method as recommended
	//https://www.scratchapixel.com/lessons/3d-basic-rendering/minimal-ray-tracer-rendering-simple-shapes/ray-box-intersection
	@Override
	public Intersection intersection(Ray r) {
		Vector start = r.start;
		Vector direction = r.direction;
		
		//compute min and max intersection of ray with x axis
		double txMin = (minBox.getX() - start.getX()) / direction.getX();
		double txMax = (maxBox.getX() - start.getX()) / direction.getX();
		if (txMax < txMin) {
			double tmp = txMax;
			txMax = txMin;
			txMin = tmp;
		}
			
		//compute min and max intersection of ray with y axis
		double tyMin = (minBox.getY() - start.getY()) / direction.getY();
		double tyMax = (maxBox.getY() - start.getY()) / direction.getY();
		if (tyMax < tyMin) {
			double tmp = tyMax;
			tyMax = tyMin;
			tyMin = tmp;
		} 
		
		/*if there is no intersection between x interval and y interval - 
		 *  ray has no intersection with box */
		if (txMin > tyMax || tyMin > txMax) {
			return null;
		}
		
		//find the largest min ("latest entry point") 
		 double tMin = txMin > tyMin ? txMin : tyMin;
		//find the smallest max ("earliest leaving point") 
		 double tMax = txMax < tyMax ? txMax : tyMax;
		 
		//compute min and max intersection of ray with z axis
		double tzMin = (minBox.getZ() - start.getZ()) / direction.getZ();
		double tzMax = (maxBox.getZ() - start.getZ()) / direction.getZ();
		if (tzMax < tzMin) {
			double tmp = tzMax;
			tzMax = tzMin;
			tzMin = tmp;
		}
		
		//ray has no intersection with box (same explanation, no intersection with z interval
		if (tMin > tzMax || tzMin > tMax) {
			return null;
		}
		
		
		//find the largest min ("latest entry point") 
		tMin = tMin > tzMin ? tMin : tzMin;
		//find the smallest max ("earliest leaving point") 
		tMax = tMax < tzMax ? tMax : tzMax; 
		
		//finding which intersection is closest and returning it
		Vector intersection1 = start.addVectors(direction.multScalar(tMin));
		double distance1 = start.distance(intersection1);
		
		Vector intersection2 = start.addVectors(direction.multScalar(tMax));
		double distance2 = start.distance(intersection2);
				
		return distance1 < distance2 ? new Intersection(this, distance1, intersection1) :  new Intersection(this, distance2, intersection2) ;
	}

	
	//https://blog.johnnovak.net/2016/10/22/the-nim-raytracer-project-part-4-calculating-box-normals/
	//should we add bias??
//	@Override
//	public Vector normalWithIntersection(Intersection hit) {
//		Vector c = minBox.addVectors(maxBox).multScalar(0.5f);
//		Vector p = hit.point_of_contact.subtract(c);
//		Vector d = minBox.subtract(maxBox).multScalar(0.5f);
//		
//		double x = p.getX() / Math.abs(d.getX());
//		double y = p.getY() / Math.abs(d.getY());
//		double z = p.getZ() / Math.abs(d.getZ());
//		return new Vector(x,y,z).normalize();
//	}
	
	
	/*Calculation the normal with intersection to box
	 * The normal to the box, is the normal to the face of the box that intersection hit.
	 * Because the box is axis aligned,
	 * the normal to the box is the normal to a plain that is axis aligned also. 
	 * So, we check which of the sides of the box the intersection hit and the normal.
	 **/
	@Override
	public Vector normalWithIntersection(Intersection hit) {
		double epsilon = 0.0001f;
		double halfScale = this.scale/2;
		
		//checking if the plain of intersection is the one that vertical to X
		if (hit.getPoint_of_contact().getX() - halfScale - this.position_center.getX() < epsilon) {
			return new Vector (1,0,0);
		}
		if (hit.getPoint_of_contact().getX() + halfScale - this.position_center.getX() < epsilon) {
			return new Vector (-1,0,0);
		}
		
		//checking if the plain of intersection is the one that vertical to Y
		if (hit.getPoint_of_contact().getY() - halfScale - this.position_center.getY() < epsilon) {
			return new Vector (0,1,0);
		}
		if (hit.getPoint_of_contact().getY() + halfScale - this.position_center.getY() < epsilon ) {
			return new Vector (0,-1,0);
		}
		
		//checking if the plain of intersection is the one that vertical to Z
		if (hit.getPoint_of_contact().getZ() - halfScale - this.position_center.getZ() < epsilon) {
			return new Vector (0,0,1);
		}
		 //(hit.getPoint_of_contact().getZ() + halfScale - this.position_center.getZ() < epsilon) {
		return new Vector (0,0,-1);
		//}
	}
	
	
}

