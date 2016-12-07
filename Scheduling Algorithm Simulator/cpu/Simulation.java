package cpu;

import Algorithm.AAIRR;
import Algorithm.ANRR;
import Algorithm.ARRSTC;
import Algorithm.FCFS;
import Algorithm.IARR;
import Algorithm.IRR;
import Algorithm.IRRVQ;
import Algorithm.MBRR;
import Algorithm.MyAlgorithm;
import Algorithm.OptimizedRR;
import Algorithm.Priority1;
import Algorithm.Priority2;
import Algorithm.RR;
import Algorithm.SJF;
import Algorithm.STRF;
import Algorithm.SelfScheduling;
import Algorithm.TARR;
import Items.Job;
import Items.Queue;

/**
 * @author Ahmed Elmowafy
 * This class is responsible for initiating, selecting the algorithm 
 * and doing the step work for the simulation
 */
public class Simulation {
 
    private static MyAlgorithm myAlgorithm; // object used for polymorphism 
    public static int Time;   // current time of the simulation
    public static String AlgorithmType = "FCFS";  // default algroithm type
    public static int Quantum = 25;  // quantum time for round robin algorithm
    public static boolean Finished = false; // show that the simulation is finished
    public static boolean Stoped = true;  // show that the simulation is stoped
    
    /**
     * reset the simulation
     */
    public static void reset()
    {
       Time = 0;  // reset the simulation time
       Finished = false;  // simulation is not finished
    }
    
    /**
     * @return the current ready queue of the working algorithm
     */
    public static Queue getReadyQueue()
    {
        return myAlgorithm.getReadyQueue();
    }
    
    /**
     * let the selected algorithm finish a step
     * @return the current job worked by the algorithm
     */
    public static Job workStep()
    {
        Job job;
        if(Time == 0) {selectAlgorithm();}  // select and init the algorithm
        job = myAlgorithm.nextStep(Time); 
        if(myAlgorithm.isFinished()){Finished = true;} 
        return job;
    }
    
    /**
     * select and initiate the selected algorithm
     */
    private static void selectAlgorithm()
    {
        if(AlgorithmType.equals("FCFS")) {myAlgorithm = new FCFS(MainQueue.get());}  // first come first served
        else if(AlgorithmType.equals("SJF")) {myAlgorithm = new SJF(MainQueue.get());} // shortest job first
        else if(AlgorithmType.equals("Priority1")) {myAlgorithm = new Priority1(MainQueue.get());} // priority non-preemptive
        else if(AlgorithmType.equals("STRF")) {myAlgorithm = new STRF(MainQueue.get());} // shortest time remaining first
        else if(AlgorithmType.equals("Priority2")) {myAlgorithm = new Priority2(MainQueue.get());} // priority preemptive
        else if(AlgorithmType.equals("RR")) {myAlgorithm = new RR(MainQueue.get() , Quantum);}  // Round Robin
        else if(AlgorithmType.equals("IRRVQ")) {myAlgorithm = new IRRVQ(MainQueue.get());}  // IRRVQ
        else if(AlgorithmType.equals("AAIRR")) {myAlgorithm = new AAIRR(MainQueue.get());}  // AAIRR
        else if(AlgorithmType.equals("TARR")) {myAlgorithm = new TARR(MainQueue.get());}  // TARR
        else if(AlgorithmType.equals("SelfScheduling")) {myAlgorithm = new SelfScheduling(MainQueue.get() , Quantum);}
        else if(AlgorithmType.equals("OptimizedRR")) {myAlgorithm = new OptimizedRR(MainQueue.get() , Quantum);}
        else if(AlgorithmType.equals("ANRR")) {myAlgorithm = new ANRR(MainQueue.get() , Quantum);}
        else if(AlgorithmType.equals("MBRR")) {myAlgorithm = new MBRR(MainQueue.get() , Quantum);}//MBRR
        else if(AlgorithmType.equals("ARRSTC")) {myAlgorithm = new ARRSTC(MainQueue.get() , Quantum);}//ARRSTC
        else if(AlgorithmType.equals("IRR")) {myAlgorithm = new IRR(MainQueue.get() , Quantum);}//IRR
        else if(AlgorithmType.equals("IARR")) {myAlgorithm = new IARR(MainQueue.get());}//IARR
    }
}
