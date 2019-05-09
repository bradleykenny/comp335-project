import java.util.ArrayList;

public class Cluster {
	
	private ArrayList<Server> servers = new ArrayList<Server>();
	private Server[] xmlServers;

	Cluster(ArrayList<Server> servers, Server[] xmlServers) {
		this.servers = servers;
		this.xmlServers = xmlServers;
	}

	// TODO: refactor this to be better / more efficient
	public Server bestFit(Job job) {
		int bestFit = Integer.MAX_VALUE;
		int minAvail = Integer.MAX_VALUE;
		Server best = null;
		Boolean found = false;

		for (Server serv : servers) {
			if (serv.coreCount > job.cpuCores && serv.disk > job.disk && serv.memory > job.memory) {
				int fitnessValue = serv.coreCount - job.cpuCores;
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
		} else {
			// go through xml file and find the server that fits best prior to other load
			System.out.println("DIDNT QUITE MAKE IT");
			Server xmlBest = null;
			for (Server serv : xmlServers) {
				if (serv.coreCount > job.cpuCores && serv.disk > job.disk && serv.memory > job.memory) {
					int fitnessValue = serv.coreCount - job.cpuCores;
					if (fitnessValue < bestFit) {
						bestFit = fitnessValue;
						xmlBest = serv;
						System.out.println("GOT EM");
					}
				}
			} return xmlBest;
		} 
	}

	public void firstFit(Job job) {
		// A job gets read; read server state info: 
		// type | id | state | time | cores | memory | space
		// FOR each server type, from smallest to largest
		for (Server serv : servers) { }
		/* Server types, types my be jumbled, i.e. sort from smallest to largest*/	
		// FOR each server types (tiny, small, medium, large, xlarge?)
			
		// 		FOR each server
		// 			IF server has enough cores to run the job
		// 				RETURN server
		// 			END IF
		// 		END FOR
		// END FOR
		// RETURN first "Active" server with enough cores (resource capacity) ro run the job
	}
}