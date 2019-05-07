public class Cluster {
	
	private Server[] servers;

	Cluster(Server[] servers) {
		this.servers = servers;
	}

	public void bestFit() {
		
	}

	public void firstFit() {
		//
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