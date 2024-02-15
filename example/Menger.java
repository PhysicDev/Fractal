package example;

import java.awt.Color;

import fractal.Fractal;
import fractal.Utilities;

public class Menger {

	public static void main(String[] args) {

		Fractal Menger=new Fractal(2,Utilities::Menger);
		Menger.setMaxDepth(9);
		Menger.setSampling(100);
		
		System.out.println("printing Menger Fractal ...\n\n");
		double test=3*3*3*3-1;
		for(int j=0;j<=test;j++) {
			for(int i=0;i<=test;i++)
				System.out.print((Menger.checkPix((double)i/test,(double)j/test)>=4)?"O":" ");
			System.out.println("");
		}
		

		System.out.println("\ngenerating Picture ...\n\n");
		
		//pre compile of the function for the benchmark
		Menger.drawFractal(3*3*3*3*3*3-1, 3*3*3*3*3*3-1, Color.WHITE, Color.BLACK, 5);
		Menger.drawFractalSampling(3*3*3*3*3*3-1, 3*3*3*3*3*3-1, Color.WHITE, Color.BLACK, 5);
		
		long benchmarkStart=System.nanoTime();
		Utilities.saveImage(Menger.drawFractal(3*3*3*3*3*3-1, 3*3*3*3*3*3-1, Color.WHITE, Color.BLACK, 5)
							,"Menger_well_choosen_size_.png","png");
		long perf=System.nanoTime()-benchmarkStart;
		System.out.println("performance : "+perf/1e6+" ms\n");
		
		benchmarkStart=System.nanoTime();
		Utilities.saveImage(Menger.drawFractal(1000, 1000, Color.WHITE, Color.BLACK, 5)
							,"Menger_unprecise_.png","png");
		perf=System.nanoTime()-benchmarkStart;
		System.out.println("performance : "+perf/1e6+" ms\n");
		
		benchmarkStart=System.nanoTime();
		Utilities.saveImage(Menger.drawFractalSampling(1000, 1000, Color.WHITE, Color.BLACK, 5)
							,"Menger_precise_.png","png");
		perf=System.nanoTime()-benchmarkStart;
		System.out.println("performance : "+perf/1e6+" ms\n");
	}

}
