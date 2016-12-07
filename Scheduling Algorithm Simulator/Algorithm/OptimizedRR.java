package Algorithm;

import java.util.ArrayList;

import Items.Job;
import Items.Queue;

/**
 * @author Priya Maheshwari
 * "Optimized Round Robin" algorithm let every job to be processed by the CPU for a 
 * certain time "quantum time" based on average then replace it the next job
 * in the ready queue.
 * when a job finishes its quantum time it get to the end of the ready queue.
 * 
 * the program here works step by step so we have to make a variable (process time)
 * to track what's the time left form the quantum time for a specific job.
 * 
 */
public class OptimizedRR extends MyAlgorithm{

	private int Quantum; // quantum time of the algorithm
	private int processTime; // the remaining of the quantum time for a specific job

	/**
	 * pass the work queue to super class to initialize lists
	 * @param workQueue queue of lists to be worked on
	 */
	public OptimizedRR(Queue workQueue , int quantum)
	{
		super(workQueue);
	}
	// function to calculate time quantum
	// Calculation on the basis of average
	public void calculateQuantum() {
		int average = 0;
		int sum = 0;
		Queue q_copy = getReadyQueue();
		int x = q_copy.size();
		for (int i =0 ;i< x;i++){
			sum = sum + q_copy.getJob(i).remaining;
		}
		average = sum/x;
		Quantum = average;
		System.out.println("Quantum is"+Quantum);
		
	}

	/**
	 * shows what happen in a single step when using this algorithm
	 * @param simulationTime current time of this simulation
	 * @return job the CPU was working on
	 */
	boolean quantum_recal_flag = false;
	int current_job = -1;
	int context = 0;
	@Override
	public Job nextStep (int simulationTime)
	{
		//Get existing size of ReadyQueue.
		int previous_size = getReadyQueue().size();
		// add newly arrived jobs to the ready queue
		updateReadyQueue(simulationTime);
		int new_size = getReadyQueue().size();
		if (previous_size != new_size){
			calculateQuantum();
			// Arrange the ready queue according to CPU Burst Time
			readyQueue.OrderedByShortRemain();
		    quantum_recal_flag = false;
		    // null the pointer
			current_job = -1;
		}     
		if(!busy) // if CPU is not processing a job ( RR is non-preemptive algorithm)
		{   context = context+ 1;
			if(simulationTime!=0 && currentJob.getRemainTime() !=0) 
			{
				readyQueue.addJob(currentJob);
				if (readyQueue.size() == 1){
					context = context -1;
				}
				if (quantum_recal_flag == false) {
					quantum_recal_flag = true;
					current_job =currentJob.jobNumber;
					//find a way to save currentJob
				}
			} // if job is not finished add it to the ready queue again
			if(readyQueue.isEmpty()) {return null;}
			//if first element of ready queue == above pointer and quantum_recal_flag== true
			if (readyQueue.getJob(0).jobNumber == current_job && quantum_recal_flag == true){
				calculateQuantum();
				quantum_recal_flag = false;
				// Arrange the ready queue according to CPU Burst Time
				readyQueue.OrderedByShortRemain();
				current_job = -1;
			}
			processTime = Quantum;  // restart quantum time for the new job
			busy = true;
			setCurrentJob();  // move the first job in the ready queue to be the current working job
			System.out.println("Context Switch Number"+ (context-1));
			    
		}
		return workInCPU(simulationTime); 
	}

	/**
	 * work the current job in the CPU for one simulation time step
	 * @param simulationTime current time of the simulation
	 * @return the current job the CPU is working on
	 */
	@Override
	protected Job workInCPU(int simulationTime)
	{  
		currentJob.jobWorked(simulationTime);
		processTime--;  // the rest of quantum time for this time of working of the job
		if(processTime == 0 || currentJob.getRemainTime() ==0 ) 
		{busy = false;} // if job is finished or round time is finished, make CPU not busy
		return currentJob;
	}

	
}
