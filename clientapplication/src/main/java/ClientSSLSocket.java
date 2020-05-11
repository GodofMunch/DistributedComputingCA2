import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.net.*;
import java.io.*;

public class ClientSSLSocket {

    private SocketFactory socketFactory = SSLSocketFactory.getDefault();
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    ClientSSLSocket() throws IOException{
        socket =  socketFactory.createSocket(ConfigParams.serverAddress, ConfigParams.defaultPort);
        setStreams();
    }

    private void setStreams() throws IOException{
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void sendMessage(int protocolCode, String message){
        output.print(protocolCode + " " + message + "\n");
        System.out.println(protocolCode + " " + message + " = Sent from client");
        output.flush();
    }
    public void sendMessage(String message){
        output.print(message + "\n");
        System.out.println(message + " = Sent from client");
        output.flush();
    }

    public String receiveMessage() throws IOException {
        String message = input.readLine( );
        System.out.println(message + " = received by client");
        return message;
    }

    public void close() throws IOException {
        socket.close();
    }
}
