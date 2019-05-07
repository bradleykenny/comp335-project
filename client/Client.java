import java.net.*;
import java.util.ArrayList;
import java.util.Random;
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

	public Client(String address, int port) {
		// ESTABLISH CONNECTION
		try {
			socket = new Socket(address, port);
			// System.out.println("Connected.");

			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output = new DataOutputStream(socket.getOutputStream());
		} catch (UnknownHostException u) {
			System.out.println("ERR: " + u);
		} catch (IOException i) {
			System.out.println("ERR: " + i);
		}
	}

	public void run() {
		// CONNECTION SET-UP
		send("HELO");
		currString = receive();
		send("AUTH " + System.getProperty("user.name"));
		currString = receive();
		parseXML();
		send("REDY");
		currString = receive();

		if (currString.equals("NONE")) {
			// if there's no jobs, we just want to quit
			quit();
		} else {
			while (!finished) {
				// finished variable is changed when we receive "NONE"
				if (currString == "OK") {
					send("REDY");
					currString = receive(); // this will be the job information
				}
				if (currString == "NONE") {
					finished = true; // time to go...
					break;
				}

				// need to parse the job here
				String[] jobString = currString.split("\\s+"); // break the job information up so we can create obj
				Server job = new Server(0, jobString[0], Integer.parseInt(jobString[1]), Integer.parseInt(jobString[2]),
						Float.parseFloat(jobString[3]), Integer.parseInt(jobString[4]), Integer.parseInt(jobString[5]),
						Integer.parseInt(jobString[6]));

				send("RESC All");
				currString = receive();
				send("OK");

				currString = receive();
				while (!currString.equals(".")) {
					// we know the server has stopped sending information when we get "."
					// therefore, we'll keeping reading information in and adding array til then

					String[] serverInfo = currString.split("\\s+");

					Random rand = new Random();
					int randID = rand.nextInt(10000); // server ID is a random value for now

					// add server information from string to serverArrList so algorithm performs on
					// all the info
					serverArrList.add(new Server(serverInfo[0], Integer.parseInt(serverInfo[1]),
							Integer.parseInt(serverInfo[2]), Integer.parseInt(serverInfo[3]),
							Integer.parseInt(serverInfo[4]), Integer.parseInt(serverInfo[5]),
							Integer.parseInt(serverInfo[6])));
					send("OK");
					currString = receive();
				}

				Cluster ourCluster = new Cluster(serverArrList, serverArr);
				// !!! IMPLEMENT OUR ALGORITHM HERE
				// send("SCHD" + sendingServer);

				// TODO: fix so this works with allToLargest
				Server sendTo = null;
				if (algorithmType.equals("bf")) {
					sendTo = ourCluster.bestFit(job);
				}

				/*
				 * FROM STAGE 1 String[] jobData = currString.split("\\s+"); int count =
				 * Integer.parseInt(jobData[2]); send("SCHD " + count + " " +
				 * serverArr[largestServer].type + " " + "0");
				 */

				send("SCHD " + job.id + " " + sendTo.type + " " + sendTo.id);
				currString = receive();
			}
		}
		quit();
	}

	// SENDING MESSAGES TO THE SERVER
	public void send(String message) {
		try {
			output.write(message.getBytes());
			// System.out.print("SENT: " + message);
			output.flush();
		} catch (IOException i) {
			System.out.println("ERR: " + i);
		}
	}

	// RECEIVING MESSAGES FROM THE SOCKET
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

	// TERMINATES CONNECTIONS AND SENDS/RECEIVES QUIT MESSAGES
	public void quit() {
		try {
			send("QUIT");
			currString = receive();
			if (currString == "QUIT") {
				input.close();
				output.close();
				socket.close();
			}
		} catch (IOException i) {
			System.out.println("ERR: " + i);
		}
	}

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
				serverArr[i] = temp;
				serverArrList.add(temp);
				// System.out.println(serverArr[i].coreCount);
			}
			largestServer = setLargestServer();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public int setLargestServer() {
		int largestServer = serverArr[0].id;
		for (int i = 0; i < serverArr.length; i++) {
			if (serverArr[i].coreCount > serverArr[largestServer].coreCount) {
				largestServer = serverArr[i].id;
			}
		}
		return largestServer;
	}

	// THIS IS WHAT RUNS
	public static void main(String args[]) {
		Client ourClient = new Client("127.0.0.1", 8096);
		
		// check for "-a" cmd argument and set algorithm type accordingly
		if (args[0].equals("-a")) {
			if (args[1].equals("bf")) {
				ourClient.algorithmType = "bf";
			} else if (args[1].equals("wf")) {
				ourClient.algorithmType = "wf";
			} else if (args[1].equals("ff")) {
				ourClient.algorithmType = "ff";
			}
		}
		ourClient.run();
	}
}