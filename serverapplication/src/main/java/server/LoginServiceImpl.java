package server;

import com.dave.server.LoginServiceGrpc;
import com.dave.server.LoginServiceOuterClass;
import db.Database;
import io.grpc.stub.StreamObserver;

public class LoginServiceImpl extends LoginServiceGrpc.LoginServiceImplBase {
    @Override
    public void login(LoginServiceOuterClass.LoginRequest request,
                      StreamObserver<LoginServiceOuterClass.LoginResponse> responseObserver) {

        String username = request.getUsername();
        String password = request.getPassword();

        Database db = Database.getInstance();
        LoginServiceOuterClass.LoginResponse response = null;
        int status = db.login(username, password);

        if(status > 0)
           response = LoginServiceOuterClass.LoginResponse.newBuilder()
                   .setAnswer("SUCCESS").build();
        else if(status == -1)
            response = LoginServiceOuterClass.LoginResponse.newBuilder()
                    .setAnswer("WRONG").build();
        else
            response = LoginServiceOuterClass.LoginResponse.newBuilder()
                    .setAnswer("ERROR").build();

        System.out.println("Request: " + request);
        System.out.println("Response: " + response);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
