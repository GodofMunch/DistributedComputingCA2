package server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

public class ServerDriver {
    private static SSLServerSocket sslServerSocket;
    public static void main(String[] args) {
        ServerSocket myDataSocket;
        try {
            initialiseProgram(args);
            Server server = ServerBuilder.forPort(1111)
                    .addService(new LoginServiceImpl()).build();
            System.out.println("ServerDriver ready to accept connections.");
            while (true) {
                System.out.println("Waiting for a connection.");
                server.start();
                System.out.println("Server started on port: " + 1111);
                server.awaitTermination();

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //                myDataSocket = new ServerSocket(sslServerSocket.accept());
//                System.out.println("connection accepted");
//                Thread theThread = new Thread(new ServerThread(myDataSocket));
//                theThread.start();
    private static void initialiseProgram(String[] args) throws IOException {

        String configFileLocation = "";
        if(args.length == 1)
            configFileLocation = args[0];

        ConfigParams.loadConfig(configFileLocation);
//        SSLServerSocketFactory sslServerSocketFactory = setKeyManagerAndSSL();
//
//        if(null == sslServerSocketFactory){
//            System.out.println("Could not build Key Manager and ServerDriver Socket Factory. Exiting....");
//            System.exit(0);
//        }
//        java.net.ServerSocket serverSocket = sslServerSocketFactory.createServerSocket(ConfigParams.defaultPort);
//        sslServerSocket = (SSLServerSocket) serverSocket;
    }

    private static SSLServerSocketFactory setKeyManagerAndSSL() {
        SSLServerSocketFactory sslServerSocketFactory = null;
        try {

            KeyStore keyStore = KeyStore.getInstance(ConfigParams.keystoreType);
            keyStore.load(new FileInputStream(ConfigParams.pathToKeystore), ConfigParams.keyStorePass.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(ConfigParams.keyManagerType);
            keyManagerFactory.init(keyStore, ConfigParams.certificatePass.toCharArray());

            SSLContext sslContext = SSLContext.getInstance(ConfigParams.sslContext);
            SSLContext.setDefault(sslContext);
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
            sslServerSocketFactory = sslContext.getServerSocketFactory();

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return sslServerSocketFactory;
    }
}
