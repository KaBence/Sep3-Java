package Client;

import Server.SepGrpServer;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import proof.*;
import sep.*;

public class SepClient
{
    public static void main(String[] args) {
        ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 1337)
                .usePlaintext()
                .build();
        SepServiceGrpc.SepServiceBlockingStub proofStub = SepServiceGrpc.newBlockingStub(managedChannel);

        DtoRegisterCustomer x = DtoRegisterCustomer.newBuilder()
                .setPhoneNumber("123456789")
                .setPassword("password")
                .setRepeatPassword("password")
                .setFirstName("Test")
                .setLastName("TestLastName")
                .setAddress("idk")
                .build();

        registerCustomerRequest req = registerCustomerRequest.newBuilder().setNewCustomer(x).build();
        generalPutResponse res = proofStub.registerCustomer(req);
        System.out.println(res.getResp());


        DtoRegisterFarmer y= DtoRegisterFarmer.newBuilder()
                .setPhoneNumber("987654321")
                .setPassword("password")
                .setRepeatPassword("password")
                .setFirstName("Test")
                .setLastName("TestLastName")
                .setAddress("idk")
                .setPesticides(true)
                .setRating(4.6)
                .build();

        registerFarmerRequest req1 = registerFarmerRequest.newBuilder().setNewFarmer(y).build();
        generalPutResponse res1 = proofStub.registerFarmer(req1);
        System.out.println(res1.getResp());

        DtoLogin z = DtoLogin.newBuilder()
                .setPhoneNumber("123456789")
                .setPassword("password")
                .build();

        loginRequest loginRequest = sep.loginRequest.newBuilder().setLogin(z).build();
        //generalPutResponse loginRes = proofStub.login(loginRequest);
        //System.out.println(loginRes.getResp());
    }
}
