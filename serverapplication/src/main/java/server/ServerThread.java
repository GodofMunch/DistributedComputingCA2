package server;

import db.Database;

class ServerThread implements Runnable {
    ServerSocket myDataSocket;
    private int userId;

    ServerThread(ServerSocket myDataSocket) {
        this.myDataSocket = myDataSocket;
    }

    public void run() {
        boolean done = false;
        String message;
        try {
            while (!done) {
                message = myDataSocket.receiveMessage();
                if(message.equals("."))
                    done = true;
                else {
                    int protocolCode = Integer.parseInt(message.substring(0, 3));
                    if (protocolCode == DAVE.SERVER_ALIVE.getValue())
                        stillAlive();
                    if (protocolCode == DAVE.LOGIN_ATTEMPT.getValue())
                        login(message);
                    if (protocolCode == DAVE.REGISTER_ATTEMPT.getValue())
                        register(message);
                    if (protocolCode == DAVE.REQUEST_LOGOFF.getValue())
                        done = logoff();
                    if (protocolCode == DAVE.SEND_MESSAGE.getValue())
                        receiveChatMessage(message);
                    if (protocolCode == DAVE.REQUEST_MESSAGE_HISTORY.getValue())
                        sendMessageHistory();
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Exception caught in thread: " + ex.getMessage());
        }

    }

    private void sendMessageHistory() {
        Database db = Database.getInstance();
        String history = db.getHistory(userId);
        if(null == history)
            myDataSocket.sendMessage(DAVE.SEND_MESSAGE_HISTORY_FAILED.getValue() + "");
        else
            myDataSocket.sendMessage(DAVE.SEND_MESSAGE_HISTORY_SUCCESS.getValue() + history);
    }

    private void stillAlive(){
        myDataSocket.sendMessage(DAVE.SERVER_ALIVE.getValue() + "");
    }

    private void login(String message){
        String[] usernameAndPassword = message.substring(3).split(";");
        Database db = Database.getInstance();
        int userExists = db.login(usernameAndPassword[0], usernameAndPassword[1]);
        if(userExists >= 0) {
            userId = userExists;
            myDataSocket.sendMessage( DAVE.LOGIN_ACCEPT.getValue()+ "");
        } else if(userExists == -1)
            myDataSocket.sendMessage(DAVE.LOGIN_FAIL_NO_SUCH_USER.getValue() + "");
        else
            myDataSocket.sendMessage(DAVE.LOGIN_FAIL_GENERIC.getValue() + "");
    }

    private void register(String message){
        String[] usernameAndPassword = message.substring(3).split(";");
        Database db = Database.getInstance();
        int createdUser = db.register(usernameAndPassword[0].trim(), usernameAndPassword[1].trim());
        if(createdUser > 0) {
            userId = createdUser;
            myDataSocket.sendMessage(DAVE.REGISTER_SUCCESS.getValue() + "");
        } else if(createdUser == 0)
            myDataSocket.sendMessage(DAVE.REGISTER_FAIL_USER_EXISTS.getValue() + "");
        else
            myDataSocket.sendMessage(DAVE.REGISTER_FAIL_GENERIC.getValue() + "");
    }

    private boolean logoff(){
        int loggedOffUser = userId;
        userId = 0;
        if(userId == 0) {
            myDataSocket.sendMessage(DAVE.LOGOFF_ACCEPTED.getValue() + "");
            System.out.println("Session terminated by user " + loggedOffUser);
        }
        else
            myDataSocket.sendMessage(DAVE.LOGOFF_FAILED.getValue() + "");
        return userId == 0;
    }

    private void receiveChatMessage(String message){
        message = message.substring(3).trim();
        Database db = Database.getInstance();
        int stored = db.saveMessage(message, userId);
        if(stored == 0)
            myDataSocket.sendMessage(DAVE.MESSAGE_STORED.getValue() + "");
        else
            myDataSocket.sendMessage(DAVE.MESSAGE_STORE_FAILED.getValue() + "");
    }
}
