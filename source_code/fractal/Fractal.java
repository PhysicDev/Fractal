package fractal;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Random;
import java.util.function.BiFunction;

/**
 * 
 * this is a class to create fractal and render it
 * but you can use it to draw other thing actualy.
 * 
 * @author Physic Dev
 * 
 * 
 * @version 1.0
 * 
 */
public class Fractal {
	//dimension of fractal coordinate (must be higher than the actual fractal)
	private int dimension;
	//maximum depth the fractal will compute
	private int maxDepth=100;
	//function that compute the maximum depth at which the given coordinate is in the fractal.
	private BiFunction<double[],Integer,Integer> observer;
	//used for the sampling drawing method.
	private int sampling=10;
	
	/**
	 * 
	 * @param dim the dimension of the fractal
	 * @param check a function that compute the maximum depth at which the given coordinate is in the fractal. 
	 */
	public Fractal(int dim,BiFunction<double[],Integer,Integer> check) {
		dimension=dim;
		observer=check;
	}
	
	/**
	 * dimension getter
	 * @return the dimension
	 */
	public int getDimension() {
		return dimension;
	}
	
	/**
	 * give the maximum depth of computing
	 * @return the maximum depth
	 */
	public int getMaxDepth() {
		return maxDepth;
	}
	
	/**
	 * get the sampling value
	 * @return the sampling value
	 */
	public int getSampling() {
		return sampling;
	}

	/**
	 * change the maximum depth of computing
	 * @param maxDpt the maximum depth
	 */
	public void setMaxDepth(int maxDpt) {
		if(maxDpt<0)
			throw new IllegalArgumentException("the maximum recursion depth must be positive");
		maxDepth=maxDpt;
	}
	
	/**
	 * set the sampling value
	 * @param samp the sampling value
	 */
	public void setSampling(int samp) {
		sampling =samp;
	}

	/**
	 * compute the maximum depth in which the coordinate coord is in the fractal
	 * 
	 * not that if the output is equal to maxDepth the coordinate may be deeper
	 * @param coord the coordinate
	 * @return the maximum depth at which the coordinate is in the fractal/
	 */
	public int checkPix(double... coord){
		if(coord.length!=dimension)
			throw new IllegalArgumentException("coordinate dimension ("+coord.length+") doesn't match fractal dimension : "+dimension);
		return(observer.apply(coord, maxDepth));
	}
	
	/**
	 * Compute an image of the fractal
	 * 
	 * to do so it will check take one coordinate for every pixel (the coordinate is located at the top left corner of the pixel)
	 * and will check if the pixel is deep enough in the fractal to be drawn.
	 * 
	 * this method will not be precise for rendering little detail but is faster than the Sampling method.
	 * 
	 * the fractal will be drawn from (0,0) to (1,1)
	 * 
	 * @param width the width of the image
	 * @param height the height of the image
	 * @param c the color of the fractal
	 * @param back the color of the background
	 * @param depth any pixel lower than this depth will be drawn.
	 * @return a BufferedImage of the fractal
	 */
	public BufferedImage drawFractal(int width,int height,Color c,Color back,int depth) {
		double[] Xvec=new double[dimension];
		double[] Yvec=new double[dimension];
		Xvec[0]=1.0;Yvec[1]=1.0;
		return drawFractal(width,height,c,back,depth,new double[dimension],Xvec,Yvec);
	}
	
	/**
	 * 
	 * Compute an image of the fractal
	 * 
	 * to do so it will check take one coordinate for every pixel (the coordinate is located at the top left corner of the pixel)
	 * and will check if the pixel is deep enough in the fractal to be drawn.
	 * 
	 * this method will not be precise for rendering little detail but is faster than the Sampling method.
	 * 
	 * in addition you can specify the part of the space you want to draw.
	 * 
	 * note that you can draw 2D section of a fractal with dimension higher than 2.
	 * 
	 * @param width the width of the image
	 * @param height the height of the image
	 * @param c the color of the fractal
	 * @param back the color of the background
	 * @param depth any pixel lower than this depth will be drawn.
	 * @param origin the origin from which the picture is drawn, the origin is located at the top left of the image
	 * @param Xvec the amount that would be draw along the X axis (horizontal left to right). the top right coordinate will be equal to the origin + Xvec
	 * @param Yvec the amount that would be draw along the Y axis (vertical top to bottom). the bottom left coordinate will be equal to the origin + Yvec
	 * 
	 * @return a BufferedImage of the fractal
	 */
	public BufferedImage drawFractal(int width,int height,Color c,Color back,int depth,double[] origin,double[] Xvec,double[] Yvec){
		double[] coord=origin.clone();
		double[] startCol=origin.clone();
		
		BufferedImage output=new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		WritableRaster raster = output.getRaster();
		int[] Bval=new int[] {back.getRed(),back.getGreen(),back.getBlue(),back.getAlpha()};
		int[] Cval=new int[] {c.getRed(),c.getGreen(),c.getBlue(),c.getAlpha()};
		for(int k=0;k<dimension;k++) {
			Yvec[k]/=height;
			Xvec[k]/=width;
		}
		for(int i=0;i<width;i++) {
			for(int k=0;k<dimension;k++) {
				startCol[k]+=Xvec[k];
				coord[k]=startCol[k];
			}
			for(int j=0;j<height;j++) {
				if(observer.apply(coord, maxDepth).intValue()>depth)
					raster.setPixel(i, j, Cval);
				else
					raster.setPixel(i, j, Bval);
				for(int k=0;k<dimension;k++)
					coord[k]+=Yvec[k];
			}
		}
		
		return output;
	}
	
	
	/**
	 * 
	 * this method is similar to {@link #drawFractal(int, int, Color, Color, int)}
	 * 
	 * except instead of using one coordinate at the top left corner of each pixel to determine if the pixel should be drawn, it will
	 * check multiple random coordinate within the pixel and measure how much the pixel should be lit.
	 * 
	 * the amount of test by pixel is determine by the attribute sampling.
	 * 
	 * @param width the image width
	 * @param height the image height
	 * @param c the color that a pixel would be if every tested coordinates are in the fractal
	 * @param back the color that a pixel would be if none of the tested coordinate are in the fractal. 
	 * @param depth limit used to determine if a pixel is or is not in the fractal
	 * @return a Buffered Image of the fractal
	 */
	public BufferedImage drawFractalSampling(int width,int height,Color c,Color back,int depth) {
		double[] Xvec=new double[dimension];
		double[] Yvec=new double[dimension];
		Xvec[0]=1.0;Yvec[1]=1.0;
		return drawFractalSampling(width,height,c,back,depth,new double[dimension],Xvec,Yvec);
	}
	
	/**
	 * 
	 * this method is similar to {@link #drawFractal(int, int, Color, Color, int, double[], double[], double[])}
	 * 
	 * except instead of using one coordinate at the top left corner of each pixel to determine if the pixel should be drawn, it will
	 * check multiple random coordinate within the pixel and measure how much the pixel should be lit.
	 * 
	 * the amount of test by pixel is determine by the attribute sampling.
	 * 
	 * @param width the image width
	 * @param height the image height
	 * @param c the color that a pixel would be if every tested coordinates are in the fractal
	 * @param back the color that a pixel would be if none of the tested coordinate are in the fractal. 
	 * @param depth limit used to determine if a pixel is or is not in the fractal
	 * @param origin the origin from which the picture is drawn, the origin is located at the top left of the image
	 * @param Xvec the amount that would be draw along the X axis (horizontal left to right). the top right coordinate will be equal to the origin + Xvec
	 * @param Yvec the amount that would be draw along the Y axis (vertical top to bottom). the bottom left coordinate will be equal to the origin + Yvec
	 * 
	 * @return a BufferedImage of the fractal
	 */
	public BufferedImage drawFractalSampling(int width,int height,Color c,Color back,int depth,double[] origin,double[] Xvec,double[] Yvec){
		return drawFractalSampling(width,height,c,back,1.0,depth,origin,Xvec,Yvec);
	}
	
	/**
	 * 
	 * this method is similar to {@link #drawFractalSampling(int, int, Color, Color, int, double[], double[], double[])}
	 * 
	 * but with an additional parameter used to increase the brightness of the fractal.
	 * 
	 * use this method if you want to create multiple frame of the fractal with a zoom level that change.
	 * This way you can correct the increase of brighness cause by the zoom.
	 * 
	 * @param width the image width
	 * @param height the image height
	 * @param c the color that a pixel would be if every tested coordinates are in the fractal
	 * @param back the color that a pixel would be if none of the tested coordinate are in the fractal. 
	 * @param light this value indicate the multiplier of light.
	 * @param depth limit used to determine if a pixel is or is not in the fractal
	 * @param origin the origin from which the picture is drawn, the origin is located at the top left of the image
	 * @param Xvec the amount that would be draw along the X axis (horizontal left to right). the top right coordinate will be equal to the origin + Xvec
	 * @param Yvec the amount that would be draw along the Y axis (vertical top to bottom). the bottom left coordinate will be equal to the origin + Yvec
	 * 
	 * @return a BufferedImage of the fractal
	 */
	public BufferedImage drawFractalSampling(int width,int height,Color c,Color back,double light,int depth,double[] origin,double[] Xvec,double[] Yvec){
		double[] coord=origin.clone();
		double[] startCol=origin.clone();
		double[] obs=new double[dimension];
		
		Random R=new Random();
		
		BufferedImage output=new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		WritableRaster raster = output.getRaster();
		for(int k=0;k<dimension;k++) {
			Yvec[k]/=height;
			Xvec[k]/=width;
		}
		for(int i=0;i<width;i++) {
			for(int k=0;k<dimension;k++) {
				startCol[k]+=Xvec[k];
				coord[k]=startCol[k];
			}
			for(int j=0;j<height;j++) {
				double test=0d;
				for(int k=0;k<sampling;k++) {
					double Xvar=R.nextDouble();
					double Yvar=R.nextDouble();
					for(int l=0;l<dimension;l++)
						obs[l]=coord[l]+Xvar*Xvec[l]+Yvar*Yvec[l];
					test+=(observer.apply(obs, maxDepth).intValue()>depth)?1d:0d;
				}
				test=test/(double)sampling*light;
				if(test>1)
					raster.setPixel(i, j,new int[]{c.getRed(),c.getBlue(),c.getGreen(),c.getAlpha()});
				else
					raster.setPixel(i, j,new int[]{(int) (test*(double)c.getRed()+(1d-test)*(double)back.getRed()),
												   (int) (test*(double)c.getGreen()+(1d-test)*back.getGreen()),
												   (int) (test*c.getBlue()+(1d-test)*back.getBlue()),
												   (int) (test*c.getAlpha()+(1d-test)*back.getAlpha())});
											   //**/
				//raster.setPixel(i, j, new int[] {(int) (test*255d),(int) (test*255d),(int) (test*255d),255});
				for(int k=0;k<dimension;k++)
					coord[k]+=Yvec[k];
			}
		}
		
		return output;
	}
}
