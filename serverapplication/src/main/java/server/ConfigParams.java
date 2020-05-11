package server;

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

public class ConfigParams {

    public static File usersFile;
    public static String dbLocation;
    public static int defaultPort;
    public static File usersMessages;
    public static String sslContext;
    public static String keyManagerType;
    public static String keystoreType;
    public static String keystoreName;
    public static String keyStorePass;
    public static String certificatePass;
    public static String pathToKeystore;

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
                    String dbPortString = e.getElementsByTagName("defaultPort").item(0).getTextContent().trim();
                    sslContext = e.getElementsByTagName("sslContext").item(0).getTextContent().trim();
                    pathToKeystore = e.getElementsByTagName("pathToKeystore").item(0).getTextContent().trim();
                    keyManagerType = e.getElementsByTagName("keyManagerType").item(0).getTextContent().trim();
                    keystoreType = e.getElementsByTagName("keystoreType").item(0).getTextContent().trim();
                    keystoreName = e.getElementsByTagName("keystoreName").item(0).getTextContent().trim();
                    keyStorePass = e.getElementsByTagName("keystorePass").item(0).getTextContent().trim();
                    certificatePass = e.getElementsByTagName("certificatePass").item(0).getTextContent().trim();


                    File dbLocationFile = new File(dbLocation);
                    pathToKeystore = pathToKeystore + keystoreName;

                    if(!dbLocationFile.isDirectory()) {
                        System.out.println("This directory does not exist, Please check dbLocation in config file.");
                    } else {
                        File usersDB = new File(dbLocation + "/users.txt");
                        usersMessages = new File(dbLocation + "/messages/");

                        if (!usersDB.exists()) {
                            usersDB.createNewFile();
                            System.out.println("Could not Find Users File, Created a new one.");
                        }
                        usersFile = usersDB;
                        if (!usersMessages.exists()){
                            usersMessages.mkdir();
                            System.out.println("Could not find Messages Directory, Created a new one");
                        }
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
