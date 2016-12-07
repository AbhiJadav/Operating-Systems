package Algorithm;

import java.util.ArrayList;

import Items.Job;
import Items.Queue;

/**
 * @author Priya Maheshwari
 * "Round Robin" algorithm let every job to be processed by the CPU for a 
 * certain time "quantum time" then replace it the next job in the ready queue.
 * when a job finishes its quantum time it get to the end of the ready queue.
 * 
 * the program here works step by step so we have to make a variable (process time)
 * to track what's the time left form the quantum time for a specific job.
 * 
 */
public class SelfScheduling extends MyAlgorithm{

	private int Quantum; // quantum time of the algorithm
	private int processTime; // the remaining of the quantum time for a specific job

	/**
	 * pass the work queue to super class to initialize lists
	 * @param workQueue queue of lists to be worked on
	 */
	public SelfScheduling(Queue workQueue , int quantum)
	{
		super(workQueue);
	}
	// function to calculate time quantum
	// Calculate median
	public void calculateQuantum() {
		int median = 0;
		Queue q_copy = getReadyQueue();
		q_copy.OrderedByShortest();
		int x = q_copy.size();
		if (x == 1) {
			median = q_copy.getJob(0).remaining;
		}
		else{    
			int mid = x/2;
			if (x%2 == 0){
				median = (q_copy.getJob(mid-1).remaining+q_copy.getJob(mid).remaining)/2;
			}else{
				median = q_copy.getJob(mid).remaining;
			}
		}
		// if median is less than 25 make it 25
		Quantum = median;
		if(Quantum < 25)
			Quantum = 25;
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
		// Check size of queue to determine if a new process is arrived
		if (previous_size != new_size){
			calculateQuantum();
		     //call calculateQuantum();
		    quantum_recal_flag = false;
		     // null the pointer
			current_job = -1;
		}     
		if(!busy) // if CPU is not processing a job ( RR is non-preemptive algorithm)
		{
			context = context+ 1;
			if(simulationTime!=0 && currentJob.getRemainTime() !=0) 
			{
				readyQueue.addJob(currentJob); 
				if (readyQueue.size() == 1){
					context = context -1;
				}
				if (quantum_recal_flag == false) {
					quantum_recal_flag = true;
					current_job =currentJob.jobNumber;
				}
			} // if job is not finished add it to the ready queue again
			if(readyQueue.isEmpty()) {return null;}
			//if first element of ready queue == above pointer and quantum_recal_flag== true
			if (readyQueue.getJob(0).jobNumber == current_job && quantum_recal_flag == true){
				//call calculateQuantum()
				calculateQuantum();
		    	//quantum_recal_flag = false
			    // pointer null
				quantum_recal_flag = false;
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
