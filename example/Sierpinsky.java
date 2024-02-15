package example;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import fractal.Fractal;
import fractal.Utilities;

public class Sierpinsky {

	

	public static final int THREAD=6;
	
	public static final boolean OVERWRITE=false;
	
	public static void main(String[] args) {
		

	    String folderPath = "./sierpinskyAnim";
	    if(!(Files.exists(Paths.get(folderPath)) && Files.isDirectory(Paths.get(folderPath)))) {
			try {
				Files.createDirectories(Paths.get(folderPath));
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
		
		Fractal sierpinsky =new Fractal(2,Utilities::Sierpinski);
		sierpinsky.setSampling(80);
		sierpinsky.setMaxDepth(10);

		Utilities.saveImage(sierpinsky.drawFractalSampling(1000, 1000,Color.WHITE, Color.BLACK,2.0, 8,new double[] {-1.0,0.0}
            										,new double[]{2.0,0},new double[]{0,2.0}),
            										"sierpinsky_precise_"+".png","png");

		ExecutorService executor = Executors.newFixedThreadPool(THREAD);
		int frames=1000;
		for(int i=0;i<frames-1;i+=1) {
			final int taskId = i;
			if (!Files.exists(Paths.get(folderPath+"/output"+String.format("%03d", taskId)+".png")) || OVERWRITE)
	            executor.submit(() -> {
	            	System.out.println("Task " + taskId + " is running on thread " + Thread.currentThread().getName());
	            	double zoomLvl=Math.exp(-taskId/721.35)*2;
	            	double bright=(1.25-(double)(taskId%500)/500d*1.0/4.0)*1.5d;
	            	Utilities.saveImage(sierpinsky.drawFractalSampling(1000, 1000,Color.WHITE, Color.BLACK,bright, 8+((taskId>=500)?1:0),new double[] {-1+taskId/4000d,taskId/2000d}
	            										,new double[]{zoomLvl,0},new double[]{0,zoomLvl}),
	            										folderPath+"/output"+String.format("%03d", taskId)+".png","png");
	            });
		}
		executor.shutdown();

        try {
            executor.awaitTermination((long) 1e10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}

}
