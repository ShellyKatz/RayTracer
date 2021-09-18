package RayTracing;

import java.awt.Transparency;
import java.awt.color.*;
import java.awt.image.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


import javax.imageio.ImageIO;

/**
 *  Main class for ray tracing exercise.
 */
public class RayTracer {

	public int imageWidth;
	public int imageHeight;
	public static Scene scene = new Scene();

	/**
	 * Runs the ray tracer. Takes scene file, output image file and image size as input.
	 */
	public static void main(String[] args) {
		try {

			RayTracer tracer = new RayTracer();

                        // Default values:
			tracer.imageWidth = 500;
			tracer.imageHeight = 500;

			if (args.length < 2)
				throw new RayTracerException("Not enough arguments provided. Please specify an input scene file and an output image file for rendering.");

			String sceneFileName = args[0];
			String outputFileName = args[1];

			if (args.length > 3)
			{
				tracer.imageWidth = Integer.parseInt(args[2]);
				tracer.imageHeight = Integer.parseInt(args[3]);
			}
			

			// Parse scene file:
			tracer.parseScene(sceneFileName);

			// Render scene:
			tracer.renderScene(outputFileName);
			


//		} catch (IOException e) {
//			System.out.println(e.getMessage());
		} catch (RayTracerException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}


	}

	/**
	 * Parses the scene file and creates the scene. Change this function so it generates the required objects.
	 */
	public void parseScene(String sceneFileName) throws IOException, RayTracerException
	{
		FileReader fr = new FileReader(sceneFileName);

		BufferedReader r = new BufferedReader(fr);
		String line = null;
		int lineNum = 0;
		System.out.println("Started parsing scene file " + sceneFileName);

		int max_material = 0;
		boolean setCamera = false, setSettings = false;

		while ((line = r.readLine()) != null)
		{
			line = line.trim();
			++lineNum;

			if (line.isEmpty() || (line.charAt(0) == '#'))
			{  // This line in the scene file is a comment
				continue;
			}
			else
			{
				String code = line.substring(0, 3).toLowerCase();
				// Split according to white space characters:
				String[] params = line.substring(3).trim().toLowerCase().split("\\s+");
				
				if (code.equals("cam"))
				{
					setCamera = true;
					double x = Double.parseDouble(params[0]);
					double y = Double.parseDouble(params[1]);
					double z = Double.parseDouble(params[2]);
					Vector position = new Vector(x,y,z);
					
					x = Double.parseDouble(params[3]);
					y = Double.parseDouble(params[4]);
					z = Double.parseDouble(params[5]);
					Vector lookAtPosition = new Vector(x,y,z);
					
					x = Double.parseDouble(params[6]);
					y = Double.parseDouble(params[7]);
					z = Double.parseDouble(params[8]);
					Vector up = new Vector(x,y,z);
					
					double dist = Double.parseDouble(params[9]);
					double width = Double.parseDouble(params[10]);
					/*compute height by dividing width with aspect ratio (imageWidth/imageHeight)*/
					double height = width / ((double)imageWidth/(double)imageHeight);
					
					boolean fisheye = false;
					double k = 0.5f;
					try {
						fisheye = Boolean.parseBoolean(params[11]);
						k = Double.parseDouble(params[12]);
					}
					
					catch( ArrayIndexOutOfBoundsException e) {}
					
					Camera camera = new Camera(position, lookAtPosition, up, dist, width, height, fisheye, k);
					scene.setImageHeight(imageHeight);
					scene.setImageWidth(imageWidth);
					scene.setCamera(camera);

					System.out.println(String.format("Parsed camera parameters (line %d)", lineNum));
				}
				else if (code.equals("set"))
				{
					setSettings = true;
					double r1 = Double.parseDouble(params[0]);
					double g1 =  Double.parseDouble(params[1]);
					double b1 =  Double.parseDouble(params[2]);
					Point backgroundcolor = new Point(r1,g1,b1);
					
					int N = Integer.parseInt(params[3]);
					int maxRecursion = Integer.parseInt(params[4]);
					
					scene.setBackgroundColor(backgroundcolor);
					scene.setN(N);
					scene.setMaxRecursion(maxRecursion);
	
					
					System.out.println(String.format("Parsed general settings (line %d)", lineNum));
				}
				else if (code.equals("mtl"))
				{	
					double diffuse_r = Double.parseDouble(params[0]);
					double diffuse_g = Double.parseDouble(params[1]);
					double diffuse_b = Double.parseDouble(params[2]);
					Point diffuse = new Point(diffuse_r,diffuse_g,diffuse_b);
					
					double specular_r = Double.parseDouble(params[3]);
					double specular_g = Double.parseDouble(params[4]);
					double specular_b = Double.parseDouble(params[5]);
					Point specular = new Point(specular_r,specular_g,specular_b);
					
					double reflection_r = Double.parseDouble(params[6]);
					double reflection_g = Double.parseDouble(params[7]);
					double reflection_b = Double.parseDouble(params[8]);
					Point reflection = new Point(reflection_r,reflection_g,reflection_b);

					
					double phong = Double.parseDouble(params[9]);
	                double transparency = Double.parseDouble(params[10]);
					
					Material m = new Material(diffuse, specular, reflection, phong, transparency);
					scene.addMaterial(m);
					
					System.out.println(String.format("Parsed material (line %d)", lineNum));
				}
				else if (code.equals("sph"))
				{
                   double x = Double.parseDouble(params[0]);
                   double y = Double.parseDouble(params[1]);
                   double z = Double.parseDouble(params[2]);
                   double radius = Double.parseDouble(params[3]);
                   int material = Integer.parseInt(params[4])-1;
                   Sphere sphere = new Sphere(x,y,z,radius,material);
                   scene.addPrimitive(sphere);
                   
                   if (material>max_material) {
                	   max_material = material;
                   }

					System.out.println(String.format("Parsed sphere (line %d)", lineNum));
				}
				else if (code.equals("pln"))
				{
					double x = Double.parseDouble(params[0]);
					double y = Double.parseDouble(params[1]);
					double z = Double.parseDouble(params[2]);
					double offset = Double.parseDouble(params[3]);
					int material =Integer.parseInt(params[4])-1;
					Plane plane = new Plane(x,y,z,offset,material);
					scene.addPrimitive(plane);
					
					if (material>max_material) {
	                	   max_material = material;
	                   }
					
					System.out.println(String.format("Parsed plane (line %d)", lineNum));
				}
				
				else if(code.equals("trg")){	

					double x = Double.parseDouble(params[0]);
					double y = Double.parseDouble(params[1]);
					double z = Double.parseDouble(params[2]);
					Point p0 = new Point(x,y,z);
					
					x = Double.parseDouble(params[3]);
					y = Double.parseDouble(params[4]);
					z = Double.parseDouble(params[5]);
					Point p1 = new Point(x,y,z);
					
					x = Double.parseDouble(params[6]);
					y = Double.parseDouble(params[7]);
					z = Double.parseDouble(params[8]);
					Point p2 = new Point(x,y,z);
		            
					int material = Integer.parseInt(params[9])-1;
					
					if (material>max_material) {
	                	   max_material = material;
	                   }
					
					Triangle triangle = new Triangle(p0,p1,p2,material);
					scene.addPrimitive(triangle);
				}
				
				else if(code.contentEquals("box")) {
					double x = Double.parseDouble(params[0]);
					double y = Double.parseDouble(params[1]);
					double z = Double.parseDouble(params[2]);
					Point p1 = new Point(x,y,z);
					
					double scale = Double.parseDouble(params[3]);
					int material = Integer.parseInt(params[4])-1;
					
					if (material>max_material) {
	                	   max_material = material;
	                   }

					Box b = new Box(p1,scale,material);
					scene.addPrimitive(b);
				}
				
				else if (code.equals("lgt"))
				{
					double x = Double.parseDouble(params[0]);
					double y = Double.parseDouble(params[1]);
					double z = Double.parseDouble(params[2]);
					Point p = new Point(x,y,z);
					
					double r1 = Double.parseDouble(params[3]);
					double g1 = Double.parseDouble(params[4]);
					double b1 = Double.parseDouble(params[5]);
					Point color = new Point(r1,g1,b1);
					
					double specular = Double.parseDouble(params[6]);
					double shadow = Double.parseDouble(params[7]);
					double radius = Double.parseDouble(params[8]);

					Light light = new Light(p,color,specular,shadow,radius);
					scene.addLight(light);
					
					System.out.println(String.format("Parsed light (line %d)", lineNum));
				}
				else
				{
					System.out.println(String.format("ERROR: Did not recognize object: %s (line %d)", code, lineNum));
				}
			}
		}
		
		r.close();
		
		if(!setCamera || !setSettings) {
			System.out.println("Error- did not set camera/settings");
			System.exit(1);
		}
		if(max_material > scene.materials.size()-1) {
			System.out.println("Error- material index out of range");
			System.exit(1);
		}

		System.out.println("Finished parsing scene file " + sceneFileName);

	}

	/**
	 * Renders the loaded scene and saves it to the specified file location.
	 */
	public void renderScene(String outputFileName)
	{
		long startTime = System.currentTimeMillis();

		// Create a byte array to hold the pixel data:
		byte[] rgbData = new byte[this.imageWidth * this.imageHeight * 3];

		for (int row=0;row<this.imageHeight;row++) {
			for(int col=0;col<this.imageWidth;col++) {
				Ray ray = scene.ConstructRayThroughPixel(row, col);
				int pixel = (col + imageWidth*row)*3;

				if (ray == null) {
					continue;
				}
				Intersection hit = scene.findIntersection(ray);
				Vector color = scene.getColor(hit, ray, scene.maxRecursion).multScalar(255f);

				//fixRangeForColor makes sure the color is between 0-255 and if not fixes it
				rgbData[pixel] = fixRangeForColor(color.get(0));//red
				rgbData[pixel+1] = fixRangeForColor(color.get(1)); //green
				rgbData[pixel+2] = fixRangeForColor(color.get(2)); //blue

				
			}
		}
                // Put your ray tracing code here!
                //
                // Write pixel color values in RGB format to rgbData:
                // Pixel [x, y] red component is in rgbData[(y * this.imageWidth + x) * 3]
                //            green component is in rgbData[(y * this.imageWidth + x) * 3 + 1]
                //             blue component is in rgbData[(y * this.imageWidth + x) * 3 + 2]
                //
                // Each of the red, green and blue components should be a byte, i.e. 0-255


		long endTime = System.currentTimeMillis();
		Long renderTime = endTime - startTime;

                // The time is measured for your own conveniece, rendering speed will not affect your score
                // unless it is exceptionally slow (more than a couple of minutes)
		System.out.println("Finished rendering scene in " + renderTime.toString() + " milliseconds.");

                // This is already implemented, and should work without adding any code.
		saveImage(this.imageWidth, rgbData, outputFileName);

		System.out.println("Saved file " + outputFileName);

	}




	

	//////////////////////// FUNCTIONS TO SAVE IMAGES IN PNG FORMAT //////////////////////////////////////////

	/*
	 * Saves RGB data as an image in png format to the specified location.
	 */
	public static void saveImage(int width, byte[] rgbData, String fileName)
	{
		try {

			BufferedImage image = bytes2RGB(width, rgbData);
			ImageIO.write(image, "png", new File(fileName));

		} catch (IOException e) {
			System.out.println("ERROR SAVING FILE: " + e.getMessage());
		}

	}

	/*
	 * Producing a BufferedImage that can be saved as png from a byte array of RGB values.
	 */
	public static BufferedImage bytes2RGB(int width, byte[] buffer) {
	    int height = buffer.length / width / 3;
	    ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
	    ColorModel cm = new ComponentColorModel(cs, false, false,
	            Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
	    SampleModel sm = cm.createCompatibleSampleModel(width, height);
	    DataBufferByte db = new DataBufferByte(buffer, width * height);
	    WritableRaster raster = Raster.createWritableRaster(sm, db, null);
	    BufferedImage result = new BufferedImage(cm, raster, false, null);

	    return result;
	}

	public static class RayTracerException extends Exception {
		public RayTracerException(String msg) {  super(msg); }
	}
	
	public static byte fixRangeForColor(double color) {
		if (color<0)
			return (byte) 0;
		if (color>255)
			return (byte)255;
		return (byte) color;
	}
	
		
	

}
