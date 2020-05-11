package db;

import server.ConfigParams;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Database {

    private static final Database instance = new Database();

    public static Database getInstance() {
        if (null == instance)
            return new Database();
        else
            return instance;
    }

    public int login(String userName, String password) {
        int userId = 0;
        String input;

        File usersFile = ConfigParams.usersFile;
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(usersFile));

            while ((input = bufferedReader.readLine()) != null) {
                String[] userAndPass = input.split(";");
                if (userAndPass[0].equals(userName.trim()) && userAndPass[1].equals(password))
                    userId = Integer.parseInt(input.split(";")[2]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            userId = -2;
        } catch (IOException e) {
            e.printStackTrace();
            userId = -3;
        }finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    userId = -4;
                }
            }
        }
        if(userId != 0)
            return userId;
        else
            return -1;
    }

    public int register(String userName, String password) {
        String input;
        BufferedWriter bufferedWriter = null;
        File usersFile = ConfigParams.usersFile;
        int userId = 1;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(usersFile));
            while ((input = bufferedReader.readLine()) != null) {
                if(input.contains(userName)) {
                    userId = 0;
                    break;
                }
                userId++;
            }
            bufferedReader.close();
            bufferedWriter = new BufferedWriter(new FileWriter(usersFile, true));
            bufferedWriter.write(userName + ";" + password + ";" + userId);
            bufferedWriter.newLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            userId = -1;
        } catch (IOException e) {
            e.printStackTrace();
            userId = -2;
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    userId = -3;
                }
            }
        }
        return userId;
    }

    public int saveMessage(String message, int userId){
        try {
            File userMessageStore = new File(ConfigParams.usersMessages + "/messages__" + userId + ".txt");
            if(!userMessageStore.exists())
                userMessageStore.createNewFile();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime currentTime = LocalDateTime.now();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(userMessageStore,true));
            bufferedWriter.write(message + ";" + dateTimeFormatter.format(currentTime) + ";");
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.close();

            return 0;

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public String getHistory(int userId) {
        try {
            File userMessageStore = new File(ConfigParams.usersMessages + "/messages__" + userId + ".txt");
            if (!userMessageStore.exists())
                userMessageStore.createNewFile();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(userMessageStore));
            String input;
            String history = "";

            while((input = bufferedReader.readLine())!= null)
                history += input;

            bufferedReader.close();
            return history;


        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
