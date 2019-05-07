public class Server {
	
	public int id = -1;
	public String type = "";
	public int limit = -1;
	public int bootupTime = -1;
	public float rate = -1;
	public int coreCount = -1;
	public int memory = -1;
	public int disk = -1;

	// these values are found using RESC
	public int state;
	public int availableTime;

	Server(int id, String t, int l, int b, float r, int c, int m, int d) {
		this.id = id;
		this.type = t;
		this.limit = l;
		this.bootupTime = b;
		this.rate = r;
		this.coreCount = c;
		this.memory = m;
		this.disk = d;
	}

	Server(String type, int id, int state, int availableTime, int coreCount, int memory, int disk) {
		this.type = type;
		this.id = id;
		this.state = state;
		this.availableTime = availableTime;
		this.coreCount = coreCount;
		this.memory = memory;
		this.disk = disk;
	}
}