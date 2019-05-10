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
				if (fitnessValueAlt >= 0 && fitnessValueAlt < bestFitAlt && serv.disk > job.disk && serv.memory > job.memory) {
					bestFitAlt = fitnessValueAlt;
					servAlt = serv;
				}
			} 
			servAlt.id = 0;
			return servAlt;
		} 
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
		for(Server serv : servers){
			if(serv.coreCount >= job.cpuCores && serv.disk >= job.disk && serv.memory >= job.memory){
				int fitnessValue = serv.coreCount - job.cpuCores;
				if (fitnessValue > worstFit){
					if((serv.state==0 || serv.state == 2) && serv.availableTime==-1){
						worstFit = fitnessValue;
						worstFound = true;
						worst = serv;
						minAvail=serv.availableTime;
					}
				}
				else if(fitnessValue>altFit){
					if(serv.state==0 || serv.state == 2){
						altFit = fitnessValue;
						altFound = true;
						alt = serv;
						minAvail=serv.availableTime;
					}
				}
				else{
					worstIgnore = serv;
				}
			}
		}
		if(worstFound == true){
			System.out.println("worst ran");
			return worst;
		}
		else if(altFound == true){
			System.out.println("alt ran");
			return alt;
		}
		System.out.println("butts ran");
		return worstIgnore;
	}
}