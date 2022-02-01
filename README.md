# RayTracer
This project implements a ray tracer

How To Start?
1. Clone the repository
2. Create a scene text file (read "Scene Definition Format" file and see examples in the "scenes" folder)
3. Parameters needed to run the code are: input file (.txt), output file (.png), width and height. Width and Height have default value of 500.
4. Run the code in 1 of 2 ways:
  - Run the RayTracer.java file from the IDE with the needed input (as described above).
  - Run the RayTracer.jar file from the cmd like this:  
    $ java -jar RayTracer.jar `<input file name>`.txt `<output file name>`.png `<width>` `<height>`  
    for example:  
    $ java -jar RayTracer.jar scenes\Spheres.txt scenes\Spheres.png 500 500
