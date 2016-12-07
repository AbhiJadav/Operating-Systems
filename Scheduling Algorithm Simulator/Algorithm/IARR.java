package Algorithm;

import Items.Job;
import Items.Queue;
/**
 * @author Abhishek Jadav
 *
 */
public class IARR extends MyAlgorithm{

	private int Quantum; // quantum time of the algorithm
	private int processTime; // the remaining of the quantum time for a specific job
	
	// Check for the next Round
	private boolean nextRound=false;
	private int current_job = -1;
	private int contextSwitch=-1;//context switch counter
	
	// To keep track of Time Quantum
	private int tq=0;
	
	// Maximum TQ for IARR
	private int maxTQ=50;

	/**
	 * pass the work queue to super class to initialize lists
	 * @param workQueue queue of lists to be worked on
	 */
	public IARR(Queue workQueue)
	{
		super(workQueue);
	}

	/**
	 * shows what happen in a single step when using this algorithm
	 * @param simulationTime current time of this simulation
	 * @return job the CPU was working on
	 */
	
	// Each step in IARR
	@Override
	public Job nextStep (int simulationTime)
	{
		updateReadyQueue(simulationTime); // add newly arrived jobs to the ready queue
		
		if(simulationTime==0)
		{
			Quantum=calculateTQ(true);
			
		}
		
		if(!busy) // if CPU is not processing a job ( RR is non-preemptive algorithm)
		{
			contextSwitch++;//count switches
			
			if(simulationTime!=0 && currentJob.getRemainTime() !=0) // if job is not finished add it to the ready queue again
			{
				readyQueue.addJob(currentJob); 
				//currentRoundQueue.addJob(currentJob);
				if(readyQueue.size()==1)
					contextSwitch--;//same process comes in cpu -> not a switch.
				
				//if the job is not finished, it goes to next round, so set the flag
				if (nextRound == false) //only if it is false, we'll get the starting job of next round and set flag.
				{
					nextRound = true;
					current_job =currentJob.jobNumber;
				}
			
			} 
			
			if (readyQueue.getJob(0).jobNumber == current_job && nextRound == true)//new round
			{
				Quantum=calculateTQ(false);
				nextRound = false;
				current_job = -1;
			}
			
			
			if(readyQueue.isEmpty()) {return null;}
			processTime = Quantum;  // restart quantum time for the new job
			busy = true;
			setCurrentJob();  // move the first job in the ready queue to be the current working job
			
			System.out.println("Context switches="+contextSwitch);
		}
		
		return workInCPU(simulationTime); 
	}
	
	// Calculation of TQ for IARR
	private int calculateTQ(boolean firstTime)
	{
		readyQueue.OrderedByShortest();
		// TODO Auto-generated method stub
				if(readyQueue.size()>0){
					if(firstTime){
						tq=readyQueue.getJob(0).getRemainTime();
						System.out.println("=== First TQ = "+tq+"===");
					} else {
						if (tq<=maxTQ){
							for (int i = 0; i < readyQueue.size(); i++) {
								Job tempJob=readyQueue.getJob(i);
								if(tempJob.getRemainTime()>tq){
									tq=tempJob.getRemainTime();
									System.out.println("=== Second TQ = "+tq+"===");
									break;
								}
							}
						} else{
							tq=readyQueue.getJob(0).getRemainTime();
							System.out.println("=== Third TQ = "+tq+"===");
						}
					}
				}

				return tq;
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
