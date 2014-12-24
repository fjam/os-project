/* Patrick Centeno
 * Fahad Jameel
 * Avinash Jairam 
 */

import java.util.*;
import java.util.concurrent.*;


public class os{
	static List<Job> jobTable;
	static List<Integer> IOQueue;
	static MemoryManager memory;
	static List<Integer> drumArray;
	static int counter = 0;
	static int runningJob = -1;
	static int jobCurrentlyMoving; //index of job moving from memory or whatever
	static int jobDoingIO = 0;
	static Boolean isOnTrace;
	static Boolean drumBeingUsed;
	static Boolean diskIsBusy;


	public static void startup(){
		sos.offtrace();
		jobTable = new ArrayList<Job>();
		IOQueue = new ArrayList<Integer>();
		memory = new MemoryManager();
		drumArray = new ArrayList<Integer>();
		drumBeingUsed = false;
		diskIsBusy = false;
	}

	public static void Crint(int [] a, int [] p){ 
		//System.out.println("Crint is on");
		bookKeeper(p[5]);
		jobTable.add(new Job(p[1], p[2], p[3], p[4], p[5]));
		drumArray.add(p[1]);	
		runningJob = CPUScheduler.scheduler(jobTable);
		//memory.printMemTable();
		runJob(a, p);
		//System.out.println("Crint is over");
	}
	public static void Dskint (int []a, int []p){
		//System.out.println("Dskint is called");
		bookKeeper(p[5]);
		diskIsBusy = false;

		int jobDoingIONumber = findJobIndex(jobDoingIO);
		jobTable.get(jobDoingIONumber).setIoRequests(jobTable.get(jobDoingIONumber).getIoRequests()-1);
		if(jobTable.get(jobDoingIONumber).getIoRequests() == 0){
			jobTable.get(jobDoingIONumber).setBlocked(false);
			jobTable.get(jobDoingIONumber).setIsLatched(false);
		}

		if(jobTable.get(jobDoingIONumber).getIsKilled() 
			&& jobTable.get(jobDoingIONumber).getIoRequests() == 0  
			&& jobTable.get(jobDoingIONumber).getInMemory()){


			memory.removeFromMemory(jobTable.get(jobDoingIONumber).getJobSize(), 
				jobTable.get(jobDoingIONumber).getLocation());
			jobTable.remove(jobDoingIONumber);

			jobTable.get(jobDoingIONumber).setInMemory(false);
		}
		runningJob = CPUScheduler.scheduler(jobTable);
		runJob(a, p);
		//System.out.println("Dskint is over");
	}

	public static void Drmint (int []a, int []p){
		//System.out.println("Drmint is called");
		drumBeingUsed = false;
		bookKeeper(p[5]);


		int jobIndex = findJobIndex(jobCurrentlyMoving);

		if(jobIndex != -1){
			jobTable.get(jobIndex).setInMemory(true);
			memory.addToMemory(jobTable.get(jobIndex).getJobSize(), 
				jobTable.get(jobIndex).getLocation());
		}


		runningJob = CPUScheduler.scheduler(jobTable);
		runJob(a, p);
		//System.out.println("Drmint is over");
	}

	public static void Tro (int []a, int []p){
		//System.out.println("Tro is called");
		bookKeeper(p[5]);

			if(jobTable.get(runningJob).getIoRequests() == 0){
				memory.removeFromMemory(jobTable.get(runningJob).getJobSize(), 
					jobTable.get(runningJob).getLocation());
				jobTable.get(runningJob).setInMemory(false);
				jobTable.remove(runningJob);
			}
			else
				jobTable.get(runningJob).setIsKilled(true);

		runningJob = CPUScheduler.scheduler(jobTable);
		//memory.printMemTable();
		runJob(a, p);
		//System.out.println("Tro is over");
	}

	public static void Svc (int []a, int []p){
		//System.out.println("Svc is called");
		//System.out.println(a[0]);
		bookKeeper(p[5]);
	
		if(a[0] == 5){
			if (jobTable.get(runningJob).getIoRequests() == 0 
				&& jobTable.get(runningJob).getIsKilled()){

				memory.removeFromMemory(jobTable.get(runningJob).getJobSize(), 
					jobTable.get(runningJob).getLocation());

				jobTable.get(runningJob).setInMemory(false);
				jobTable.remove(runningJob);
			}else{
				jobTable.get(runningJob).setIsKilled(true);
			}
		}else if(a[0] == 6){
			int jobNumber = jobTable.get(runningJob).getJobNumber();
			IOQueue.add(jobNumber);
			jobTable.get(runningJob).setIoRequests(jobTable.get(runningJob).getIoRequests() + 1);
			runIO();
		}else if(a[0] == 7){
			if (jobTable.get(runningJob).getIoRequests() > 0)
				jobTable.get(runningJob).setBlocked(true);
		}
	
		runningJob = CPUScheduler.scheduler(jobTable);
		//memory.printMemTable();

		runJob(a, p);
		//System.out.print("Svc is over " + a[0]);
		
	}

	/*
	 * Finds job in drum array to swap into memory
	 * Then updates the job table with the information
	 */
	public static void swapper(){
		//System.out.println("Swapper is called");
		if(!drumBeingUsed){
			for(int i = 0; i < drumArray.size(); i++){
				Job job = findJobById(drumArray.get(i));
				int location = memory.getSpotInMemory();
				if (location != -1 && job.getJobSize() < memory.memoryAvailable){
					job.setLocation(location);
					sos.siodrum(job.getJobNumber(), job.getJobSize(), location, 0);
					drumBeingUsed = true;
					drumArray.remove(i);
					jobCurrentlyMoving = job.getJobNumber();
					updateJob(job);
					break;
				}
			}
		}
	}

	/*
	 * Changes running jobs maxCPU time to maxCPUTime -
	 * the entry time of new job. 
	 */
	public static void bookKeeper(int time){
		if (runningJob != -1 && jobTable.get(runningJob).getInCPU()){
			//jobTable.get(runningJob).setInCPU(false);
			int temp = time - jobTable.get(runningJob).getEntryTime();
			int diff = jobTable.get(runningJob).getMaxCPUTime() - temp; ;
			jobTable.get(runningJob).setMaxCPUTime(diff);
		}
	}

	/*
	 * Calls runIO and swapper
	 * Then checks whether there is job there to run
	 * and then runs it
	 */
	public static void runJob(int [] a, int [] p){
		runIO();
		swapper();
		//System.out.println("runJob is called" + a[0]);

		if(runningJob != -1){
			if(!jobTable.get(runningJob).getInMemory() 
				|| jobTable.get(runningJob).getBlocked()){
				a[0] = 1;
			}
			else {
				a[0] = 2;
				p[2] = jobTable.get(runningJob).getLocation();
				p[3] = jobTable.get(runningJob).getJobSize();
				p[4] = jobTable.get(runningJob).getMaxCPUTime();
				jobTable.get(runningJob).setEntryTime(p[5]);
				jobTable.get(runningJob).setInCPU(true);
			}
			//System.out.println("run job is done" + " " + a[0]);
		}else{
			a[0] = 1;
		}
	}

	/*
	 * Finds job on top of the IOQueue
	 * and then starts it to perform IO
	 */
	public static void runIO(){
		if(!diskIsBusy){
			for(int i = 0; i < IOQueue.size(); i ++){
				Job job = findJobById(IOQueue.get(i));
				if (job.getInMemory()){
					sos.siodisk(IOQueue.get(i));
					jobDoingIO = job.getJobNumber();
					diskIsBusy = true;
					IOQueue.remove(i);
					updateJob(job);
					break;
				}

			}
		}
	}

	/*
	 * Finds a specific job Number based on its 
	 * index in the jobTable
	 */
	public static Job findJobById(int index){
		for (int i = 0; i < jobTable.size(); i++){
			if(jobTable.get(i).getJobNumber() == index){
				return jobTable.get(i);
			}
		}
		return new Job(-1,0,0,0,0);
	}

	/*
	 * Finds a job's index in a the jobTable 
	 * based its job Number (the one 
	 * assigned by sos)
	 */
	public static int findJobIndex(int jobID){
		for (int i = 0; i < jobTable.size(); i++){
			if(jobTable.get(i).getJobNumber() == jobID){
				return i;
			}
		}
		return -1;
	}


	//prints the whole table
	public static void printJobTable(){
		System.out.println("THE WHOLE JOBTABLE  ");
		for (int index =0; index < jobTable.size(); index++){
			System.out.println(jobTable.get(index).getJobNumber() + " "
				+jobTable.get(index).getPriority() + " "
				+jobTable.get(index).getJobSize() + " "
				+jobTable.get(index).getMaxCPUTime() + " "
				+jobTable.get(index).getEntryTime() + " "
				+jobTable.get(index).getCurrentTime() + ""
				+jobTable.get(index).getLocation() + " "
				+jobTable.get(index).getIoRequests() + " "
				+jobTable.get(index).getInMemory() + " "
				+jobTable.get(index).getBlocked() + " "
				+jobTable.get(index).getIsKilled() + " "
				+jobTable.get(index).getIsLatched() + " "
				+jobTable.get(index).getIoRequests() + " "
				+jobTable.get(index).getInCPU() + " ");


			System.out.println("IOQUEE");
				System.out.println(IOQueue.toString()+", ");
		}
	}

	/*
	 * Updates the current job 
	 * Its called in swapper.  This makes sure
	 * the data given to the job being used 
	 * in swapper is updated on the table
	 */
	public static void updateJob(Job newJob){
		for (int i = 0; i < jobTable.size(); i++){
			if(jobTable.get(i).getJobNumber() == newJob.getJobNumber()){
				jobTable.set(i, newJob);
			}
		}

	}





}