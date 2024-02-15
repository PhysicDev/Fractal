package example;

import java.awt.Color;

import fractal.Fractal;
import fractal.Utilities;

public class Cantor {

	public static void main(String[] args) {

		Fractal Cantor=new Fractal(1,Utilities::Cantor);
		Cantor.setMaxDepth(9);
		Cantor.setSampling(100);
		
		System.out.println("printing Cantor Set ...\n\n");

		double test=3*3*3*3*3-1;
		for(int j=0;j<6;j++) {
			for(int i=0;i<=test;i++)
				System.out.print((Cantor.checkPix((double)i/test)>=j)?"1":"_");
			System.out.println("");
		}
		
		System.out.println("\ngenerating Picture ...\n\n");
		
		//pre cmpile of the function for the benchmark
		Cantor.drawFractal(3*3*3*3*3*3-1, 250, Color.WHITE, Color.BLACK, 5, new double[] {0d}, new double[] {1d}, new double[] {0d});
		Cantor.drawFractalSampling(3*3*3*3*3*3-1, 250, Color.WHITE, Color.BLACK, 5, new double[] {0d}, new double[] {1d}, new double[] {0d});
		
		long benchmarkStart=System.nanoTime();
		Utilities.saveImage(Cantor.drawFractal(3*3*3*3*3*3-1, 200, Color.WHITE, Color.BLACK, 5, new double[] {0d}, new double[] {1d}, new double[] {0d})
							,"Cantor_well_choosen_size_.png","png");
		long perf=System.nanoTime()-benchmarkStart;
		System.out.println("performance : "+perf/1e6+" ms\n");
		

		benchmarkStart=System.nanoTime();
		Utilities.saveImage(Cantor.drawFractal(1000, 200, Color.WHITE, Color.BLACK, 5, new double[] {0d}, new double[] {1d}, new double[] {0d})
							,"Cantor_unprecise_.png","png");
		perf=System.nanoTime()-benchmarkStart;
		System.out.println("performance : "+perf/1e6+" ms\n");
		
		benchmarkStart=System.nanoTime();
		Utilities.saveImage(Cantor.drawFractalSampling(1000, 200, Color.WHITE, Color.BLACK, 5, new double[] {0d}, new double[] {1d}, new double[] {0d})
							,"Cantor_precise_.png","png");
		perf=System.nanoTime()-benchmarkStart;
		System.out.println("performance : "+perf/1e6+" ms\n");
		
	}

}
