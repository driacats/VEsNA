package stage;

import cartago.Artifact;
import java.io.*;
import java.net.*;

public class Client extends Artifact{
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) throws Exception{
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public String sendMessage(String msg) throws Exception {
        out.println(msg);
        String resp = in.readLine();
        return resp;
    }

    public void stopConnection() throws Exception{
        in.close();
        out.close();
        clientSocket.close();
    }
}