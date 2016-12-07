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
public class MBRR extends MyAlgorithm{

	private int Quantum; // quantum time of the algorithm
	private int processTime; // the remaining of the quantum time for a specific job
	
	//added by PriyaK - for dynamic TQ calc for each round and when a new job arrives in RQ.
	private boolean newJobArrived=false;
	private boolean nextRound=false;
	private int current_job = -1;
	private int contextSwitch=-1;//context switch counter

	/**
	 * pass the work queue to super class to initialize lists
	 * @param workQueue queue of lists to be worked on
	 */
	public MBRR(Queue workQueue , int quantum)
	{
		super(workQueue);
		this.Quantum = quantum;  // set the wanted quantum time
		
	}

	/**@author PriyaK
	 * shows what happen in a single step when using this algorithm
	 * @param simulationTime current time of this simulation
	 * @return job the CPU was working on
	 */
	
	//changes for MBRR.
	@Override
	public Job nextStep (int simulationTime)
	{
		updateReadyQueue(simulationTime); // add newly arrived jobs to the ready queue
		
		if(simulationTime==0)
		{
			readyQueue.OrderedByShortest();
			Quantum=calculateTQ();
			
		}
		
		if(!busy) // if CPU is not processing a job ( RR is non-preemptive algorithm)
		{
			contextSwitch++;//count switches
			
			if(simulationTime!=0 && currentJob.getRemainTime() !=0) // if job is not finished add it to the ready queue again
			{
				readyQueue.addJob(currentJob); 
				
				if(readyQueue.size()==1)
					contextSwitch--;//same process comes in cpu ->not a switch.
				
				//if the job is not finished, it goes to next round, so set the flag
				if (nextRound == false) //only if it is false, we'll get the starting job of next round and set flag.
				{
					nextRound = true;
					current_job =currentJob.jobNumber;
				}
			
			} 
			
			
			//set Quantum to sqrt((median^2+avg^2)/2) after each round.
			//if a new job arrives, sort and calculate TQ again.
			
			if(newJobArrived)
			{
				//sort readyQueue in ascending order of BT
				readyQueue.OrderedByShortest();
				Quantum=calculateTQ();
				newJobArrived=false;
			}
			
			if (readyQueue.getJob(0).jobNumber == current_job && nextRound == true)//new round
			{
				Quantum=calculateTQ();
				nextRound = false;
				current_job = -1;
			}
			
			
			if(readyQueue.isEmpty()) {return null;}
			processTime = Quantum;  // restart quantum time for the new job
			busy = true;
			setCurrentJob();  // move the first job in the ready queue to be the current working job
			
			
			//System.out.println("Current round queue = "+currentRoundQueue);
			
			System.out.println("Context switches="+contextSwitch);
		}
		
		return workInCPU(simulationTime); 
	}
	
	//calculate TQ as sqrt((median^2+avg^2)/2) of all jobs in currentRoundQueue. 
	//this TQ is round-specific.
	//by PriyaK
	private int calculateTQ()
	{
		int TQ=25;
		
		//if readyQueue count=1, the first process burst time is the TQ.
		//else, calculate TQ.
		if(readyQueue.size()==1)
			TQ=readyQueue.getJob(0).getRemainTime();
		else if(readyQueue.size()>1)
		{
			//TQ=sqrt((median^2+avg^2)/2)
			
			double avg=0;
			for(int i=0;i<readyQueue.size();i++)
			{
				avg+=readyQueue.getJob(i).getRemainTime();
			}
			avg=(avg/(double)readyQueue.size());//DECIMAL - CORRECTed.************************
			
			double median=0;
			if(readyQueue.size()%2==0)//even
			{
				//size/2, size/2 -1
				int middleIndex1=readyQueue.size()/2;
				int middleIndex2=(readyQueue.size()/2) -1;
				median=(readyQueue.getJob(middleIndex1).getRemainTime() + readyQueue.getJob(middleIndex2).getRemainTime())/2.0;//DECIMAL - CORRECTed.************************
				
			}
			else{//odd
				int middleIndex=((readyQueue.size()-1)/2);
				median=readyQueue.getJob(middleIndex).getRemainTime();
			}
			double x=((avg*avg) + (median*median))/2.0;//DECIMAL - CORRECTed.************************
			//System.out.println(x);System.out.println(Math.sqrt(x));
			double sqrt=Math.sqrt(x);;//Must round to Upper- DONE******************

			if(sqrt>(int)sqrt)
				TQ=(int)sqrt+1;
			else
				TQ=(int)sqrt;
						
		}
		System.out.println("TQ = "+TQ);
		return TQ;
	}
	
	//overridden to modify for MBRR:
	//by PriyaK
	@Override
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
        
        //System.out.println("Ready queue = "+readyQueue);
    }
	
	//overridden for MBRR:
	@Override
	protected void setCurrentJob()
    {
        currentJob = readyQueue.getJob(0); 
        readyQueue.removeJob(0);
        
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
