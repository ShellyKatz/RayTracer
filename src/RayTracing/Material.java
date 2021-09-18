package RayTracing;

public class Material {
	Point diffuse, specular, reflection;
	double phong, transparency;
	
	public Material(double diffuse_r, double diffuse_g, double diffuse_b, double specular_r, 
						double specular_g, double specular_b, double reflection_r, 
						double reflection_g, double reflection_b, double phong, double transparency) {
		this(new Point(diffuse_r, diffuse_g, diffuse_b), 
				new Point(specular_r,specular_g,specular_b),
				new Point(reflection_r,reflection_g,reflection_b), phong, transparency);
	}
	
	public Material(Point diffuse, Point specular, Point reflection, double phong, double transparency) {
		this.diffuse = diffuse;
		this.specular = specular;
		this.reflection = reflection;
		this.phong = phong;
		this.transparency = transparency;
		
	}
	
}
