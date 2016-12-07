package Algorithm;

import Items.Job;
import Items.Queue;

/**
 * @author Pooja Nanjundaswamy
 * AAIRR algorithm was designed as a further improvement on another dynamic time slice RR algorithm namely IRR. 
 * The new algorithm uses a static time quantum same as that of Round Robin initially. 
 * The decision on which process to execute next depends on the remaining burst time. 
 * First process that arrives to ready queue is run for one time quantum. 
 * Then, if the remaining burst time of that process is less than or equal to time quantum, it continues to use CPU till it terminates.
 * Otherwise, it is moved to the tail of ready queue. The next shortest process will get to use CPU after that.
 * 
 */
public class AAIRR extends MyAlgorithm{

	private int Quantum; // quantum time of the algorithm
	private int processTime; // the remaining of the quantum time for a specific job
	
	int myjobcycle[];
	int currentcycle = 1;
	int counter[];
	Job myjob[];
	int context=0;
	private int previousJobId=-1;

	/**
	 * pass the work queue to super class to initialize lists
	 * @param workQueue queue of lists to be worked on
	 */
	public AAIRR(Queue workQueue)
	{
		super(workQueue);
		this.Quantum = 25;  // set the wanted quantum time
		myjobcycle = new int[workQueue.size()];
		counter = new int[workQueue.size()];
		myjob = new Job[workQueue.size()];
		for(int i=0;i<myjobcycle.length;i++)
			{
				myjobcycle[i]=1;
				counter[i]=0;	
			}
	}

	/**
	 * shows what happen in a single step when using this algorithm
	 * @param simulationTime current time of this simulation
	 * @return job the CPU was working on
	 */
	@Override
	public Job nextStep (int simulationTime)
	{
		updateReadyQueue(simulationTime); //add newly arrived jobs to the ready queue
		if(currentcycle==1 && simulationTime>0 )
		{
			readyQueue.OrderedByShortRemain(); //order ready queue by shortest remaining time in the first cycle
		}
		if(!busy) //if CPU is not processing a job ( RR is non-preemptive algorithm)
		{
			if(simulationTime!=0 && currentJob.getRemainTime() !=0) 
			{
				//if current job's remaining time is greater than quantum, it needs to go to the next cycle 
				if(currentJob.getRemainTime() > Quantum)
				{
					myjob[currentJob.getJobId() - 1]= currentJob;
					counter[currentJob.getJobId() - 1]=1;
					myjobcycle[currentJob.getJobId() - 1] = myjobcycle[currentJob.getJobId() - 1] + 1; 
				}
			} 
			//if ready queue is empty, add the jobs ready to run in the next cycle 
			if(readyQueue.isEmpty()) {
				
				int flag=0;
				for(int i=0;i<counter.length;i++)
				{
					if(counter[i]!=0)
					{
						readyQueue.addJob(myjob[i]); 
						readyQueue.OrderedByShortRemain();
						counter[i]=0;
						flag=1;
					}
				}
				//if no jobs left to run and the current job has finished, execution of all processes is done
				if(flag==0 && currentJob.finished)
				{
					return null;
				}
			}
			processTime = Quantum;  //restart quantum time for the new job
			busy = true;
			//if current job's remaining time is greater than quantum or zero, then its time for another job to get CPU
			if(currentJob.getRemainTime() > Quantum || simulationTime==0 || currentJob.getRemainTime()==0)
			{		
				setCurrentJob(); //move the first job in the ready queue to be the current working job
				if(currentJob.getJobId() == previousJobId)
				{
					//do nothing
				}
				else
				{
					context=context+1; //calculate context switches
				}
				previousJobId = currentJob.getJobId();
			}
			
			//when the cycle of new job is changed, set its cycle as the current cycle
			if(currentcycle != myjobcycle[currentJob.getJobId() - 1])
			{			
				currentcycle = myjobcycle[currentJob.getJobId() - 1];
			}
			System.out.println("No of Context switch : "+(context-1));
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
