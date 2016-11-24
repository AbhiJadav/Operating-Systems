import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/*
 Filename: NFUWithAging.java
 Version: 1.0
 Objective: Write a program that simulates a paging system using the aging algorithm.
 The number of page frames is a parameter.
 The sequence of page references should be read from a file.
 For a given input file, plot the number of page faults per 1000 memory references as a function of the number of page frames available.
 Author: Abhishek Jadav
 Assumption: Clock interrupt occurs at every memory reference. 
 created: 4 Nov, 2016
 List of API/Library: -
 */

public class NFUWithAging {

	private static int[][] pageFramesAndCounters;
	private static int pageFaultCounter=0;

	public static void main(String[] args) throws IOException {

		// Input file to read sequence of pages
		File file=new File("input.txt");

		FileReader fr=new FileReader(file);
		BufferedReader br = new BufferedReader(fr);

		String line=null;
		String[] seqOfPageReferenes=null;

		while((line=br.readLine())!=null) {
			seqOfPageReferenes = line.trim().split(",");
		}

		br.close();
		Scanner sc=new Scanner(System.in);

		// Get number of Test Cases
		System.out.print("How many Test Cases you want to run? : ");
		int testCases=sc.nextInt();

		double[][] pageFramesPageFault=new double[testCases][4];

		for (int i = 0; i < testCases; i++) {

			// Get no of Page Frames from user
			System.out.print("\nEnter no of Page Frames for Test Case "+(i+1)+" : ");
			int pageFrames=sc.nextInt();
			pageFramesAndCounters=new int[pageFrames][2];

			// Page Table which have "page frames" along with the "counters"
			for (int x = 0; x < pageFramesAndCounters.length; x++) {
				pageFramesAndCounters[x][0]=-1;
				pageFramesAndCounters[x][1]=0;
			}

			NFUWithAging pagingAlgoWithAging=new NFUWithAging();

			// for each memory reference check for page fault
			for (int j = 0; j < seqOfPageReferenes.length; j++) {
				pagingAlgoWithAging.checkAndAssignPage(Integer.parseInt(seqOfPageReferenes[j]));
			}

			pageFramesPageFault[i][0]=pageFrames;			
			pageFramesPageFault[i][1]=pageFaultCounter;
			pageFramesPageFault[i][2]=pageFaultCounter*1000/(double)(seqOfPageReferenes.length);

			// Print Page Fault for current Test Case
			System.out.println("Total Page Fault for Test Case "+(i+1)+" : "+pageFaultCounter);

			pageFaultCounter=0;
			pageFramesAndCounters=null;
		}

		// Print the outputs
		System.out.println();
		String formatter="%20s\t%20s\t%50s\n";
		System.out.format(formatter, "Number of Page Frames","Total Page Faults","Number of Page Faults per 1K Memory Reference");

		int[] xValues=new int[pageFramesPageFault.length];
		double[] yValues=new double[pageFramesPageFault.length];

		for (int i = 0; i < pageFramesPageFault.length; i++) {
			System.out.format(formatter, (int)pageFramesPageFault[i][0],(int)pageFramesPageFault[i][1],pageFramesPageFault[i][2]);
			xValues[i]=(int)pageFramesPageFault[i][0];
			yValues[i]=pageFramesPageFault[i][2];
		}

		sc.close();

		// Generate a XY graph
		generateGraph(xValues,yValues);
	}

	private static void generateGraph(int[] xValues,double[] yValues) {
		// TODO Auto-generated method stub
		// Graph
		XYLineChart_AWT chart = new XYLineChart_AWT("XY Graph", "Fault Rate-No. of Page Frames","No. of Page Frames","Fault Rate/1K Memory Reference","Fault Rate-No. of Page Frames",xValues,yValues);
		chart.pack( );          
		RefineryUtilities.centerFrameOnScreen( chart );          
		chart.setVisible( true ); 
	}

	void checkAndAssignPage(int pageNumber){

		boolean isPageAssigned=false;

		// Check if page is already assigned or not
		// Also, do the right shift operation on all page frame's respective counters
		for (int i = 0; i < pageFramesAndCounters.length; i++) {
			pageFramesAndCounters[i][1]=pageFramesAndCounters[i][1]>>1;
		}

		for (int j = 0; j < pageFramesAndCounters.length; j++) {
			// If page is already there set the leftmost bit 
			if(pageFramesAndCounters[j][0]==pageNumber){

				// Page is already assigned
				isPageAssigned=true;

				// Set the Most Significant Bit
				pageFramesAndCounters[j][1] |= (1<<32);
				
				break;
			}
		}

		if(!isPageAssigned){
			// Page Fault occurred, Increment the counter
			pageFaultCounter++;

			for (int i = 0; i < pageFramesAndCounters.length; i++) {

				// If there's space in page table assign the page
				if(pageFramesAndCounters[i][0]==-1){

					pageFramesAndCounters[i][0]=pageNumber;
					pageFramesAndCounters[i][1]=1;

					isPageAssigned=true;
					break;
				}
			}

			if(!isPageAssigned){
				// Page Table is FULL
				// We will replace the current page with the page having minimum counter

				int minCounter=pageFramesAndCounters[0][1];
				int minCounterIndex=0;

				for (int i = 0;i<pageFramesAndCounters.length; i++) {

					if(pageFramesAndCounters[i][1]<=minCounter){
						minCounter=pageFramesAndCounters[i][1];
						minCounterIndex=i;
					}
				}

				// Swap the current page with the one with minimum counter
				pageFramesAndCounters[minCounterIndex][0]=pageNumber;
				pageFramesAndCounters[minCounterIndex][1]=1;

				// Set page to assigned
				isPageAssigned=true;
			}
		}
	}

}

// Class to generate Graph
class XYLineChart_AWT extends ApplicationFrame {

	public XYLineChart_AWT( String applicationTitle, String chartTitle,String xTitle,String yTitle,String datasetTitle,int[] xValues,double[] yValues)
	{
		super(applicationTitle);
		JFreeChart xylineChart = ChartFactory.createXYLineChart(
				chartTitle ,
				xTitle,
				yTitle,
				createDataset(datasetTitle,xValues,yValues) ,
				PlotOrientation.VERTICAL ,
				true , true , false);

		ChartPanel chartPanel = new ChartPanel( xylineChart );
		chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
		final XYPlot plot = xylineChart.getXYPlot( );
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
		renderer.setSeriesPaint( 0 , Color.RED );
		plot.setRenderer( renderer ); 
		setContentPane( chartPanel ); 
	}

	private XYDataset createDataset(String datasetTitle, int[] xValues, double[] yValues){
		final XYSeries faultRate = new XYSeries(datasetTitle);

		for (int i = 0; i < xValues.length; i++) {
			faultRate.add( xValues[i] , yValues[i]);          
		}

		final XYSeriesCollection dataset = new XYSeriesCollection( );          
		dataset.addSeries( faultRate );          
		return dataset;
	}
}
