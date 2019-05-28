import java.util.ArrayList;

public class Cluster {

	private ArrayList<Server> servers = new ArrayList<Server>();
	private Server[] xmlServers;

	Cluster(ArrayList<Server> servers, Server[] xmlServers) {
		this.servers = servers;
		this.xmlServers = xmlServers;
	}

	// STATES: 0=inactive, 1=booting, 2=idle, 3=active, 4=unavailable

	/*
	 * Best-fit algorithm implemented by Bradley Kenny. This algorithm iterates
	 * through the ArrayList and looks for the server that will be the 'best' for
	 * our given job. This is determined by calculating the fitness value and
	 * ensuring the server has enough resources to be able to handle the job.
	 */
	public Server bestFit(Job job) {
		int bestFit = Integer.MAX_VALUE;
		int minAvail = Integer.MAX_VALUE;
		Server best = null;
		Boolean found = false;

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
			// We only want to get here if there is nothing calculated above.
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
			servAlt.id = 0; // If this isn't zero, server thinks it doesn't exist.
			return servAlt;
		}
	}

	/*
	 * First-Fit algorithm implemented by John Kim. Iterate through sorted servers,
	 * compare each jobs' requirements to the servers' capacity and if it can run
	 * the job, assign it to that server. otherwise, look for the next active server
	 * that can run the job and assign it, regardless of how ill-fitting the job
	 * size to the server size.
	 */
	public Server firstFit(Job job) {
		Server[] sortedServers = sortByID(xmlServers);

		// Iterate through the sorted servers and check for the server's available
		// resources and if the server has sufficient amount of resources, assign
		// the job to the server by returning the server which is then passed to
		// the ds-server.
		for (Server serv : sortedServers) {
			for (Server serv2 : servers) {
				if ((serv.type).equals(serv2.type)) {
					if (serv2.coreCount >= job.cpuCores && serv2.disk >= job.disk && serv2.memory >= job.memory
							&& serv2.state != 4) {
						return serv2;
					}
				}
			}
		}
		// For when there aren't any good fit to for job-server
		// iterate through the whole arrayList of servers and find the next active
		// server that can run the job.
		for (Server serv : xmlServers) {
			Server temp = null;
			if (serv.coreCount >= job.cpuCores && serv.disk >= job.disk && serv.memory >= job.disk && serv.state != 4) {
				temp = serv;
				temp.id = 0; // If this isn't zero, server thinks it doesn't exist.
				return temp;
			}
		}
		return null;
	}

	/*
	 * Bubble sort function, based off GeeksForGeeks implementation Takes in an
	 * arrayList of servers which are sorted by the coreCount, which dictate the
	 * serverType and size.
	 */
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

	/*
	 * Worst-fit algorithm implemented by Mark Smith. This algorithm iterates
	 * through the ArrayList and looks for the server that will be have the 'worst'
	 * value for the server, and hence likely have the costliest result. This
	 * implementation uses a tracked fitness value to compare the servers to the
	 * given job, returning the one with the largest gap.
	 */
	public Server worstFit(Job job) {
		// Establish flags and fit variables to track fitness scores and servers.
		int worstFit = Integer.MIN_VALUE;
		int altFit = Integer.MIN_VALUE;
		Server worst = null;
		Server alt = null;
		Boolean worstFound = false;
		Boolean altFound = false;

		for (Server serv : servers) {
			if (serv.coreCount >= job.cpuCores && serv.disk >= job.disk && serv.memory >= job.memory
					&& (serv.state == 0 || serv.state == 2 || serv.state == 3)) {
				int fitnessValue = serv.coreCount - job.cpuCores;
				if (fitnessValue > worstFit && (serv.availableTime == -1 || serv.availableTime == job.submitTime)) {
					worstFit = fitnessValue;
					worstFound = true;
					worst = serv;
				} else if (fitnessValue > altFit && serv.availableTime >= 0) {
					altFit = fitnessValue;
					altFound = true;
					alt = serv;
				}
			}
		}
		// Return the server that was found to be most suitable based on fitness scores
		// and availability.
		if (worstFound) {
			return worst;
		} else if (altFound) {
			return alt;
		}
		int lowest = Integer.MIN_VALUE;
		Server forNow = null;
		for (Server serv : xmlServers) {
			int fit = serv.coreCount - job.cpuCores;
			if (fit > lowest && serv.disk >= job.disk && serv.memory >= job.memory) {
				lowest = fit;
				forNow = serv;
			}
		}
		forNow.id = 0; // If this isn't zero, server thinks it doesn't exist.
		return forNow;
	}

	/*
	 * Custom-algorithm implemented by Bradley Kenny. For stage 3. 
	 * !!! EXPLAINATION WILL GO HERE.
	 * focus on aspects such as: disk, memory...
	 */
	public Server myFit(Job job) {	
		int bestFit = Integer.MAX_VALUE;
		int minAvail = Integer.MAX_VALUE;
		Server best = null;
		Boolean found = false;

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
			// We only want to get here if there is nothing calculated above.
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
			servAlt.id = 0; // If this isn't zero, server thinks it doesn't exist.
			return servAlt;
		}
	}
}