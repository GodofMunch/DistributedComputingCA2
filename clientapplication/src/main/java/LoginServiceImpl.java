import com.dave.server.LoginServiceGrpc;
import com.dave.server.LoginServiceOuterClass;
import io.grpc.stub.StreamObserver;

public class LoginServiceImpl extends LoginServiceGrpc.LoginServiceImplBase {
    @Override
    public void login(LoginServiceOuterClass.LoginRequest request,
                      StreamObserver<LoginServiceOuterClass.LoginResponse> responseObserver) {

        LoginServiceOuterClass.LoginResponse response = LoginServiceOuterClass.LoginResponse.newBuilder()
                .setAnswer("OK").build();
        System.out.println("Request: " + request);
        System.out.println("Response: " + response);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}