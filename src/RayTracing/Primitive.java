package RayTracing;


public abstract class Primitive {
	int material_index;
	
	public Primitive(int material) {
		this.material_index = material;
	}
	
	public int getMaterial() {
		return material_index;
	}

	public void setMaterial(int material_index) {
		this.material_index = material_index;
	}
	
	public abstract Intersection intersection(Ray r);
	
	public abstract Vector normalWithIntersection(Intersection hit);


}