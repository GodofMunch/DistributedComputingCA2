import java.io.*;

public class Client {

    public static void main(String[] args) {

        ConfigParams.loadConfig(args[0]);
        InputStreamReader is = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(is);
        System.setProperty("javax.net.ssl.trustStore",ConfigParams.trustStoreName);
        System.setProperty("javax.net.ssl.trustStorePassword",ConfigParams.trustStorePassword);
        try {

            ClientHelper helper = new ClientHelper();
            boolean done = false;
            String message;
            while (!done) {
                System.out.println("Welcome to DAVE twitter messaging protocol.\n\nTo login, type [l]ogin" +
                        "\nIf you are new to the service, please register an account by typing [r]egister.");
                message = br.readLine();
                if ((message.trim()).equals (ConfigParams.endMessage)){
                    done = true;
                    helper.done();
                }
                if(message.trim().startsWith("l") || message.trim().startsWith("L"))
                    login(br, helper);
                if(message.trim().startsWith("r") || message.trim().startsWith("R"))
                    register(br, helper);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace( );
        }
    }

    private static void login(BufferedReader br, ClientHelper helper){

        String login = helper.login(br);

        System.out.println(login);
        if(login.equals("SUCCESS")) {
            System.out.println("Welcome to Twitter Messenger!");
            messenger(br, helper);
        }
        else if(login.equals("WRONG"))
            System.out.println("Sorry! No such user exists on the system with this password.");
        else if(login.equals("ERROR"))
            System.out.println("Sorry! Something went wrong. Please try again soon.");
        else
            System.out.println("Could not establish a connection with the server located at " +
                    ConfigParams.serverAddress + ":" + ConfigParams.defaultPort);

    }

    private static void register(BufferedReader br, ClientHelper helper){
        String register = helper.register(br);
        int protocolCode = Integer.parseInt(register.trim().substring(0,3));
        if(protocolCode == DAVE.REGISTER_SUCCESS.getValue()) {
            String username = register.split(";")[1];
            System.out.println("Welcome to Twitter Messenger " + username);
            messenger(br, helper);
        }
        else if(protocolCode == DAVE.REGISTER_FAIL_USER_EXISTS.getValue())
            System.out.println("Sorry! This username already exists, please try another.");
        else if(protocolCode == DAVE.REGISTER_FAIL_GENERIC.getValue())
            System.out.println("Sorry! Something went wrong. Try again Soon");
        else if(protocolCode == DAVE.SERVER_DEAD.getValue())
            System.out.println("Could not establish a connection with the server located at " +
                    ConfigParams.serverAddress + ":" + ConfigParams.defaultPort);

    }

    private static boolean logoff(ClientHelper helper){
        String logoff = helper.logoff();
        int protocolCode = Integer.parseInt(logoff.trim().substring(0,3));

        if(protocolCode == DAVE.LOGOFF_ACCEPTED.getValue())
            System.out.println("You have logged out. Goodbye!");
        else if(protocolCode == DAVE.LOGOFF_FAILED.getValue())
            System.out.println("Something went wrong, Please try again!");
        else if(protocolCode == DAVE.SERVER_DEAD.getValue())
            System.out.println("Could not establish a connection with the server located at " +
                    ConfigParams.serverAddress + ":" + ConfigParams.defaultPort);

        return protocolCode == DAVE.LOGOFF_ACCEPTED.getValue();
    }

    private static void messenger(BufferedReader br, ClientHelper helper){
        boolean done = false;
        String message;
        System.out.println("\n\nSay something to get started...\nTo logoff, type \'" +
                ConfigParams.endMessage + "\'\nIf you would like to download and view your message history, " +
                "type \'" + ConfigParams.downloadHistoryCommand + "\'");
        while(!done){
            try {
                message = br.readLine();

                if(message.equals(ConfigParams.endMessage))
                    done = logoff(helper);
                else if(message.equals(ConfigParams.downloadHistoryCommand))
                    downloadHistory(helper);
                else
                    sendMessage(message, helper);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void downloadHistory(ClientHelper helper) {
        String history = helper.downloadHistory();
        int protocolCode = Integer.parseInt(history.substring(0,3).trim());
        if(protocolCode == DAVE.SEND_MESSAGE_HISTORY_FAILED.getValue())
            System.out.println("Could not download history at this time, please try again later.");
        else if(protocolCode == DAVE.SERVER_DEAD.getValue())
            System.out.println("There was an issue connecting to the server. Please try again later");
        else if(protocolCode == DAVE.SEND_MESSAGE_HISTORY_SUCCESS.getValue())
            printHistory(history.substring(3).trim());
    }

    private static void printHistory(String history) {
        String[] messagesAndDates = history.split(";");
        String[] messages = new String[messagesAndDates.length / 2];
        String[] dates = new String[messagesAndDates.length / 2];

        int j = 0;
        int k = 0;

        for (int i = 0; i < messagesAndDates.length; i++)
            if ((i % 2) != 0) {
                dates[j] = messagesAndDates[i];
                j++;
            } else {
                messages[k] = messagesAndDates[i];
                k++;
            }

        for(int i = 0; i < messages.length; i++)
            System.out.println("On " + dates[i] + " you sent:\n\n\t" + messages[i] + "\n");

        saveHistoryToFile(messages, dates);
    }

    private static void saveHistoryToFile(String[] messages, String[] dates) {
        System.out.println("Saving Messages to file : " + ConfigParams.clientMessagesFile.getAbsolutePath());
        int[] messagesAlreadyInFile = new int[messages.length];
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(ConfigParams.clientMessagesFile));
            String input;

            while((input = bufferedReader.readLine()) != null)
                for (int i = 0; i < messages.length; i ++)
                    if(input.equals(messages[i] + ";" + dates[i] + ";"))
                        messagesAlreadyInFile[i] = 1;


            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(ConfigParams.clientMessagesFile, true));

            for(int i = 0; i < messagesAlreadyInFile.length; i ++)
                if(messagesAlreadyInFile[i] == 0) {
                    bufferedWriter.write(messages[i] + ";" + dates[i] + ";");
                    bufferedWriter.newLine();
                }

            bufferedReader.close();
            bufferedWriter.flush();
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Saved to File...");
    }

    private static void sendMessage(String message, ClientHelper helper) {
        String sent = helper.sendMessage(message);
        int protocolCode = Integer.parseInt(sent.substring(0,3).trim());
        if(protocolCode == DAVE.MESSAGE_STORED.getValue())
            System.out.println("Your message was uploaded.\n\n");
        else if(protocolCode == DAVE.MESSAGE_STORE_FAILED.getValue())
            System.out.println("There was an issue uploading your message.. Please try again");
        else
            System.out.println("There was an issue connecting to the server. Please try again later");
    }
}
