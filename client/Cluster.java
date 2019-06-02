import java.util.ArrayList;
import java.util.HashMap;

public class Cluster {

	private ArrayList<Server> servers = new ArrayList<Server>();
	private Server[] xmlServers;
	private HashMap<String, Integer> estRunTime;

	Cluster(ArrayList<Server> servers, Server[] xmlServers) {
		this.servers = servers;
		this.xmlServers = xmlServers;
	}

	Cluster() {
		this.servers = null;
		this.xmlServers = null;
		this.estRunTime = new HashMap<String, Integer>();
	}

	public void updateArrList(ArrayList<Server> serverArrList) {
		this.servers = serverArrList;
		
		for(int i = 0; i < xmlServers.length; i++) {
			xmlServers[i].numAvailable = 0;
		}
		for (Server serv : servers) {
			for (int i = 0; i < xmlServers.length; i++) {
				if (serv.type.equals(xmlServers[i].type)) {
					xmlServers[i].numAvailable += 1;
				}
			}
		}
	}

	public void updateArr(Server[] serverArr) {
		this.xmlServers = serverArr;
	}

	// STATES: 0 = inactive, 1 = booting, 2 = idle, 3 = active, 4 = unavailable

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
			if ((serv.hasEnoughCores(job) && serv.hasEnoughDisk(job) && serv.hasEnoughMemory(job))) {
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
					if (serv2.hasEnoughCores(job) && serv2.hasEnoughDisk(job) && serv2.hasEnoughMemory(job)
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
			if (serv.hasEnoughCores(job) && serv.hasEnoughDisk(job) && serv.hasEnoughMemory(job) && serv.state != 4) {
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
			if (serv.hasEnoughCores(job) && serv.hasEnoughDisk(job) && serv.hasEnoughMemory(job)
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
			if (fit > lowest && serv.hasEnoughDisk(job) && serv.hasEnoughMemory(job)) {
				lowest = fit;
				forNow = serv;
			}
		}
		forNow.id = 0; // If this isn't zero, server thinks it doesn't exist.
		return forNow;
	}

	/*
	 * Custom-algorithm implemented by Bradley Kenny. For stage 3. !!! EXPLAINATION
	 * WILL GO HERE. focus on aspects such as: disk, memory...
	 */
	public Server myFit(Job job) {

		// Find the optimal fit across servers, regardless of current capacity.
		int bestFit = Integer.MAX_VALUE;
		for (Server serv : xmlServers) {
			int nowFit = serv.coreCount - job.cpuCores;
			if (serv.canRunJob(job) && nowFit <= bestFit) {
				bestFit = nowFit;
			}
		}

		sortByCores(servers, 0, servers.size() - 1); // Sort to optimise run time. 
		
		// Try to find a server that meets the optimal fit and is currently ready. 
		int minAvailableTime = Integer.MAX_VALUE;
		Server bestFitServ = null;
		for (Server serv : servers) {
			int currFit = serv.coreCount - job.cpuCores;
			if (serv.canRunJob(job) && bestFit >= currFit && serv.availableTime < minAvailableTime) {
				minAvailableTime = serv.availableTime;
				bestFitServ = serv;
			}
		}

		// Store values for next run and return best for this job.
		if (bestFitServ != null) {
			String tempID = bestFitServ.type + "," + Integer.toString(bestFitServ.id);
			if (estRunTime.containsKey(tempID)) {
				estRunTime.replace(tempID, bestFitServ.availableTime);
			} else {
				estRunTime.put(tempID, bestFitServ.availableTime);
			}
			return bestFitServ;
		}

		// If we got this far, no available servers that have optimal fit. 

		int bestFitIdle = Integer.MAX_VALUE;
		Server bestFitServIdle = null;
		for (Server serv : servers) {
			if (serv.state == 3 && serv.canRunJob(job)) {
				int possibleFit = job.cpuCores - serv.coreCount;
				if (bestFitIdle > possibleFit) {
					bestFitIdle = possibleFit;
					bestFitServIdle = serv;
				}
			}
		}

		if (bestFitServIdle != null) {
			return bestFitServIdle;
		}

		int bestEst = Integer.MAX_VALUE;
		Server bestGuess = null;

		for (Server serv : xmlServers) {
			for (int i = 0; i < serv.numAvailable; i++) {
				int currFit = serv.coreCount - job.cpuCores;
				String tempID = serv.type + "," + Integer.toString(i);
				if (serv.canRunJob(job) && bestFit >= currFit) {
					int currEst = estRunTime.get(tempID);
					if (currEst < bestEst) {
						bestEst = currEst;
						bestGuess = serv;
						bestGuess.id = i;
					}
				}
			}
		}

		String tempID = bestGuess.type + "," + Integer.toString(bestGuess.id);
		estRunTime.replace(tempID, estRunTime.get(tempID) + job.estRuntime);
		return bestGuess;
	}

	/*
	 * QuickSort Algorithm based on GeeksForGeeks' solution. > arr --> ArrayList to
	 * be sorted > low --> starting index > high --> ending index
	 */
	void sortByCores(ArrayList<Server> arr, int low, int high) {
		if (low < high) {
			int pi = partition(arr, low, high);

			sortByCores(arr, low, pi - 1);
			sortByCores(arr, pi + 1, high);
		}
	}

	/*
	 * Helper function for the QuickSort implementation. takes last element as
	 * pivot, places the pivot element at its correct position in sorted ArrayList,
	 * and places all smaller (smaller than pivot) to left of pivot and all greater
	 * elements to right of pivot.
	 */
	int partition(ArrayList<Server> arr, int low, int high) {
		Server pivot = arr.get(high);
		int i = (low - 1);
		for (int j = low; j < high; j++) {
			if (arr.get(j).coreCount <= pivot.coreCount) {
				i++;

				Server temp = arr.get(i);
				arr.set(i, arr.get(j));
				arr.set(j, temp);
			}
		}

		Server temp = arr.get(i + 1);
		arr.set(i + 1, arr.get(high));
		arr.set(high, temp);

		return i + 1;
	}
}