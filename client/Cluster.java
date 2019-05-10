import java.util.ArrayList;

public class Cluster {

	private ArrayList<Server> servers = new ArrayList<Server>();
	private Server[] xmlServers;

	Cluster(ArrayList<Server> servers, Server[] xmlServers) {
		this.servers = servers;
		this.xmlServers = xmlServers;
	}

	// 0=inactive, 1=booting, 2=idle, 3=active, 4=unavailable

	public Server bestFit(Job job) {
		int bestFit = Integer.MAX_VALUE;
		int minAvail = Integer.MAX_VALUE;
		Server best = null;
		Boolean found = false;

		// for else, need to be able to compare initial stats, not currently updated.
		for (Server serv : servers) {
			if ((serv.coreCount >= job.cpuCores && serv.disk >= job.disk && serv.memory >= job.memory)) {
				int fitnessValue = serv.coreCount - job.cpuCores;
				if ((fitnessValue < bestFit) || (fitnessValue == bestFit && serv.availableTime < minAvail)) {
					bestFit = fitnessValue;
					minAvail = serv.availableTime;
					if (serv.state == 0 || serv.state == 1 || serv.state == 2 || serv.state == 3) {
						found = true;
						best = serv;
					}
				}
			}
		}
		if (found) {
			return best;
		} else {
			int bestFitAlt = Integer.MAX_VALUE;
			Server servAlt = null;
			for (Server serv : xmlServers) {
				int fitnessValueAlt = serv.coreCount - job.cpuCores;
				if (fitnessValueAlt >= 0 && fitnessValueAlt < bestFitAlt && serv.disk > job.disk
						&& serv.memory > job.memory) {
					bestFitAlt = fitnessValueAlt;
					servAlt = serv;
				}
			}
			servAlt.id = 0;
			return servAlt;
		}
	}

	public Server firstFit(Job job) {
		Server[] sortedServers = sortByID(xmlServers);

		for (Server serv : sortedServers) {
			for (Server serv2 : servers) {
				if (serv.type.equals(serv2.type)) {
					if (serv2.coreCount >= job.cpuCores && serv2.disk >= job.disk && serv2.memory >= job.memory
							&& serv2.state != 4 && serv2.state != 3) {
						return serv2;
					}
				}
			}
		}
		for (Server serv : xmlServers) {
			if (serv.coreCount >= job.cpuCores && serv.disk >= job.disk && serv.memory >= job.disk) {
				return serv;
			}
		}
		return null;
	}

	// for(int i = 0; i < 9; i++)
	// {
	// if(serv.coreCount==Math.pow(2, i))
	// {
	// if (serv.coreCount >= job.cpuCores && serv.disk >= job.disk && serv.memory >=
	// job.memory)
	// {
	// first = serv;
	// if(first.state != 4)
	// {
	// firstActive = first;
	// }
	// return first;
	// }
	// }
	// }

	public Server[] sortByID(Server[] servArr) {
		int n = servArr.length;
		for (int i = 0; i < n - 1; i++) {
			for (int j = 0; j < n - i - 1; j++) {
				if (servArr[j].coreCount > servArr[j + 1].coreCount) {
					Server temp = servArr[j];
					servArr[j] = servArr[j + 1];
					servArr[j + 1] = temp;
				}
			}
		}
		return servArr;
	}
}