package server;

import java.net.*;
import java.io.*;

public class ServerSocket {

    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    ServerSocket(Socket socket)  throws IOException {
        this.socket = socket;
        setStreams();
    }

    private void setStreams() throws IOException{
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void sendMessage(String message){
        output.print(message + "\n");
        System.out.println(message + " = sent to client");
        output.flush();
    }

    public String receiveMessage() throws IOException {
        String message = input.readLine( );
        System.out.println(message + " = received from client");
        return message;
    }
}
