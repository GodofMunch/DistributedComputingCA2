import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

public class ConfigParams {

    public static String dbLocation;
    public static String serverAddressString;
    public static InetAddress serverAddress;
    public static int defaultPort;
    public static String endMessage;
    public static File clientMessagesFile;
    public static String trustStorePassword;
    public static String downloadHistoryCommand;
    public static String pathToTrustStore;
    public static String trustStoreName;

    public static void loadConfig(String configFileLocation){
        File configFile = new File(configFileLocation);
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(configFile);
            doc.getDocumentElement().normalize();
            NodeList nodes = doc.getElementsByTagName("config");

            for(int i = 0; i < nodes.getLength(); i ++){
                Node node = nodes.item(i);

                if(node.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element) node;

                    dbLocation = e.getElementsByTagName("dbLocation").item(0).getTextContent().trim();
                    serverAddressString = e.getElementsByTagName("serverAddress").item(0).getTextContent().trim();
                    trustStorePassword = e.getElementsByTagName("trustStorePassword").item(0).getTextContent().trim();
                    downloadHistoryCommand = e.getElementsByTagName("downloadHistoryCommand").item(0).getTextContent().trim();
                    pathToTrustStore = e.getElementsByTagName("pathToTrustStore").item(0).getTextContent().trim();
                    trustStoreName = e.getElementsByTagName("trustStoreName").item(0).getTextContent().trim();

                    trustStoreName = pathToTrustStore + "/" + trustStoreName;


                    try {
                        serverAddress = InetAddress.getByName(serverAddressString);
                    } catch(Exception ex){
                        System.out.print("Could not convert serverAddress in config file to an IP address");
                    }
                    String dbPortString = e.getElementsByTagName("defaultPort").item(0).getTextContent().trim();
                    endMessage = e.getElementsByTagName("endMessage").item(0).getTextContent().trim();

                    File dbLocationFile = new File(dbLocation);

                    if(!dbLocationFile.isDirectory()) {
                        System.out.println("This directory does not exist, Please check dbLocation in config file.");
                    } else {
                        File usersMessages = new File(dbLocation + "/messages/");
                        if (!usersMessages.exists()){
                            usersMessages.mkdir();
                            System.out.println("Could not find Messages Directory, Created a new one");
                        }
                        clientMessagesFile = new File(usersMessages + "/messages.txt");
                        if(!clientMessagesFile.exists())
                            clientMessagesFile.createNewFile();
                    }
                    try{
                        defaultPort = Integer.parseInt(dbPortString);
                        if(defaultPort < 0 || defaultPort > 65535) {
                            System.out.println("Port Number must be between 0 and 65535. Please consult config file.");
                            System.exit(0);
                        }
                    } catch(Exception ex){
                        System.out.println(ex.getMessage());
                    }
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
