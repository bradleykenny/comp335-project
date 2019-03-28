
// A Java program for a Client 
import java.net.*; 
import java.io.*; 
  
public class gfgclient { 
    // initialize socket and input output streams 
    private Socket socket = null; 
    private DataInputStream input = null; 
    private DataOutputStream out = null; 
  
    // constructor to put ip address and port 
    public gfgclient(String address, int port) { 
        // establish a connection 
        try { 
            socket = new Socket(address, port); 
            System.out.println("Connected"); 
  
            // takes input from terminal 
            input = new DataInputStream(System.in); 
  
            // sends output to the socket 
            out = new DataOutputStream(socket.getOutputStream()); 
        } 
        catch(UnknownHostException u) { 
            System.out.println(u); 
        } 
        catch(IOException i) { 
            System.out.println("IOE: " + i); 
        } 
  
        // string to read message from input 
        byte[] line = new byte[4]; 
  
        // keep reading until "Over" is input 
        while (!line.equals("OVER")) 
        { 
            System.out.println("HERE");
            try { 
                String temp = input.readLine();
                // line = temp.getBytes("UTF-8");
                // for(byte b : line) {
                out.writeBytes(temp);
                // }
            } 
            catch(IOException i) { 
                System.out.println("IOE: " + i); 
            } 
        } 
  
        // close the connection 
        try { 
            input.close(); 
            out.close(); 
            socket.close(); 
        } 
        catch(IOException i) { 
            System.out.println(i); 
        } 
    } 
  
    public static void main(String args[]) { 
        gfgclient client = new gfgclient("127.0.0.1", 8096); 
    } 
} 
