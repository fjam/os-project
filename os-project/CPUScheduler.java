//package os;

import java.util.*;

public class CPUScheduler {
	/*
	 * Shortest Remaining Time Next
	 */
	
	public static int scheduler(List<Job> jobTable){
		//OVER 9000!!!!!!!!
		int minTime = 900000000, jobNumberToRun = -1; 
		for (int i = 0; i < jobTable.size(); i++){
			if (jobTable.get(i).getInMemory() 
				&& !jobTable.get(i).getBlocked()
				&& jobTable.get(i).getMaxCPUTime() > 0 
				&& !jobTable.get(i).getIsKilled()){

					if(jobTable.get(i).getMaxCPUTime() < minTime){
						minTime = jobTable.get(i).getMaxCPUTime();
						jobNumberToRun = i;
					}
			}
		}
		return jobNumberToRun;
	}

}
