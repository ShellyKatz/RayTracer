package RayTracing;

import java.util.ArrayList;
import java.util.List;

public class Scene {

	Camera camera;
	List<Primitive> primitives; 
	List<Light> lights;
	List<Material> materials;
	Point backgroundColor; 
	Vector leftCorner, right, towards, up;
	int N, maxRecursion, imageHeight, imageWidth;
	double pixelWidth, pixelHeight;
	Vector prevGoDown;
	
	public Scene() {
		primitives = new ArrayList<Primitive>();
		lights = new ArrayList<Light>();
		materials = new ArrayList<Material>();
	}
	
	
	public Camera getCamera() {
		return camera;
	}


	public void setCamera(Camera camera) {
		this.camera = camera;
		right = camera.u;
		towards = camera.w;
		up = camera.v;
		pixelWidth = camera.screenWidth/imageWidth;
		pixelHeight = camera.screenHeight/imageHeight;
		leftCorner = calcLeftCorner(right, towards, up);
	}


	public Point getBackgroundColor() {
		return backgroundColor;
	}


	public void setBackgroundColor(Point color) {
		this.backgroundColor = color;
	}


	public int getN() {
		return N;
	}


	public void setN(int n) {
		N = n;
	}


	public int getMaxRecursion() {
		return maxRecursion;
	}


	public void setMaxRecursion(int maxRecursion) {
		this.maxRecursion = maxRecursion;
	}
	
	public List<Primitive> getPrimitives() {
		return primitives;
	}


	public void setPrimitives(List<Primitive> primitives) {
		this.primitives = primitives;
	}


	public List<Light> getLights() {
		return lights;
	}


	public void setLights(List<Light> lights) {
		this.lights = lights;
	}


	public List<Material> getMaterials() {
		return materials;
	}


	public void setMaterials(List<Material> materials) {
		this.materials = materials;
	}
	
	public int getImageHeight() {
		return imageHeight;
	}


	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}


	public int getImageWidth() {
		return imageWidth;
	}


	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}
	
	public void addMaterial(Material m) {
		this.materials.add(m);
	}
	
	public void addLight(Light l) {
		this.lights.add(l);
	}
	
	public void addPrimitive(Primitive p) {
		this.primitives.add(p);
	}
	

	public Ray ConstructRayThroughPixel(int i, int j) {
		Vector P;
		if (this.camera.fisheye == true) {
			int[] newPoint = fisheyeDistortion(i, j, this.camera.k);
			if (newPoint == null) {
				return null;
			}
			i = newPoint[0];
			j = newPoint[1];
			P = calcPixelLocationFishEye(i,j);
		}
		
		else 	
			P = calcPixelLocation(i, j);
		
//		Vector rayDirection = P.subtract(camera.position).normalize();
		Vector rayDirection = P.normalize();
		return new Ray(camera.position, rayDirection);
	}
	
	/* this function moves from upper left pixel location to the relevant (i,j) pixel location 
	 * pixel's width (in the camera's world) is screenWidth/imageWidth. Respectively for pixel's height 
	 * calcPixelLocation = left_corner + right*j*pixelWidth - up*i*pixelHeight */
	private Vector calcPixelLocation(int i, int j) {	
		double rightSize = j*pixelWidth;
		
		//we only need to calculate go down once in every n iteration (n the number of columns)
		Vector goDown;
		if (j == 0) {
			double downSize = i*pixelHeight;
			goDown = leftCorner.addVectors(up.multScalar(-downSize));
			prevGoDown = goDown;
			return goDown; //j==0 so rightSize==0 -> goRight = (0,0,0)
		}
		
		goDown = prevGoDown; 
		Vector goRight = goDown.addVectors(right.multScalar(rightSize));
		
		return goRight;

	}
	
	/*In fisheye we are going through the pixels in a different order
	 * so we won't use the prevGoDown*/
	private Vector calcPixelLocationFishEye(int i, int j) {
		double rightSize = j*pixelWidth;
		double downSize = i*pixelHeight;
		
		Vector goRight = leftCorner.addVectors(right.multScalar(rightSize));
		Vector goDown = goRight.addVectors(up.multScalar(-downSize));
		return goDown;		
	}


	/* calculate the upper left corner of the screen: p0 + towards*d - right*0.5*screenWidth + up*0.5*screenHeight
	 - from camera position (p0) go distance to the direction of towards vector(to the middle of the screen)
	 - go 0.5Width to the left direction (-right)
	 - go 0.5Height to the up direction */
	private Vector calcLeftCorner(Vector right, Vector towards, Vector up) {
//		Vector midScreen = camera.position.addVectors(towards.multScalar(camera.distance));
		Vector midScreen = towards.multScalar(camera.distance);
		Vector midLeft = midScreen.subtract(right.multScalar((double)0.5*camera.screenWidth));
		return midLeft.addVectors(up.multScalar((double)0.5*camera.screenHeight));
	}
	
	/*Finding the ray's intersection with the closest primitive */
	public Intersection findIntersection(Ray ray, Primitive ignore) {
		double minDistance = Double.MAX_VALUE;
		Intersection minInter = null;
		boolean firstTime = true;
		for(Primitive p : this.getPrimitives()) {
			
			if (p == ignore) continue; //if p is the primitive we want to ignore we continue to next iteration
			
			//calculating intersection of p with ray
			Intersection hit = p.intersection(ray);
			
			if (hit==null) continue; //if there's no intersection - continue
			
			//finding the min distance intersection
			if (firstTime || hit.distance < minDistance) {
				minDistance = hit.distance;
				minInter = hit;
				firstTime = false;
			}
		}
		return minInter;
	}
	
	public Intersection findIntersection(Ray ray) {
		 return findIntersection(ray, null); 
	}
	
	public Vector getColor(Intersection hit, Ray ray, int depth) {
		//got to max depth
		if (depth == 0) {
			return new Vector(0, 0, 0);
		}
		
		//no intersection - returning background color
		if (hit == null) {
			return this.backgroundColor;
		}
		
		//getting Material and Primitive from hit
		Primitive p = hit.getP();
		int mat_index = p.getMaterial();
		Material m = this.materials.get(mat_index);
		
		//computing normal to the primitive at intersection point
		Vector N = p.normalWithIntersection(hit); // N
		
		//checking if the normal is towards the shape or outwards-
		Vector minusV = ray.direction.normalize(); //The direction to the viewing point 
		Vector V = minusV.multScalar(-1);
		double VN = N.dotProduct(V);
		if(VN < 0.0)
			N = N.multScalar(-1);
		
		//compute diffuse and specular color
		Vector diffuseAndSpecular = getDiffuseAndSpecular(hit, m, N, V);

		/*compute reflection: R = V - 2(V*N)N
		 * Then, we compute the reflection ray intersection (R) 
		 * Lastly, we calculate the reflection color recursively */
		Vector totalReflection = new Vector(0,0,0);
		if (m.reflection.getX() != 0 || m.reflection.getY() != 0 || m.reflection.getZ() != 0) {
			double minusVN = minusV.dotProduct(N);
			Vector R = minusV.subtract(N.multScalar(2*minusVN)).normalize();
			Ray reflectionRay = new Ray(hit.point_of_contact, R);
			Intersection reflectionIntersection = this.findIntersection(reflectionRay, p);
			Vector reflectionColor = getColor(reflectionIntersection, reflectionRay, depth-1);
			totalReflection = reflectionColor.multVector(m.reflection);
		}
		
		
		/*compute transparency*/
		Vector background = new Vector(0, 0, 0);
		if (m.transparency > 0) { //if the material is somewhat transparent	
			//We compute the transparency ray intersection and the transparency color recursively 
			Ray transparencyRay = new Ray(hit.point_of_contact, ray.direction);
			Intersection interTransparency = this.findIntersection(transparencyRay, p);
			background = getColor(interTransparency, transparencyRay, depth-1);
			background = background.multScalar(m.transparency);
		}

		//output color = (background color) * transparency + (diffuse + specular) * (1 - transparency)+ (reflection color)
		//Vector background = bgColor.multScalar(m.transparency);
		Vector mid = diffuseAndSpecular.multScalar(1-m.transparency).addVectors(totalReflection);
		Vector outputColor = background.addVectors(mid);
		
		return outputColor;
		
	}


	private Vector getDiffuseAndSpecular(Intersection hit, Material m, Vector N, Vector V) {	
		Vector diffuse = new Vector(0,0,0);
		Vector specular = new Vector(0,0,0);
		for (Light light : this.lights) {//going over the list of lights
					
			Vector L = light.position.subtract(hit.point_of_contact).normalize(); //L is the vector in the direction towards the light
			double NL = N.dotProduct(L);
			
			if (NL < 0) {//The light is behind the shape at intersection point
				continue;
			}
			
			double shadow = light.computeLightShadows(hit, this.N);
			
			//summing for each light:NL*(light's color)*shadow 
			diffuse = diffuse.addVectors(light.color.multScalar(NL*shadow)); 

			
			Vector R = N.multScalar(2*NL).subtract(L).normalize(); //R = (2NL)N - L
			double RV = R.dotProduct(V);
			
			if (RV < 0) {//The direction is behind the shape
				continue;
			}
			
			//summing for each light: (light's color)*(RV)^n*shadow
			specular = specular.addVectors(light.color.multScalar((double)Math.pow(RV, m.phong)*light.specular*shadow));
		}
		
		Vector Kd = m.diffuse; 
		diffuse = diffuse.multVector(Kd); //Kd*(NL)
				
		Vector Ks = m.specular; 
		specular = specular.multVector(Ks); //Ks*(RV)^n
		
		return diffuse.addVectors(specular);
	}
	
	
	public int[] fisheyeDistortion(int x, int y, double k) {
		int[] newPoint = new int[2];
		double f = this.camera.distance/this.pixelWidth;
		
		double centerX = imageWidth/(double)2;
		double centerY = imageHeight/(double)2;
	
		/* compute Rf (distance from center to (x,y)*/
		double distX = (double)Math.pow((x - centerX), 2);
		double distY = (double)Math.pow((y - centerY), 2);
		double Rf = (double)Math.sqrt(distX + distY);
		
		/* compute theta
		 * computations are according to ratios given in the assignment  */
		double theta; 
		if (k > 0 && k <= 1) {
			theta = (double)Math.atan((Rf*k/f))/k; 
		}
		else if (k == 0) {
			theta = Rf/f;
		}
		else if (k >= -1 && k < 0) {
			theta = (double)Math.asin((Rf*k)/f)/k;
		}
		else {
			return null; 
		}
		
		if (theta < 0) {
			theta = theta + 2*Math.PI;
		}
		
		/* theta is bigger/equal to 90 degrees therefore out of camera veiw field*/
		if (theta >= Math.PI/2) {
			return null;
		}
		
		/* compute Rp (distance between point (x,y) without fisheye to center)
		 * tan(theta) = Rp/f -> Rp = tan(theta)/f */
		double tanTheta = (double)Math.tan(theta);
	
		double Rp = f*tanTheta;
		
		/* normal = Rf */
		double directionX = (x - centerX)/Rf;
		double directionY = (y - centerY)/Rf;
		
		/* to get to the new point we go from center point, Rp magnitude, in the relevant direction */	
		int newX = (int)Math.round(centerX + Rp*directionX);
		int newY = (int)Math.round(centerY + Rp*directionY);
		
		newPoint[0] = newX;
		newPoint[1] = newY;
		
		
		return newPoint;
	}
	
	
}
