package Algorithm;

import Items.Job;
import Items.Queue;

/**
 * @author Pooja Nanjundaswamy
 * IRRVQ algorithm enhances CPU performance using the features of Shortest Job First and Round Robin Scheduling with varying time quantum. 
 * Initially the processes in the ready queue are arranged in the ascending order of their remaining burst time. 
 * The burst time of the first process in Ready queue is taken as the time quantum. 
 * After each round, the ready queue is sorted again based on remaining burst time of processes and a new time quantum is determined. 
 * The numerical analysis shows that the waiting time and turnaround time have been reduced in the IRRVQ scheduling compared to conventional RR.
 * 
 */
public class IRRVQ extends MyAlgorithm{

	private int Quantum; //quantum time of the algorithm
	private int processTime; //the remaining of the quantum time for a specific job
	int myjobcycle[];
	int currentcycle = 1;
	int counter[];
	private boolean initialize = false;
	Job myjob[];
	int context=0;
	private int previousJobId=-1;

	/**
	 * pass the work queue to super class to initialize lists
	 * @param workQueue queue of lists to be worked on
	 */
	public IRRVQ(Queue workQueue)
	{
		super(workQueue);
		
		myjobcycle = new int[workQueue.size()]; //keeps track of the cycle in which the process is
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
		
		//for the first time
		if(!initialize )
		{
			initialize=true;
			if(readyQueue.size() > 1) { readyQueue.OrderedByShortest();} //order ready queue by shortest burst time
			Quantum=readyQueue.getJob(0).burst; //make the first process burst as time quantum
			processTime = Quantum;
		}
		
		if(!busy) //if CPU is not processing a job
		{
			if(simulationTime!=0 && currentJob.getRemainTime() !=0) 
			{
				//if the job has remaining time left, it should proceed to next cycle
				myjob[currentJob.getJobId() - 1]= currentJob;
				counter[currentJob.getJobId() - 1]=1;
				myjobcycle[currentJob.getJobId() - 1] = myjobcycle[currentJob.getJobId() - 1] + 1;
			
			} 
			//if ready queue is empty, then add all the jobs that are ready to be run in the next cycle
			if(readyQueue.isEmpty())
			{
				int flag=0;
				for(int i=0;i<counter.length;i++)
				{
					if(counter[i]!=0)
					{
						readyQueue.addJob(myjob[i]); 
						counter[i]=0;
						flag=1;
					}
				}
				
				if(flag==0)
					return null;
				else
					readyQueue.OrderedByShortRemain();		
			}
			
			busy = true;
			setCurrentJob(); //move the first job in the ready queue to be the current working job	
			if(currentJob.getJobId() == previousJobId)
			{
				//do nothing
			}
			else
			{
				context=context+1; //count the context switch
			}
			
			previousJobId = currentJob.getJobId();
			processTime = Quantum;
			
			//if the current job belongs to a cycle different than the current cycle, then its time to change the time quantum
			if(currentcycle != myjobcycle[currentJob.getJobId() - 1])
			{
				currentcycle = myjobcycle[currentJob.getJobId() - 1];
				Quantum=currentJob.getRemainTime();
				processTime = Quantum;	
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
