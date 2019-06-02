public class Server {

	public int id;
	public String type;
	public int limit;
	public int bootupTime;
	public float rate;
	public int coreCount;
	public int memory;
	public int disk;
	public int state;
	public int availableTime;
	
	public int numAvailable;

	// Get this information from the XML file.
	Server(int id, String type, int limit, int bootupTime, float rate, int coreCount, int memory, int disk) {
		this.id = id;
		this.type = type;
		this.limit = limit;
		this.bootupTime = bootupTime;
		this.rate = rate;
		this.coreCount = coreCount;
		this.memory = memory;
		this.disk = disk;
		this.numAvailable = 0;
	}

	// Get this information from the RESC command.
	Server(String type, int id, int state, int availableTime, int coreCount, int memory, int disk) {
		this.type = type;
		this.id = id;
		this.state = state;
		this.availableTime = availableTime;
		this.coreCount = coreCount;
		this.memory = memory;
		this.disk = disk;
	}

	public Boolean hasEnoughCores(Job j) {
		return (this.coreCount >= j.cpuCores);
	}

	public Boolean hasEnoughMemory(Job j) {
		return (this.memory >= j.memory);
	} 

	public Boolean hasEnoughDisk(Job j) {
		return (this.disk >= j.disk);
	}

	public Boolean canRunJob(Job j) {
		return (this.hasEnoughCores(j) & this.hasEnoughMemory(j) && this.hasEnoughDisk(j));
	}

	public Boolean readyToGo() {
		return (this.state == 0 || this.state == 2);
	}

	public Boolean inGoodState() {
		return (this.state == 0 || this.state == 1 || this.state == 2 || this.state == 3);
	}
}