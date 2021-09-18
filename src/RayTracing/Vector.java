package RayTracing;

import java.util.LinkedList;
import java.util.List;

public class Vector{
	List<Double> v;
	
	public Vector() {
		v = new LinkedList<Double>();
	}
	
	public Vector(double x, double y, double z){
		v = new LinkedList<Double>();
		this.v.add(x);
		this.v.add(y);
		this.v.add(z);
	}
	
	public Vector(List<Double> v) {
		this.v = v;
	}
	
	public Vector(double x, double y, double z, double w){
		this(x,y,z);
		this.v.add(w);
	}
	
	public double get(int index) {
		return this.v.get(index);
	}
	
	public int size() {
		return this.v.size();
	}
	
	public double getX() {
		return v.get(0);
	}
	
	public double getY() {
		return v.get(1);
	}
	
	public double getZ() {
		return v.get(2);
	}
	
	public Vector normalize() {
		double sum = 0;
		for (double cor: v) {
			sum += cor*cor;
		}
		double normal = (double)Math.sqrt(sum);
		if (normal == 0) {
			return this;
		}
		Vector normalV= new Vector();
		for (double cor: v) {
			normalV.add(cor/normal);
		}
		
		return normalV;
	}
	
	public Vector subtract(Vector other) {
		Vector substractV = new Vector();
		for (int i =0; i < this.size(); i++) {
			substractV.add(this.get(i) - other.get(i));
		}
		
		return substractV;
	}
	
	public double dotProduct(Vector other) {
		double dp = 0;
		for (int i =0; i < this.size(); i++) {
			dp += this.get(i) * other.get(i);
		}
		
		return dp;
	}
	
	public Vector crossProduct3(Vector other) {
		double newX = getY()*other.getZ()-getZ()*other.getY();
		double newY = getZ()*other.getX() - getX()*other.getZ();
		double newZ = getX()*other.getY() - getY()*other.getX();
		return new Vector(newX, newY, newZ);
	}
	
	public Vector copy() {
		Vector res = new Vector();
		for (double cor: v) {
			res.add(cor);
		}		
		return res;
	}
	
	public void add(double item) {
		this.v.add(item);
	}
	
	public Vector multScalar(double scalar) {
		Vector res = new Vector();
		for (int i =0; i < this.size(); i++) {
			res.add(this.get(i)*scalar);
		}
		return res;
	}
	
	public Vector addScalar(double scalar) {
		Vector res = new Vector();
		for (int i =0; i < this.size(); i++) {
			res.add(this.get(i)+scalar);
		}
		return res;
	}
	
	public Vector addVectors(Vector other) {
		Vector res = new Vector();
		for (int i =0; i < this.size(); i++) {
			res.add(this.get(i) + other.get(i));
		}
		return res;
	}
	
	public Vector multVector(Vector other) {
		Vector res = new Vector();
		for (int i =0; i < this.size(); i++) {
			res.add(this.get(i)*other.get(i));
		}
		return res;
	}
	
	public double distance(Vector other) {
		double sum = 0;
		for (int i =0; i < this.v.size(); i++) {
			double diff = this.get(i) - other.get(i);
			sum += diff*diff;
		}
		
		return (double)Math.sqrt(sum);
		
	}

}
