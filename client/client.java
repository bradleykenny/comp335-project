import java.net.*; 
import java.io.*; 
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;


public class client { 
	private Socket socket = null; 
	private BufferedReader input = null; // USED GET INFO FROM SOCKET
    private DataOutputStream output = null; // USED TO WRITE TO SOCKET
    private NodeList servers = null;
	
	public client(String address, int port) { 
		// CREATE CONNECTION 
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
        parseXML(servers);
		send(output, "HELO");
		receive(input);
		send(output, "AUTH BJM");
		receive(input);
		
		// UNCOMMENT FOR DEBUGGING ONLY
		// debug(output);
  
		// CLOSE CONNECTION
		try { 
			input.close(); 
			output.close(); 
			socket.close(); 
		} 
		catch(IOException i) { 
			System.out.println("ERR: " + i); 
		} 
	} 
	
	// SENDING MESSAGES TO THE SERVER
	public void send(DataOutputStream destination, String message) {
		try {
			message += "\n";
			destination.write(message.getBytes());
			System.out.print("SENT: " + message);
			destination.flush();
		} 
		catch (IOException i) {
			System.out.println("ERR: " + i);
		}
	}

	// RECEIVING MESSAGES FROM THE SOCKET
	public void receive(BufferedReader input) {
		String message = "";
		try {
			while (input.ready()) {
				message += (char) input.read();
			} System.out.print("RCVD: " + message);
		} catch (IOException i) {
			System.out.println("ERR: " + i);
		} 
		// return message;
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
    
    public void parseXML(NodeList servers){
        try{
            File systemXML = new File("../ds-sim_v3/system.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(systemXML);
            doc.getDocumentElement().normalize();
            servers = doc.getElementsByTagName("servers");
            System.out.println(servers.getLength());
            String[][] serversArray = new String[2][6];
            for(int i = 0; i<servers.getLength(); i++){
                System.out.println(i);
                Element server =(Element) servers.item(i);
                System.out.println("Type:"+(server.getAttribute("type"));
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

    }
    public class Server{
        public String type;
        public int limit;
        public int bootupTime;
        public float rate;
        public int coreCount;
        public int memory;
        public int disk;

        Server(String t, int l, int b, float r, int c, int m, int d){
            this.type = t;
            this.limit = l;
            this.bootupTime=b;
            this.rate = r;
            this.coreCount =c;
            this.memory =m;
            this.disk =d;
        }
        
    }
	// THIS IS WHAT RUNS
	public static void main(String args[]) { 
		client ourClient = new client("127.0.0.1", 8096);
	}
}


