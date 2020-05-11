import com.dave.server.LoginServiceGrpc;
import com.dave.server.LoginServiceOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.io.*;

public class ClientHelper {


    private ClientSSLSocket mySocket;

    ClientHelper() throws IOException {
        this.mySocket = new ClientSSLSocket();
        System.out.println("Connection request made");
    }

    public void done() throws IOException {
        mySocket.sendMessage(ConfigParams.endMessage);
        mySocket.close();
    }

    public String login(BufferedReader br) {
        String reply = "";
        try {

            System.out.println("Please enter your Username...");
            String username = br.readLine();
            System.out.println("Please enter your Password...");
            String password = br.readLine();

             ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 1111)
                    .usePlaintext(true)
                    .build();

            LoginServiceGrpc.LoginServiceBlockingStub stub = LoginServiceGrpc.newBlockingStub(channel);

            LoginServiceOuterClass.LoginRequest request = LoginServiceOuterClass.LoginRequest
                    .newBuilder()
                    .setUsername(username)
                    .setPassword(password)
                    .build();
            LoginServiceOuterClass.LoginResponse response = stub.login(request);

            reply = response.getAnswer();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return reply;
    }
    //            mySocket.sendMessage(DAVE.SERVER_ALIVE.getValue() , "");
//            reply = mySocket.receiveMessage();
//            if (Integer.parseInt(reply) == DAVE.SERVER_ALIVE.getValue()) {
//                mySocket.sendMessage(DAVE.LOGIN_ATTEMPT.getValue(), username + ";" + password);
//                reply = mySocket.receiveMessage() + ";" + username;
//            } else
//                reply = String.valueOf(DAVE.SERVER_DEAD.getValue());
    public String register(BufferedReader br) {
        String reply = "";
        try {
            System.out.println("Please enter your Username...");
            String username = br.readLine();
            System.out.println("Please enter your Password...");
            String password = br.readLine();
            mySocket.sendMessage(DAVE.SERVER_ALIVE.getValue(), "");
            reply = mySocket.receiveMessage().trim();
            if (Integer.parseInt(reply) == DAVE.SERVER_ALIVE.getValue()) {
                mySocket.sendMessage(DAVE.REGISTER_ATTEMPT.getValue(), username + ";" + password);
                reply = mySocket.receiveMessage() + ";" + username;
            } else
                reply = String.valueOf(DAVE.SERVER_DEAD.getValue());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return reply;
    }

    public String logoff() {
        String reply = "";
        try {
            mySocket.sendMessage(DAVE.SERVER_ALIVE.getValue(), "");
            reply = mySocket.receiveMessage().trim();
            if (Integer.parseInt(reply) == DAVE.SERVER_ALIVE.getValue()) {
                mySocket.sendMessage(DAVE.REQUEST_LOGOFF.getValue(), "");
                reply = mySocket.receiveMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reply;
    }

    public String sendMessage(String message){
        String reply = "";
        try {
            mySocket.sendMessage(DAVE.SERVER_ALIVE.getValue(), "");
            reply = mySocket.receiveMessage().trim();
            if (Integer.parseInt(reply) == DAVE.SERVER_ALIVE.getValue()) {
                mySocket.sendMessage(DAVE.SEND_MESSAGE.getValue(), message);
                reply = mySocket.receiveMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reply;
    }

    public String downloadHistory() {
        String reply = "";
        try{
            mySocket.sendMessage(DAVE.SERVER_ALIVE.getValue(), "");
            reply = mySocket.receiveMessage();
            if(Integer.parseInt(reply) == DAVE.SERVER_ALIVE.getValue()) {
                mySocket.sendMessage(DAVE.REQUEST_MESSAGE_HISTORY.getValue(), "");
                reply = mySocket.receiveMessage().trim();
                int protocolCode = Integer.parseInt(reply.substring(0, 3).trim());
                if (protocolCode == DAVE.SEND_MESSAGE_HISTORY_SUCCESS.getValue())
                    return reply;
                else if (protocolCode == DAVE.SEND_MESSAGE_HISTORY_FAILED.getValue())
                    reply = String.valueOf(protocolCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            reply = String.valueOf(DAVE.SERVER_DEAD);
        }
        return reply;
    }
}
