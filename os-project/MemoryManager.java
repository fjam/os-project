
import java.util.*;

public class MemoryManager {
	private List<Integer> memory;
	public final int MAX_SIZE = 100; 
	public int memoryAvailable;

	public void printMemTable(){
		System.out.println(memory.toString());
		System.out.println("printMemTable was actually called");
	}

	public MemoryManager() {
		//Initialize the linked list up to 100 of zeros
		memory = new LinkedList<Integer>();
			for(int i = 0; i < MAX_SIZE; i++){
				memory.add(i, 0);
			}
		memoryAvailable = 0;
	}

	/*
	 * Searches through the linked list to find a 
	 * a free space to store a job
	 * If it cant, returns -1
	 */
	public int getSpotInMemory(){
		memoryAvailable = 0;
		Boolean start = true;
		int startingLocation = -1, endLocation = 0;
		for (int i = 0; i < MAX_SIZE; i++){
			//System.out.print("MEMORY.GET(i): " + memory.get(i) + " ");

			if (memory.get(i) == 0 && start){
				start = false;
				startingLocation = i;
			}

			if ((memory.get(i) == 1 && !start)  || i == 99){
				endLocation = i -1;
				//start = true;
			}		
		}

		memoryAvailable = endLocation - startingLocation;
		return startingLocation;
	}

	//Addes based on jobs location and size
	public void addToMemory (int jobLength, int jobLocation){
		for (int i = jobLocation; i < jobLocation + jobLength; i++){
			memory.set(i, 1);
		}
	}

	//Removes based on jobs location and size
	public void removeFromMemory(int jobLength, int jobLocation){
		for (int i = jobLocation; i < jobLength + jobLocation; i++){
			memory.set(i, 0);
		}
	}

	
	
	
}
