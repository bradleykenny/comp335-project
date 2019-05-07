import java.util.ArrayList;

public class Cluster {
	
	private ArrayList<Server> servers = new ArrayList();

	Cluster(ArrayList<Server> servers) {
		this.servers = servers;
	}

	public Server bestFit(Server job) {
		int bestFit = Integer.MAX_VALUE;
		int minAvail = Integer.MAX_VALUE;
		Server best;
		Boolean found = false;
		for (Server serv : servers) {
			if (serv.coreCount > job.coreCount && serv.disk > job.disk && serv.memory > job.memory) {
				int fitnessValue = serv.coreCount - job.coreCount;
				if ((fitnessValue < bestFit) || (fitnessValue == bestFit && serv.availableTime < minAvail)) {
					bestFit = fitnessValue;
					minAvail = serv.availableTime;
					found = true;
					best = serv;
				}
			}
		}
		if (found) {
			return best;
		}
		return "";
	}
}