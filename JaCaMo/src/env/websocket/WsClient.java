package websocket;

// The Client class provides three methods that allow the user to create a websocket
// connection with a server, send and receive messages.

import cartago.Artifact;
import java.io.*;
import java.net.*;

public class WsClient extends Artifact{
    private Socket clientSocket;    // Client Socket used
    private PrintWriter out;        // Output channel
    private BufferedReader in;      // Input channel

    public void startConnection(String ip, int port) throws Exception{
        // try{
            clientSocket = new Socket(ip, port); // connect to the given ip on the given port
            // clientSocket.setKeepAlive(true);
            out = new PrintWriter(clientSocket.getOutputStream(), true); // open the output channel
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // open the input channel
        // }catch(Exception e){
        //     System.out.println(e);
        // }
    }

    public String sendMessage(String msg) throws Exception {
        out.println(msg); // use the output channel to send the message
        System.out.println("Sending message");
        // String resp = in.readLine(); // read the message from server
        System.out.println("Answer message");
        // return resp;
        return "Sent";
    }

    public void stopConnection() throws Exception{
        in.close(); // close input channel
        out.close(); // close output channel
        clientSocket.close(); // close client connection
    }
}
