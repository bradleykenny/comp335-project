	import java.net.*;
import java.util.ArrayList;
import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;

public class Client {
	private Socket socket = null;
	private BufferedReader input = null; // USED GET INFO FROM SOCKET
	private DataOutputStream output = null; // USED TO WRITE TO SOCKET
	private Server[] serverArr = new Server[1];
	private ArrayList<Server> serverArrList = new ArrayList<Server>();
	private int largestServer = 0;
	private String currString;
	private Boolean finished = false;
	private String algorithmType = "ff";
	
	/*
	 * The constructor for the class. Need an address and port to set-up the connection
	 * with the server. Also sets up an input and output datastream so we can send/
	 * receive data from the server.
	 */ 

	public Client(String address, int port) {
		// Establish a connection with the server.
		try {
			socket = new Socket(address, port);
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output = new DataOutputStream(socket.getOutputStream());
		} catch (UnknownHostException u) {
			System.out.println("ERR: " + u);
		} catch (IOException i) {
			System.out.println("ERR: " + i);
		}
	}

	/*
	 * Contained in this method is everything that we would want to happen when
	 * running the protocol. Expectation is that run() will be called when we want
	 * the sequence of interaction to occur with the server. run() requires
	 * interaction with a server model.
	 */
	public void run() {
		// Set-up of the connection.
		send("HELO");
		currString = receive();
		send("AUTH " + System.getProperty("user.name"));
		currString = receive();
		parseXML();
		send("REDY");
		currString = receive();

		if (currString.equals("NONE")) {
			// No jobs, nothing left to do.
			quit();
		} else {
			while (!finished) {
				// finished variable is changed when we receive "NONE".
				if (currString.equals("OK")) {
					send("REDY");
					currString = receive(); // Expected to be job information.
				}
				if (currString.equals("NONE")) {
					// Time to go...
					finished = true; 
					break;
				}

				// Parse job information received here.
				String[] jobString = currString.split("\\s+"); // break the job information up so we can create obj
				Job job = new Job(Integer.parseInt(jobString[1]), Integer.parseInt(jobString[2]), Integer.parseInt(jobString[3]),
						Integer.parseInt(jobString[4]), Integer.parseInt(jobString[5]), Integer.parseInt(jobString[6]));

				send("RESC All"); // Get all server information. 
				currString = receive();
				send("OK");

				currString = receive();
				while (!currString.equals(".")) {
					// We know the server has stopped sending information when we get ".".
					// Therefore, we'll keeping reading information in and adding array until then.

					String[] serverInfo = currString.split("\\s+");

					// Adding Server information to ArrayList for later use. 
					serverArrList.add(
							new Server(serverInfo[0], Integer.parseInt(serverInfo[1]), Integer.parseInt(serverInfo[2]),
									Integer.parseInt(serverInfo[3]), Integer.parseInt(serverInfo[4]),
									Integer.parseInt(serverInfo[5]), Integer.parseInt(serverInfo[6])));
					send("OK");
					currString = receive();
				}

				Cluster ourCluster = new Cluster(serverArrList, serverArr);

				// TODO: Implement all our solutions here.
				Server sendTo = null;
				if (algorithmType.equals("bf")) {
					sendTo = ourCluster.bestFit(job);
					send("SCHD " + job.id + " " + sendTo.type + " " + sendTo.id);
				} else {
					// FROM STAGE 1
					String[] jobData = currString.split("\\s+");
					int count = Integer.parseInt(jobData[2]);
					send("SCHD " + count + " " + serverArr[largestServer].type + " " + "0");	
				}

				// send("SCHD " + job.id + " " + sendTo.type + " " + sendTo.id);
				currString = receive();
			}
		}
		quit();
	}

	// Used to send messages from the server. Takes the message as a parameter.
	public void send(String message) {
		try {
			output.write(message.getBytes());
			// System.out.print("SENT: " + message);
			output.flush();
		} catch (IOException i) {
			System.out.println("ERR: " + i);
		}
	}

	// Used to receive messages from the server.
	public String receive() {
		String message = "";
		try {
			while (!input.ready()) {
			}
			while (input.ready()) {
				message += (char) input.read();
			}
			// System.out.print("RCVD: " + message);
			currString = message;
		} catch (IOException i) {
			System.out.println("ERR: " + i);
		}
		return message;
	}


	// Terminate the connection with the server.
	public void quit() {
		try {
			send("QUIT");
			currString = receive();
			if (currString.equals("QUIT")) {
				input.close();
				output.close();
				socket.close();
			}
		} catch (IOException i) {
			System.out.println("ERR: " + i);
		}
	}

	/*
	 * Used to parse information from the XML used in association with the
	 * server. We need to be able to break up the information and put it 
	 * into a Server object so that we can use it. 
	 */
	public void parseXML() {
		try {
			File systemXML = new File("../ds-sim/28Apr2019/system.xml");

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(systemXML);

			doc.getDocumentElement().normalize();
			NodeList servers = doc.getElementsByTagName("server");
			serverArr = new Server[servers.getLength()];
			for (int i = 0; i < servers.getLength(); i++) {
				Element server = (Element) servers.item(i);
				String t = server.getAttribute("type");
				int l = Integer.parseInt(server.getAttribute("limit"));
				int b = Integer.parseInt(server.getAttribute("bootupTime"));
				float r = Float.parseFloat(server.getAttribute("rate"));
				int c = Integer.parseInt(server.getAttribute("coreCount"));
				int m = Integer.parseInt(server.getAttribute("memory"));
				int d = Integer.parseInt(server.getAttribute("disk"));
				Server temp = new Server(i, t, l, b, r, c, m, d);
				serverArrList.add(temp);
				// System.out.println(serverArr[i].coreCount);
			}
			largestServer = setLargestServer();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/* 
	 * Using the information from the XML file, we want to determine which Server
	 * object is the largest and return that.
	 */
	public int setLargestServer() {
		int largestServer = serverArr[0].id;
		for (int i = 0; i < serverArr.length; i++) {
			if (serverArr[i].coreCount > serverArr[largestServer].coreCount) {
				largestServer = serverArr[i].id;
			}
		}
		return largestServer;
	}

	// This is our method that will be called when running the program from terminal. 
	public static void main(String args[]) {
		Client ourClient = new Client("127.0.0.1", 8096);

		// Check for "-a" cmd argument and set algorithm type accordingly.
		if (args[0] != null) {
			if (args[0].equals("-a")) {
				if (args[1].equals("bf")) {
					ourClient.algorithmType = "bf";
				} else if (args[1].equals("wf")) {
					ourClient.algorithmType = "wf";
				} else if (args[1].equals("ff")) {
					ourClient.algorithmType = "ff";
				}
			}
		}

		ourClient.run();
	}
}