import java.util.ArrayList;

public class Cluster {
	
	private ArrayList<Server> servers = new ArrayList<Server>();
	private Server[] xmlServers;

	Cluster(ArrayList<Server> servers, Server[] xmlServers) {
		this.servers = servers;
		this.xmlServers = xmlServers;
	}

	// 0=inactive, 1=booting, 2=idle, 3=active, 4=unavailable

	// TODO: refactor this to be better / more efficient
	public Server bestFit(Job job) {
		int bestFit = Integer.MAX_VALUE;
		int minAvail = Integer.MAX_VALUE;
		
		Server best = null;
		Server bestDontCare = null;
		
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
			int something = Integer.MAX_VALUE;
			Server theOne = null;
			for (Server serv : xmlServers) {
				int fit = job.cpuCores - serv.coreCount;
				if (fit < something) {
					theOne = serv;
				}
			} 
			theOne.id = 0;
			return theOne;
		} 
	}

	public Server firstFit(Job job)
	{
		ArrayList<Integer> serverTypes = new ArrayList<Integer>();
		Server first = null;
		Server firstActive = null;

		for(Server serv : servers)
		{
			for(int i = 0; i < 8; i++)
			{
				if(serv.coreCount==Math.pow(2, i))
				{
					if (serv.coreCount >= job.cpuCores && serv.disk >= job.disk && serv.memory >= job.memory) 
					{
						first = serv;
						if(first.state == 0 || first.state == 2)
						{
							firstActive = first;
						}
						return first;
					}
				}
			}
			/*
			if(serverTypes.contains(serv.coreCount)==false)
			{
				serverTypes.add(serv.coreCount);
			}
			*/
		}
		return firstActive;
	}
}