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

		for (Server serv : servers) {
			if (serv.coreCount >= job.cpuCores && serv.disk >= job.disk && serv.memory >= job.memory) {
				int fitnessValue = serv.coreCount - job.cpuCores;
				if ((fitnessValue < bestFit) || (fitnessValue == bestFit && serv.availableTime < minAvail)) {
					// this returns the wrong one when there is a server larger than this
					if (serv.state == 0 || serv.state == 2) {
						bestFit = fitnessValue;
						minAvail = serv.availableTime;
						found = true;
						best = serv;
					} else {
						System.out.println("TYPE: " + serv.type);
						bestDontCare = serv;
					}
				}
			}
		}
		if (found) {
			return best;
		} else {
			return bestDontCare;
		} 

		// // go through xml file and find the server that fits best prior to other load
		// System.out.println(xmlServers);
		// Server xmlBest = null;
		// for (Server serv : xmlServers) {
		// 	if (serv.coreCount > job.cpuCores && serv.disk > job.disk && serv.memory > job.memory) {
		// 		int fitnessValue = serv.coreCount - job.cpuCores;
		// 		if (fitnessValue < bestFit) {
		// 			bestFit = fitnessValue;
		// 			xmlBest = serv;
		// 			System.out.println("GOT EM");
		// 		}
		// 	}
		// } return xmlBest;
	}

	public Server firstFit(Job job)
	{
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
		}
		return firstActive;
	}

	public Server worstFit(Job job){
		int worstFit = Integer.MIN_VALUE;
		int altFit = Integer.MIN_VALUE;
		Server worst = null;
		Server alt = null;
		Boolean worstFound = false;
		Boolean altFound = false;
		Server worstIgnore = null;
		int minAvail = Integer.MIN_VALUE;
		ArrayList<Integer> coreCounts = new ArrayList<Integer>();
		for (Server serv : xmlServers){
			if(coreCounts.contains(serv.coreCount)==false){
				coreCounts.add(serv.coreCount);
			}
		}
		for(Integer i : coreCounts){
			for(Server serv : xmlServers){
				if(serv.coreCount==i && serv.coreCount > job.cpuCores && serv.disk > job.disk && serv.memory > job.memory){
					int fitnessValue = serv.coreCount-job.cpuCores;
					if(fitnessValue>worstFit && (serv.state==0 || serv.state == 2)){
						worstFit = fitnessValue;
						worstFound = true;
						worst = serv;
						minAvail=serv.availableTime;
					}
					else if(fitnessValue>altFit && job.estRuntime<serv.availableTime){
						altFit = fitnessValue;
						altFound = true;
						alt = serv;
					}
					else{
						worstIgnore = serv;
					}
				}
			}
		}
		if(worstFound==true){
			return worst;
		}
		else if(altFound == true){
			return alt;
		}
		else{
			return worstIgnore;
		}
	}
}