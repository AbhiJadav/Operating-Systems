import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/*
 * Filename: SemaphoreDemo.java
 * Version: 1.0
 * Objective: Process A reads the file data from the disk to Buffer 1, Process B copies the data from Buffer 1 to Buffer 2, finally Process C takes the data from Buffer 2 and print it.
 * Assume all three processes operate on one (file) record at a time, both buffers' capacity are one record.
 * Write a program to coordinate the three processes using semaphores.
 * Author: Abhishek Jadav
 * Created: 12 Oct, 2016
 * List of API/Library: -
 */

public class SemaphoreDemo {

	// Give the path of the file to open
	private static String fileName = "C:/Users/Admin/Desktop/Operating Systems COEN 283/Assignments/Assignment 2/Programming Question/file.txt";

	// This semaphore is used to represent empty buffers
	private final static Semaphore emptyBuffer1 = new Semaphore(1);
	private final static Semaphore emptyBuffer2 = new Semaphore(1);
	
	// This semaphore is used to represent filled out buffers
	private final static Semaphore fullBuffer1 = new Semaphore(0);
	private final static Semaphore fullBuffer2 = new Semaphore(0);
	
	// Initially all buffers are set empty
	// String Buffers are used as intermediate storage buffers
	private static StringBuffer buffer1=new StringBuffer();
	private static StringBuffer buffer2=new StringBuffer();
	private static String ls = System.getProperty("line.separator");

	public static void main(String[] args) {

		// You can start these Threads in any order
		// Because of the Semaphore Mutual Exclusion will be there between the Threads

		// Start reading File
		new ReadFile().start();

		// Start copying content of buffer1 to buffer2
		new CopyBuffer().start();

		// Start printing contents of buffer2
		new PrintBuffer().start();
	}


	static class ReadFile extends Thread {

		private BufferedReader br;
		private FileReader fr;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();

			try {
				// If buffer1 is empty grab it and start filling it, otherwise wait
				emptyBuffer1.acquire();

				// CRITICAL SECTION OF READING FILE

				// Open Buffer to read the contents of the file
				fr = new FileReader(fileName);
				br = new BufferedReader(fr);

				String nextLine;
				while((nextLine=br.readLine())!=null){
					// Save the lines in the file in a String Buffer along with a separator
					buffer1.append(nextLine);	
					buffer1.append(ls);
				}

				System.out.println("Process A started reading file from the disk...\nFile Reading completed...\nHere's the content in buffer1 : "+buffer1.toString());

				// Signal that empty buffer2 is available now to get filled up
				fullBuffer1.release();

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}				
		}
	}

	static class CopyBuffer extends Thread{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();

			try {
				// If buffer1 is full, grab it and start copying FROM it otherwise wait
				fullBuffer1.acquire();
				// If buffer2 is empty, grab it and start copying TO it otherwise wait
				emptyBuffer2.acquire();

				// CRITICAL SECTION OF COPYING FROM buffer1's content to buffer2's content
				buffer2.append(buffer1.toString());

				System.out.println("Process B started copying from Buffer 1 to Buffer 2...\nCopying is completed...\nHere's the copied content in buffer2 : "+buffer2.toString());

				// Declare that empty buffer1 is available now to get filled up
				emptyBuffer1.release();
				// Signal that full buffer2 is available now to get printed
				fullBuffer2.release();	
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}				
		}
	}


	static class PrintBuffer extends Thread{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();

			try {
				// If buffer2 is full, grab it and start printing it, otherwise wait
				fullBuffer2.acquire();

				// Print contents in buffer1
				System.out.println("Process C started printing out contents of buffer2...\n"+buffer2.toString());

				// Signal that empty buffer2 is available now to get filled up
				emptyBuffer2.release();	

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// A semaphore class to keep sync whenever lock() and acquire() is called
	// By using release() and acquire() we are making sure that Mutual Exclusion is there
	public static class Semaphore {
		private int semaphoreCounter;

		public Semaphore(int semaphoreCounter) {
			// Semaphore's value can't be negative
			if (semaphoreCounter < 0) {
				throw new IllegalArgumentException(semaphoreCounter + " < 0");
			}
			this.semaphoreCounter = semaphoreCounter;
		}

		public synchronized void release() {
			if (semaphoreCounter == 0) {
				// If the counter is 0 there might me some Thread already be waiting, so notify them
				this.notify();
			}
			semaphoreCounter++;
		}

		public synchronized void acquire() throws InterruptedException {
			while (semaphoreCounter == 0) {
				// If the counter is 0 just wait do nothing
				this.wait();
			}
			semaphoreCounter--;
		}
	}
}
