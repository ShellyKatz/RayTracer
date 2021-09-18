package RayTracing;

public class Camera {
	Vector position, lookAtPosition, up;
	double distance, screenWidth, screenHeight, k;
	boolean fisheye;
	Vector u,v,w;
	Matrix M;
	
	public Camera(Vector position, Vector lookAtPosition, Vector up, double dist, double width, double height, boolean fisheye, double k) {
		this.position = position;
		this.lookAtPosition = lookAtPosition;
		this.up = up;
		this.distance = dist;
		this.screenWidth = width;
		this.fisheye = fisheye;
		this.screenHeight = height;
		this.k = k;
		
		this.createCameraCoordinate();
	}
	
	public Camera(double x, double y, double z, double lx, double ly, double lz, double	ux, double uy, double	uz, double dist, double width, double height, boolean fisheye, double k) {
		this(new Vector(x,y,z),  new Vector(lx,ly,lz), new Vector(ux,uy,uz), dist, width, height, fisheye, k);
	}

	
	public void createCameraCoordinate() {
	//	Vector N = position.subtract(lookAtPosition);
		Vector N = lookAtPosition.subtract(position);

		w = N.normalize();
		u = up.crossProduct3(N).normalize();
		v = w.crossProduct3(u);
		
//		System.out.println("w: "+ w.getX()+ " " + w.getY()+ " " + w.getZ());
//		System.out.println("u: "+ u.getX()+ " " + u.getY()+ " " + u.getZ());
//		System.out.println("v: "+ v.getX()+ " " + v.getY()+ " " + v.getZ());

		
		/*creating rows for transformation matrix M*/
		Vector newW = w.copy();
		newW.add(0);
		Vector newU = u.copy();
		newU.add(0);
		Vector newV = v.copy();
		newV.add(0);
		Vector last = new Vector(0,0,0,1);
		
		Matrix m1 = new Matrix(newU, newV, newW, last);
		
		Vector x = new Vector(1,0,0,-position.getX());
		Vector y = new Vector(0,1,0,-position.getY());
		Vector z = new Vector(0,0,1,-position.getZ());
		Vector last2 = new Vector(0,0,0,1);
		
		Matrix m2 = new Matrix(x, y, z, last2);
		
		M = m1.multiply(m2);	
	}
}
