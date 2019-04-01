import java.net.*; 
import java.io.*; 
import java.util.*;
  
public class client { 
    private Socket socket = null; 
    private DataInputStream input = null; 
    private DataOutputStream output = null; 
  
    public client(String address, int port) { 
        // establish a connection 
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
  
        // close the connection 
        try { 
            input.close(); 
            output.close(); 
            socket.close(); 
        } 
        catch(IOException i) { 
            System.out.println("ERR: " + i); 
        } 
	} 
	
	// TODO: method to send information to the server
	public void send() {
		//
	}

	// TODO: method to receive information from the server
	public void receive() {
		//
	}
	
	// main method that runs
    public static void main(String args[]) { 
        client ourClient = new client("127.0.0.1", 8096); 
    } 
} 
