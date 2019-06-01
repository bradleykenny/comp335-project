public class Job {

	public int submitTime;
	public int id;
	public int estRuntime;
	public int cpuCores;
	public int memory;
	public int disk;

	Job(int submitTime, int id, int estRuntime, int cpuCores, int memory, int disk) {
		this.submitTime = submitTime;
		this.id = id;
		this.estRuntime = estRuntime;
		this.cpuCores = cpuCores;
		this.memory = memory;
		this.disk = disk;
	}
}