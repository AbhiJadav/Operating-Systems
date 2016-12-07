package Algorithm;

import Items.Job;
import Items.Queue;

/**
 * @author Pooja Nanjundaswamy
 * TARR algorithm focuses only on a particular task set that is applicable for processing at an instance and not the entire ready queue.
 * The task sets are formed based on arrival times.
 * The time quantum is selected from the task set and executes only for that task set. 
 * During every creation of task set, the algorithm will select the best suitable time quantum value with the help of greedy approach. 
 * Since a new task set is formed if a process arrives late, a new time quantum is adopted which ensures fair chance for those tasks that arrive late with small execution time. 
 * This adaptive algorithm performs better in terms of average waiting time (AWT), average turnaround time (ATT) and most significantly context switches (CS).
 * 
 */
public class TARR extends MyAlgorithm{

	private int Quantum; // quantum time of the algorithm
	private int processTime; // the remaining of the quantum time for a specific job
	int myjobcycle[];
	int currentcycle = 1;
	int counter[];
	private boolean initialize = false;
	Job myjob[];
	Queue taskSet;
	private int newjobs=0;
	private int countjobs=0;
	private boolean jobtoReadyQueueRemaining=false;
	int context=0;
	private int previousJobId=-1;
	
	/**
	 * pass the work queue to super class to initialize lists
	 * @param workQueue queue of lists to be worked on
	 */
	public TARR(Queue workQueue)
	{
		super(workQueue);
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
		//count no of jobs that entered ready queue
		if(updateReadyQueue_TARR(simulationTime))
		{
			newjobs++;
		}

		if(readyQueue.size() > 1) { readyQueue.OrderedByArrive();} //order ready queue by arrival time
		//for the first time execution
		if(!initialize )
		{
			initialize=true;
			if(readyQueue.size() > 1) 
			{
				Queue q = getReadyQueue();
				q.OrderedByShortRemain();
				Quantum = q.getJob(0).burst;//choose the time quantum of the shortest job		
			}
			else
			{
				Quantum=readyQueue.getJob(0).burst;
			}
				
			processTime = Quantum;
		}
		
		
		if(!busy) //if CPU is not processing a job
		{
			if(newjobs>0)
			{
				countjobs = newjobs;
				newjobs=0;
			}
			
			//add jobs to next cycle if they have remaining time to run
			if(simulationTime!=0 && currentJob.getRemainTime() !=0) 
			{
				myjob[currentJob.getJobId() - 1]= currentJob;
				counter[currentJob.getJobId() - 1]=1;
				myjobcycle[currentJob.getJobId() - 1] = myjobcycle[currentJob.getJobId() - 1] + 1;
				jobtoReadyQueueRemaining=true;
			} 
				
			//when ready queue is empty, add the jobs that are ready to run 
			if(readyQueue.isEmpty())
			{
				int flag=0;
				for(int i=0;i<counter.length;i++)
				{
					if(counter[i]!=0)
					{
						readyQueue.addJob(myjob[i]); 
						counter[i]=0;
						jobtoReadyQueueRemaining=false;
						flag=1;
					}
				}
				
				if(flag==0)
					return null;
				else
					readyQueue.OrderedByArrive();
				
			}
			
			busy = true;	
			
			//set the quantum to shortest burst time that is greater than the current quantum for the newly arrived jobs
			if(countjobs>0)
			{
				countjobs=0;
				Queue q = getReadyQueue();
				q.OrderedByShortRemain();
				int jobsize = q.size();
				for(int i=0;i<jobsize;i++)
				{
					if(q.getJob(i).burst >= Quantum)
					{
						Quantum = q.getJob(i).burst;
						processTime = Quantum;
						break;
					}
				}
				
				setCurrentJob();
				if(currentJob.getJobId() == previousJobId)
				{
					//do nothing
				}
				else
				{
					context=context+1;//count no of context switches
				}
			}
			else
			{
				//set the quantum to shortest remaining time that is greater than the current quantum for the pending jobs
				int myQuantum=0;
				Queue q = getReadyQueue();
				q.OrderedByShortRemain();
				int jobsize = q.size();
				for(int i=0;i<jobsize;i++)
				{
					if(q.getJob(i).getRemainTime() > Quantum)
					{
						myQuantum = q.getJob(i).getRemainTime();
						break;
					}
				}
				
				setCurrentJob(); 
				if(currentJob.getJobId() == previousJobId)
				{
					//do nothing
				}
				else
				{
					context=context+1;
				}
				previousJobId = currentJob.getJobId();
				
				//if the current job's cycle changes, then its time to change the quantum for this new task set
				if(currentcycle != myjobcycle[currentJob.getJobId() - 1])
				{			
					currentcycle = myjobcycle[currentJob.getJobId() - 1];			
					Quantum=myQuantum;
					processTime = Quantum;
				}
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
		//System.out.println("[pooja] RR.java workInCPU processTime :"+processTime+ " jobid : "+currentJob.getJobId());
		currentJob.jobWorked(simulationTime);
		processTime--;  // the rest of quantum time for this time of working of the job
		if(processTime == 0 || currentJob.getRemainTime() ==0 ) 
		{busy = false;} // if job is finished or round time is finished, make CPU not busy
		return currentJob;
	}

	/**
	 * ensure all jobs have finished execution by checking all possible conditions below
	 */
	public boolean isFinished()
    {
        return (list.isEmpty() && readyQueue.isEmpty()  && !busy &&  currentJob.getRemainTime() == 0 && !jobtoReadyQueueRemaining);
    }
	
}
