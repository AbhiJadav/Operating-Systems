/**
 * 
 */
package Algorithm;

import Items.Job;
import Items.Queue;

/**
 * @author PriyaK
 *
 */
public class ARRSTC extends MyAlgorithm{

	private int Quantum; // quantum time of the algorithm
	private int processTime; // the remaining of the quantum time for a specific job
	
	//added by PriyaK - for dynamic TQ calc when a new job arrives in RQ.
	private boolean newJobArrived=false;
	private int contextSwitch=-1;//context switch counter

	
	/**
	 * pass the work queue to super class to initialize lists
	 * @param workQueue queue of lists to be worked on
	 */
	public ARRSTC(Queue workQueue , int quantum)
	{
		super(workQueue);
		this.Quantum = quantum;  // set the wanted quantum time
	}

	/**@author PriyaK
	 * shows what happen in a single step when using this algorithm
	 * @param simulationTime current time of this simulation
	 * @return job the CPU was working on
	 */
	
	//changes for ARR using Shortest burst and STC.
	@Override
	public Job nextStep (int simulationTime)
	{
		updateReadyQueue(simulationTime); // add newly arrived jobs to the ready queue
		
		if(!busy) // if CPU is not processing a job ( RR is non-preemptive algorithm)
		{
			contextSwitch++;
			if(simulationTime!=0 && currentJob.getRemainTime() !=0) 
			{readyQueue.addJob(currentJob); 
			if(readyQueue.size()==1)
				contextSwitch--;//same process comes in cpu ->not a switch.
			
			} // if job is not finished add it to the ready queue again
			
			//set Quantum to STC based on the number of processes in readyQueue
			if(newJobArrived)
			{
				System.out.println("Ready queue = "+readyQueue);
				
				Quantum=calculateSTC();
				newJobArrived=false;
				
				System.out.println("Sorted ready queue = "+readyQueue);
			}
			
			if(readyQueue.isEmpty()) {return null;}
			processTime = Quantum;  // restart quantum time for the new job
			busy = true;
			setCurrentJob();  // move the first job in the ready queue to be the current working job
			
			System.out.println("Context switches="+contextSwitch);
		}
		return workInCPU(simulationTime); 
	}
	
	//by PriyaK
	//calculate STC based on the number of processes in readyQueue
	private int calculateSTC()
	{
		int STC=25;
		//sort readyQueue in ascending order of BT
		readyQueue.OrderedByShortest();
		//if readyQueue count=1, the first process burst time is the STC.
		//else, calculate STC.
		if(readyQueue.size()==1)
			STC=readyQueue.getJob(0).getRemainTime();
		else if(readyQueue.size()>1)
		{
			//if no of processes=even, STC=Avg BT of all processes.
			if(readyQueue.size()%2==0)//even
			{
				int sum=0;
				//STC=Avg BT of all processes.
				for(int i=0;i<readyQueue.size();i++)
				{
					sum+=readyQueue.getJob(i).getRemainTime();
				}
				
				STC=(int)(sum/(readyQueue.size()));//average rounded down.
			}
			else//odd
			{
				//STC=Median BT of all processes.
				int middleIndex=((readyQueue.size()-1)/2);
				STC=readyQueue.getJob(middleIndex).getRemainTime();
				
			}
		}
		System.out.println("STC = "+STC);
		return STC;
	}
	
	//overridden for ARRSTC modifications
	//by PriyaK
	protected void updateReadyQueue(int simulationTime)
    {
        for (int i = 0 ; i<list.size() ; i++)
        {
            Job temp = list.getJob(i);
            if(temp.arrivalTime == simulationTime)  // if job arrived  
            {
                readyQueue.addJob(temp);  // if job arrived then move it to the ready queue
                
                newJobArrived=true;
                
                list.removeJob(i);   // remove the job from main job list
                i--; // removing reduces the size of the list by one
            }
            
        }
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
