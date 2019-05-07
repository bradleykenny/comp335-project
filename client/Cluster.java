public class Cluster {
	
	private Server[] servers;

	Cluster(Server[] servers) {
		this.servers = servers;
	}

	/*
	First-Fit
	For a given job ji,
	1. Obtain server state information
	2. For each server type i, si , from the smallest to the largest
	3. For each server j, si,j of server type si, from 0 to limit - 1 // j is server ID
	4. If server si,j has sufficient available resources to run job ji then
	5. Return si,j
	6. End If
	7. End For
	8. End For
	9. Return the first Active server with sufficient initial resource capacity to run job j
	*/
}