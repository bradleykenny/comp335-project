import java.util.ArrayList;
import java.util.*;

public class Cluster {
	
	private ArrayList<Server> servers = new ArrayList<Server>();
	Server[] xmlServers;

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
			Server xmlBest = null;
			for (Server serv : xmlServers) {
				if (serv.coreCount > job.cpuCores && serv.disk > job.disk && serv.memory > job.memory) {
					int fitnessValue = serv.coreCount - job.cpuCores;
					if (fitnessValue < bestFit) {
						xmlBest = serv;
					}
				}
			} return xmlBest;
		} 
	}

	public Server firstFit(Job job) {
		// type | id | state | time | cores | memory | space |
		ArrayList<Integer> serverTypes = new ArrayList<Integer>();

		for(Server serv : servers){
			if(serverTypes.contains(serv.coreCount)==false){
				serverTypes.add(serv.coreCount);
			}
		}
		

		return null;

		/*
		for(Server serv : servers)
		{
			int max = 0;
			for(int i = 0; i < servers.size(); i++)
			{
				if(serv.coreCount > 0)
				{
					max = serv.coreCount;
				}				
			}
			for(int j = 0; j < max; j++)
			{
				
			}
		}
		
		return null;
		*/
	}
}