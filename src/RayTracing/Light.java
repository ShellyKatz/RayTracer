package RayTracing;

import java.util.List;
import java.util.Random;

public class Light {
	
	Point position, color; 
	double specular, shadow, radius;
	
	public Light(Point position, Point color, double specular, double shadow, double radius) {
		this.position = position;
		this.color = color;
		this.specular = specular;
		this.shadow = shadow;
		this.radius = radius;
	}
	
	public Light(double x, double y, double z, double r, double g, double b, double specular, double shadow, double radius) {
		this(new Point(x,y,z), new Point(r,g,b),specular,shadow,radius);
	}
	
	/*This function divides the light to n^2 subrays.
	 * We define a rectangle on that plane, centered at the light source and as wide as the defined light radius.
	 * Then, we divide the rectangle into a grid of N*N cells, where N is the number of shadow 
	 * rays from the scene parameters.
	 * For each cell we shoot a ray from a selected random point to the light. */	
	public double computeLightShadows(Intersection hit, int N) {
		double shadow = 0;
		
		/*create viewing coordinates*/
		Vector normal = hit.point_of_contact.subtract(this.position).normalize();
		Vector up = new Vector(0, 1, 0);
		Vector right = up.crossProduct3(normal).normalize();
		Vector top = normal.crossProduct3(right);
				
		double cellWidth = this.radius/(double)N;
		
		Random rnd = new Random();
		
		//calculating the position of the left corner of the rectangle 
		Vector leftCornerOfRec = this.position.addVectors(top.multScalar(0.5f*radius));
		leftCornerOfRec = leftCornerOfRec.addVectors(right.multScalar(-0.5f*radius));
		
		//int count = 0;
		double oppacity = 0;	
		double goVerticalBase =  0;
		
		/*create all subrays*/
		for (int row=0; row < N; row++) {
			/*compute the subray position from a random point in each cell
			 * we are getting to the cell position from top left corner*/
			
			goVerticalBase += row==0 ? 0.5f*cellWidth : cellWidth;
			
			
			double goHorizontalBase = 0;
			
			for (int col=0; col < N; col++) {
				
				goHorizontalBase += col==0 ? 0.5f*cellWidth : cellWidth;
				
				double goVertical = goVerticalBase;
				double goHorizontal = goHorizontalBase;
				
				//taking a random point from the cell
			    goVertical = goVertical + (rnd.nextDouble() -0.5f)*cellWidth;
				goHorizontal = goHorizontal + (rnd.nextDouble() - 0.5f)*cellWidth;
				
				Vector lightPosition = leftCornerOfRec.addVectors(top.multScalar(-goVertical));
				lightPosition = lightPosition.addVectors(right.multScalar(goHorizontal));
				
				//creating the subray from the random position in the cell to the hit point
				Vector rayDirection = hit.point_of_contact.subtract(lightPosition).normalize();
				Ray subray = new Ray(lightPosition, rayDirection); //subray from the light to intersection point 
				
				//calculating a list of primitives that intersect with the subray before the main primitive (hit.p)
				List<Primitive> intersectingPrimitives = hit.findPrimitivesBeforePrimitive(subray);
				
				//the ray missed the primitive so it's not effected by the light's shadow
				if (intersectingPrimitives == null)
					continue;
				
				/*Calculation that doesn't take into account transparency
				 * counting the rays that hit the main primitive directly */
//				if(intersectingPrimitives.size() == 0 ) {
//					count++;
//				}
				
				
				//Bonus- taking into account each primitive's (on the way) transparency
				oppacity = 0.0f;
				for(Primitive p: intersectingPrimitives) {
					oppacity += 1-RayTracer.scene.getMaterials().get(p.material_index).transparency;
				}

				
				shadow += Math.pow(1-this.shadow, oppacity);
			}
			 
		}
				
		/*light_intesity = (1 - shadow_intensity) * 1 + shadow_intensity * (%of rays
		*that hit the points from the light source) 
		*(a ray hit the surface if it didn't meet any other primitive on the way)*/
//		double precent_rays = (double)count/(double)(N*N);
//		double light_intensity = (1-this.shadow) + this.shadow*precent_rays;
		
		
		double withTransparency = (double)shadow/(double)(N*N);
		
		return withTransparency;
	}	

}


