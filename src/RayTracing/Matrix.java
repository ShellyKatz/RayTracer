package RayTracing;

import java.util.LinkedList;
import java.util.List;

public class Matrix {
	
	List<Vector> mat;
	
	public Matrix(List<Vector> mat) {
		this.mat = mat;
	}
	
	public Matrix(Vector v1, Vector v2, Vector v3, Vector v4) {
		this.mat = new LinkedList<Vector>();
		mat.add(v1);
		mat.add(v2);
		mat.add(v3);
		mat.add(v4);
	}
	
	
	public Matrix multiply(Matrix other) {
		if (mat.size() == 0) {
			return null;
		}
		List<Vector> result = new LinkedList<Vector>();
		for (int row=0; row<mat.size(); row++) {
			Vector newList = new Vector();
			result.add(newList);
			for(int col=0; col<mat.get(row).size(); col++) {
				double sum = 0;
				for(int i=0;i<mat.get(row).size();i++) {
					sum += mat.get(row).get(i)*other.mat.get(i).get(col);
				}
				newList.add(sum);
			}
		}
		return new Matrix(result);
	}
	
}
