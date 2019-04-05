import java.net.*; 
import java.io.*; 

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;


public class Client { 
	private Socket socket = null; 
	private BufferedReader input = null; 	// USED GET INFO FROM SOCKET
	private DataOutputStream output = null; // USED TO WRITE TO SOCKET
	private Server[] serverArr = new Server[1];
    private int largestServer = 0;
	private String currString;
	private Boolean finished = false;

	public Client(String address, int port) { 
		// ESTABLISH CONNECTION 
		try { 
			socket = new Socket(address, port); 
			System.out.println("Connected."); 
  
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output = new DataOutputStream(socket.getOutputStream()); 
		} 
		catch(UnknownHostException u) { 
			System.out.println("ERR: " + u); 
		} 
		catch(IOException i) { 
			System.out.println("ERR: " + i); 
		} 
	
		// UNCOMMENT FOR DEBUGGING ONLY
		// debug(output);
	} 

	public void run() {
		// CONNECTION SET-UP
		send("HELO");
		currString = receive();
		send("AUTH BJM");
		currString = receive();
		parseXML();
		send("REDY");
		currString = receive();
		
		if (currString.equals("NONE")) {
			quit();
        }
        else {
			while (!finished) {
				if(currString.equals("OK")){
                    send("REDY");
                    currString = receive();
                } 
                if(currString.equals("NONE")){
                    finished = true;
                    break;
                }
				String[] jobData = currString.split("\\s+");
			    int count = Integer.parseInt(jobData[2]);
			    send("SCHD "+count+ " " +serverArr[largestServer].type+" "+ largestServer);
			    currString = receive();
			}
		}
		quit();
	}
	
	// SENDING MESSAGES TO THE SERVER
	public void send(String message) {
		try {
			//message += "\n";
			output.write(message.getBytes());
			System.out.print("SENT: " + message);
			output.flush();
		} 
		catch (IOException i) {
			System.out.println("ERR: " + i);
		}
	}

	// RECEIVING MESSAGES FROM THE SOCKET
	public String receive() {
		String message = "";
		try {
			while (!input.ready()) {} // MAKE THIS BETTER
			while (input.ready()) {
                message += (char) input.read();
            } 
            System.out.print("RCVD: " + message);
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
		} 
		catch(IOException i) { 
			System.out.println("ERR: " + i); 
		} 
	}
	
	// DEBUGGING VIA MANUAL INPUT/OUTPUT
	// NOTE: DOESNT WORK RN
	public void debug(DataOutputStream output) {
		BufferedInputStream man_input = new BufferedInputStream(System.in); 
		String line = ""; 
		while (!line.equals("QUIT")) { 
			try { 
				// line = man_input.read();
				output.write(line.getBytes());
			} 
			catch(IOException i) { 
				System.out.println("IOE: " + i); 
			} 
		} 
    }
    
    public void parseXML() {
        try {
            File systemXML = new File("../ds-sim_v3/system.xml");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(systemXML);

            doc.getDocumentElement().normalize();
            NodeList servers = doc.getElementsByTagName("server");
            serverArr = new Server[servers.getLength()];
            for(int i = 0; i < servers.getLength(); i++) {
                Element server = (Element) servers.item(i);
                String t = server.getAttribute("type");
                int l = Integer.parseInt(server.getAttribute("limit"));
                int b = Integer.parseInt(server.getAttribute("bootupTime"));
                float r = Float.parseFloat(server.getAttribute("rate"));
                int c = Integer.parseInt(server.getAttribute("coreCount"));
                int m = Integer.parseInt(server.getAttribute("memory"));
                int d = Integer.parseInt(server.getAttribute("disk"));
                Server temp = new Server(i,t, l, b, r, c, m, d);
                serverArr[i] = temp;
                //System.out.println(serverArr[i].coreCount);
            }
            largestServer = setLargestServer();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }

    }
    public int setLargestServer(){
		int largestServer = serverArr[0].id;
		for(int i = 0; i<serverArr.length; i++){
			if(serverArr[i].coreCount>serverArr[largestServer].coreCount){
				largestServer = serverArr[i].id;
			}
		}
		return largestServer;
	}
	
    public class Server {
        public int id;
        public String type;
        public int limit;
        public int bootupTime;
        public float rate;
        public int coreCount;
        public int memory;
        public int disk;

        Server(int id, String t, int l, int b, float r, int c, int m, int d) {
            this.id = id;
            this.type = t;
            this.limit = l;
            this.bootupTime = b;
            this.rate = r;
            this.coreCount = c;
            this.memory = m;
            this.disk = d;
        } 
	}
	
	// THIS IS WHAT RUNS
	public static void main(String args[]) { 
		Client ourClient = new Client("127.0.0.1", 8096);
		ourClient.run();
	}
}


