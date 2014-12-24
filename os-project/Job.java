
public class Job {

	private int jobNumber;
	private int priority;
	private int jobSize;
	private int maxCPUTime;
	private int entryTime;
	private int currentTime;
	private int ioRequests;
	private int location;
	private Boolean inMemory = false;
	private Boolean blocked = false;
	private Boolean isKilled = false;
	private Boolean isLatched = false; 
	private Boolean inCPU = false;
	
	
	public Job(int jobNumber, int priority, int jobSize, int maxCPUTime,
			int entryTime) {
		super();
		this.jobNumber = jobNumber;
		this.priority = priority;
		this.jobSize = jobSize;
		this.maxCPUTime = maxCPUTime;
		this.entryTime = entryTime;
		//this.currentTime = entryTime;
		ioRequests = 0;
	}

	public int getJobNumber() {
		return jobNumber;
	}


	public void setJobNumber(int jobNumber) {
		this.jobNumber = jobNumber;
	}


	public int getPriority() {
		return priority;
	}


	public void setPriority(int priority) {
		this.priority = priority;
	}


	public int getJobSize() {
		return jobSize;
	}


	public void setJobSize(int jobSize) {
		this.jobSize = jobSize;
	}


	public int getMaxCPUTime() {
		return maxCPUTime;
	}


	public void setMaxCPUTime(int maxCPUTime) {
		this.maxCPUTime = maxCPUTime;
	}


	public int getEntryTime() {
		return entryTime;
	}


	public void setEntryTime(int entryTime) {
		this.entryTime = entryTime;
	}
	
	public int getCurrentTime(){
		return currentTime;
	}

	public void setCurrentTime(int currentTime){
		this.currentTime = currentTime;
	}
	
	public void setIoRequests(int ioRequests){
		this.ioRequests = ioRequests;
	}
	
	public int getIoRequests(){
		return ioRequests;
	}
	
	public int getLocation(){
		return location;
	}
	
	public void setLocation(int location){
		this.location = location;
	}

	public Boolean getInMemory() {
		return inMemory;
	}


	public void setInMemory(Boolean inMemory) {
		this.inMemory = inMemory;
	}


	public Boolean getBlocked() {
		return blocked;
	}


	public void setBlocked(Boolean blocked) {
		this.blocked = blocked;
	}


	public Boolean getIsKilled() {
		return isKilled;
	}


	public void setIsKilled(Boolean isKilled) {
		this.isKilled= isKilled;
	}

	public void setIsLatched(Boolean isLatched){
		this.isLatched = isLatched;
	}

	public Boolean getIsLatched(){
		return isLatched;
	}

	public Boolean getInCPU() {
		return inCPU;
	}


	public void setInCPU(Boolean inCPU) {
		this.inCPU = inCPU;
	}
	
	
	
	
}
