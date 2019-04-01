import java.net.*; 
import java.io.*; 
import java.util.*;
  
public class client { 
    private Socket socket = null; 
    private DataInputStream input = null; 
    private DataOutputStream output = null; 
  
    public client(String address, int port) { 
        // CREATE CONNECTION 
        try { 
            socket = new Socket(address, port); 
            System.out.println("Connected."); 
  
            input = new DataInputStream(System.in); 
            output = new DataOutputStream(socket.getOutputStream()); 
        } 
        catch(UnknownHostException u) { 
            System.out.println("ERR: " + u); 
        } 
        catch(IOException i) { 
            System.out.println("ERR: " + i); 
        } 
  
        // DEBUGGING ONLY
        debug();
  
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
            destination.write(message.getBytes());
            System.out.println("SENT: " + message);
        } catch (IOException i) {
            System.out.println("ERR: " + i);
        }
	}

	// TODO: method to receive information from the server
	public void receive() {
		//
    }
    
    // DEBUGGING VIA MANUAL INPUT/OUTPUT
    public void debug() {
        String line = ""; 
        while (!line.equals("QUIT")) { 
            try { 
                line = input.readLine();
                output.write(line.getBytes());
            } 
            catch(IOException i) { 
                System.out.println("IOE: " + i); 
            } 
        } 
    }
	
	// main method that runs
    public static void main(String args[]) { 
        client ourClient = new client("127.0.0.1", 8096); 
    } 
} 
