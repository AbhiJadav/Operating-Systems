import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/*
 * Filename: BankersAlgorithm.java
 * Version: 1.0
 * Objective: Program a simulation of the banker’s algorithm.
 * Your program should cycle through each of the bank clients asking for a request and evaluating whether it is safe or unsafe.
 * Output a log of requests and decisions to a file.
 * Author: Abhishek Jadav
 * Created: 23 Oct, 2016
 * List of API/Library: -
 */

public class BankersAlgorithm {
	private static int currentAllocatedFundMatrix[][];
	private static int neededFundMatrix[][];
	private static int maxRequiredFundMatrix[][];
	private static int availableFundMatrix[][];
	private static int numClients;
	private static int totalResources;
	private static BufferedWriter writer = null;

	public static void main(String[] args) throws IOException {
		
		Scanner sc=new Scanner(System.in);

		// Number of clients
		System.out.print("Enter number of clients : ");
		numClients=sc.nextInt();

		// Number of resources of the the funds
		System.out.print("Enter total number of resources of the funds : ");
		totalResources=sc.nextInt();

		// Matrix of funds needed for client
		neededFundMatrix=new int[numClients][totalResources];

		// Matrix of maximum funds required by client
		maxRequiredFundMatrix=new int[numClients][totalResources];
		
		// Matrix of current allocated funds of the client
		currentAllocatedFundMatrix=new int[numClients][totalResources];
		
		// Matrix of available funds
		availableFundMatrix=new int[1][totalResources];

		System.out.println("Enter Current Allocated Fund Matrix (with a space between two elements):");
		for(int i=0;i<numClients;i++){
			for(int j=0;j<totalResources;j++){
				currentAllocatedFundMatrix[i][j]=sc.nextInt();  
			}
		}

		System.out.println("Enter Maximum Required Fund Matrix (with a space between two elements): ");
		for(int i=0;i<numClients;i++){
			for(int j=0;j<totalResources;j++){
				maxRequiredFundMatrix[i][j]=sc.nextInt();  
			}
		}

		System.out.println("Enter Available Fund Matrix (with a space between two elements): ");
		for(int j=0;j<totalResources;j++){
			availableFundMatrix[0][j]=sc.nextInt(); 
		}
		
		// Calculate Matrix of needed funds by the client
		for(int i=0;i<numClients;i++){
			for(int j=0;j<totalResources;j++){  
				neededFundMatrix[i][j]=maxRequiredFundMatrix[i][j]-currentAllocatedFundMatrix[i][j];
			}
		}
		
		// Prepare writer to write outputs into the file
		try {
			writer = new BufferedWriter(new FileWriter(new File("output.txt")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// To keep track of satisfied clients
		boolean done[]=new boolean[numClients];
		int j=0;

		while(j<numClients){  
			boolean allocated=false;
			for(int i=0;i<numClients;i++){

				if(!done[i]){
					writer.newLine();
					writer.write("Client "+(i+1));
					writer.newLine();

					if(isResourceAvailable(i)){  
						for(int k=0;k<totalResources;k++){
							availableFundMatrix[0][k]=availableFundMatrix[0][k]-neededFundMatrix[i][k]+maxRequiredFundMatrix[i][k];
						}
						allocated=done[i]=true;
						j++;
					}

					// Print the decision
					if(allocated){
						writer.newLine();
						writer.write("Decision : Safe");
						writer.newLine();
					}else{
						writer.newLine();
						writer.write("Decision : Unsafe");
						writer.newLine();
					}					
				}
			}

			if(!allocated){ 
				break;
			}
		}
		
		// Check if all clients are satisfied safely or not, if not print deadlocked client(s)
		if(j==numClients){
			writer.newLine();
			writer.write("All clients are allocated funds safely.");
			writer.newLine();
		} else{
			writer.newLine();
			writer.write("There's a Deadlock in the system. Client(s) in Deadlock are :");
			writer.newLine();
			
			for (int i = 0; i < done.length; i++) {
				if(!done[i]){
					writer.write("Client "+(i+1));
					writer.newLine();
				}
			}
		}
		
		writer.close();
		
		System.out.println("\nSee the file 'output.txt' for the logs.");
		
		sc.close();
	}
	
	// Check if each resource of funds required by current client is available
	private static boolean isResourceAvailable(int i) throws IOException{
		boolean allResourceAvailable=true;

		writer.write("Needed Fund Vector : ");
		
		for(int j=0;j<totalResources;j++) {
			writer.write(neededFundMatrix[i][j] +" ");
			if(availableFundMatrix[0][j]<neededFundMatrix[i][j]){
				allResourceAvailable=false;
			}
		}
		
		writer.newLine();
		writer.write("Available Fund Vector : ");
		
		for(int j=0;j<totalResources;j++) {
			writer.write(availableFundMatrix[0][j] +" ");
		}
		
		
		return allResourceAvailable;
	}

}
