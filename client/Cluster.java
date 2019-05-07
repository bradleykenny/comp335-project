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

	public void firstFit() {
		// A job gets read; read server state info: 
		// type | id | state | time | cores | memory | space
		// FOR each server type, from smallest to largest
		// 		FOR each server
		// 			IF server has enough cores to run the job
		// 				RETURN server
		// 			END IF
		// 		END FOR
		// END FOR
		// RETURN first "Active" server with enough cores (resource capacity) ro run the job
	}
}