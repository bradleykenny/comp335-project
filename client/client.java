import java.net.*; 
import java.io.*; 
import java.util.*;
  
public class client { 
	private Socket socket = null; 
	private BufferedReader input = null; // USED GET INFO FROM SOCKET
	private DataOutputStream output = null; // USED TO WRITE TO SOCKET
	
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

		send(output, "HELO");
		String received = receive(input);
		send(output, "AUTH BJM");
		
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
	public String receive(BufferedReader input) {
		String message = "";
		try {
			while (input.ready()) {
				message += (char) input.read();
			} System.out.print("RCVD: " + message);
			return message;
		} catch (IOException i) {
			System.out.println("ERR: " + i);
		} 
		// dont get here
		System.out.println("REC DONE");
		return message;
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
	
	// THIS IS WHAT RUNS
	public static void main(String args[]) { 
		client ourClient = new client("127.0.0.1", 8096);
	}
}
