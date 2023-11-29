package Client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import sep.*;

import java.util.ArrayList;

public class SepClient
{
    public static void main(String[] args) {
        ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 1337)
                .usePlaintext()
                .build();
        SepServiceGrpc.SepServiceBlockingStub proofStub = SepServiceGrpc.newBlockingStub(managedChannel);

        /*DtoRegisterCustomer x = DtoRegisterCustomer.newBuilder()
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
        //System.out.println(loginRes.getResp());*/

        DtoOrder dtoOrder=DtoOrder.newBuilder()
                .setCustomerId("0000")
                .setDate("2023-11-28")
                .build();

        ArrayList<DtoOrderItem> dtoOrderItems=new ArrayList<>();

        DtoOrderItem dtoOrderItem=DtoOrderItem.newBuilder()
                .setAmount(100)
                .setProductId(5)
                .build();

        DtoOrderItem dtoOrderItem2=DtoOrderItem.newBuilder()
                .setAmount(100)
                .setProductId(3)
                .build();

        DtoOrderItem dtoOrderItem3=DtoOrderItem.newBuilder()
                .setAmount(500)
                .setProductId(4)
                .build();

        dtoOrderItems.add(dtoOrderItem3);
        dtoOrderItems.add(dtoOrderItem2);
        dtoOrderItems.add(dtoOrderItem);

        createOrderRequest req2=createOrderRequest.newBuilder()
                .setNewOrder(dtoOrder)
                .addAllOrderItems(dtoOrderItems)
                .setNote("Debugging CreateOrder Ver. 320")
                .setPaymentMethod("Google Pay")
                .build();

        generalPutResponse res2=proofStub.createNewOrder(req2);
        System.out.println(res2.getResp());
    }
}
