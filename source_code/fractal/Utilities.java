package fractal;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * this class contain multiple usefull method
 * 
 * as well as some fractal example
 * 
 */
public class Utilities {
	
	
	public static boolean DEBUG=true;
	
	/**
	 * this method is used to save an image to a file
	 * @param image the Buffered image to save
	 * @param outputPath the path of the picture
	 * @param format the format of the picture
	 */
	public static void saveImage(BufferedImage image, String outputPath, String format) {
        try {
            File outputFile = new File(outputPath);
            ImageIO.write(image, format, outputFile);
            if(DEBUG)
            	System.out.println("Image saved successfully at: " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
            if(DEBUG)
            	System.err.println("Error saving image to: " + outputPath);
        }
    }
	
	/**
	 * initializer of the cantor set fractal
	 * use this method in the Fractal class
	 * 
	 * @param coord the point 
	 * @param MaxDepth the maximum depth to check
	 * @return the maximum depth where the point is in the fractal (or MaxDepth if the point is lower)
	 */
	public static int Cantor(double[] coord,int MaxDepth) {
		return CantorSet(coord,MaxDepth,1d/3d,2d/3d);
	}
	
	
	/**
	 * This is a simple recursive method that create the CantorSet
	 * i've used moving border instead of changing the coord array value.
	 * 
	 * @param coord the point
	 * @param MaxDepth the maximum depth to check
	 * @param start the lower bound (set at 1/3 of the current block the point is in)
	 * @param end the upper bound (set at 2/3 of the current block the point is in)
	 * @return the maximum depth where the point is in the fractal (or MaxDepth if the point is lower)
	 */
	public static int CantorSet(double[] coord,int MaxDepth,double start,double end) {
		//stopping condition
		if(MaxDepth==1)
			return 1;
		//lower block
		if(coord[0]<=start)
			return CantorSet(coord,MaxDepth-1,start+(start-end)*2d/3d,start+(start-end)*1d/3d)+1;
		//higher block
		if(coord[0]>=end)
			return CantorSet(coord,MaxDepth-1,end-(start-end)*1d/3d,end-(start-end)*2d/3d)+1;
		//not in the fractal
		return 0;
	}
	
	
	/**
	 * initializer of the Sierpinsky carpet fractal
	 * use this method in the Fractal class
	 * 
	 * @param coord the point 
	 * @param MaxDepth the maximum depth to check
	 * @return the maximum depth where the point is in the fractal (or MaxDepth if the point is lower)
	 */
	public static int Menger(double[] coord,int MaxDepth) {
		return Menger(coord,MaxDepth,1d/3d,2d/3d,1d/3d,2d/3d,1.0);
	}
	
	
	/**
	 * This is a simple recursive method that create a Menger fractal (also known as Sierpinsky carpet)
	 * 
	 * this method is not very optimize and i think i can make it better.
	 * 
	 * @param coord the point
	 * @param MaxDepth the maximum depth to check
	 * @param startX the lower bound X
	 * @param endX the upper bound X
	 * @param startY the lower bound Y
	 * @param endY the upper bound Y
	 * @param distance the size of the current block (divided by 3 because it's more convenient for the computing)
	 * @return the maximum depth where the point is in the fractal (or MaxDepth if the point is lower)
	 */
	public static int Menger(double[] coord,int MaxDepth,double startX,double endX,double startY,double endY,double distance) {
		//middle square condition (out of the fractal)
		if(coord[0]>startX && coord[0]<endX && coord[1]>startY && coord[1]<endY)
			return 0;
		//stopping condition
		if(MaxDepth==1)
			return 1;
		distance=(endX-startX)/3d;
		//computing new bound
		if(coord[0]<startX) {
			startX=startX-2*distance;
			endX=startX+distance;
		}
		else {
			if(coord[0]>endX) {
				startX=endX+distance;
				endX=startX+distance;
			}
			else {
				startX+=distance;
				endX=startX+distance;
			}
		}
		distance=(endY-startY)/3d;
		if(coord[1]<startY) {
			startY-=2*distance;
			endY=startY+distance;
		}
		else {
			if(coord[1]>endY) {
				startY=endY+distance;
				endY=startY+distance;
			}
			else {
				startY+=distance;
				endY=startY+distance;
			}
		}
		return Menger(coord,MaxDepth-1,startX,endX,startY,endY,distance)+1 ;
	}
	/**
	 * the ratio used by the method Sierpinsky
	 */
	public static double ratio=2d;
	
	/**
	 * initializer of the Sierpinsky triangle fractal
	 * use this method in the Fractal class
	 * 
	 * @param coord the point 
	 * @param MaxDepth the maximum depth to check
	 * @return the maximum depth where the point is in the fractal (or MaxDepth if the point is lower)
	 */
	public static int Sierpinski(double[] coord,int MaxDepth) {
		//initial check to see if we are in the main triangle.
		if(ratio*(1-Math.abs(coord[0]))<coord[1])
			return 0;
		return Sierpinski(coord,0d,1d,1d,MaxDepth);
	}
	
	/**
	 * 
	 * This is a simple recursive method that create a Sierpinsky triangle fractal
	 * 
	 * note that this fractal use a constant (ratio) this is used to determine
	 * how tall is the triangle relative to its base, i use 2 to make the triangle have the same size that it's base.
	 * 
	 * @param coord the point
	 * @param xLim the xlim (corresponding to the middle of the current triangle)
	 * @param yLim the xlim (corresponding to the middle of the current triangle)
	 * @param distance the size of the current triangle
	 * @param MaxDepth the maximum depth to check
	 * @return the maximum depth where the point is in the fractal (or MaxDepth if the point is lower)
	 */
	public static int Sierpinski(double[] coord,double xLim,double yLim,double distance,int MaxDepth) {
		distance/=2d;
		
		//upper triangle condition
		if(coord[1]>yLim) {
			if(MaxDepth==1)
				return 1;
			return Sierpinski(coord,xLim,yLim+ratio*distance/2d,distance,MaxDepth-1)+1;
		}
		
		//middle triangle (so not in the fractal)
		if(Math.abs(coord[0]-xLim)*ratio<coord[1]-yLim+distance*ratio)
			return 0;
		
		//stopping condition
		if(MaxDepth==1)
			return 1;
		//left or right triangle condition
		if(coord[0]>xLim)
			xLim+=distance;
		else
			xLim-=distance;
		return Sierpinski(coord,xLim,yLim-ratio*distance/2d,distance,MaxDepth-1)+1;
	}
	
}
