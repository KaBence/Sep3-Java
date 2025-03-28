package Client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import sep.*;

public class SepClient {
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

                DtoRegisterFarmer y = DtoRegisterFarmer.newBuilder()
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

                // DtoLogin z = DtoLogin.newBuilder()
                //                 .setPhoneNumber("123456789")
                //                 .setPassword("password")
                //                 .build();

                // loginRequest loginRequest = sep.loginRequest.newBuilder().setLogin(z).build();
                // generalPutResponse loginRes = proofStub.login(loginRequest);
                // System.out.println(loginRes.getResp());

                DtoOrder dtoOrder = DtoOrder.newBuilder()
                                .setCustomerId("0000")
                                .setDate("2023-11-28")
                                .build();

                ArrayList<DtoOrderItem> dtoOrderItems = new ArrayList<>();

                DtoOrderItem dtoOrderItem = DtoOrderItem.newBuilder()
                                .setAmount(100)
                                .setProductId(5)
                                .setFarmName("Dapper Dell")
                                .build();

                DtoOrderItem dtoOrderItem2 = DtoOrderItem.newBuilder()
                                .setAmount(100)
                                .setProductId(3)
                                .setFarmName("Mordor")
                                .build();

                DtoOrderItem dtoOrderItem3 = DtoOrderItem.newBuilder()
                                .setAmount(500)
                                .setProductId(4)
                                .setFarmName("Dapper Dell")
                                .build();

                dtoOrderItems.add(dtoOrderItem3);
                dtoOrderItems.add(dtoOrderItem2);
                dtoOrderItems.add(dtoOrderItem);

                Collections.sort(dtoOrderItems, Comparator.comparing(DtoOrderItem::getFarmName));

                System.out.println("Sorted Order Items by Farm Name:");
                for (DtoOrderItem item : dtoOrderItems) {
                        System.out.println("OrderID: " + item.getOrderId() +
                                        ", ProductID: " + item.getProductId() +
                                        ", Amount: " + item.getAmount() +
                                        ", Type: " + item.getType() +
                                        ", Price: " + item.getPrice() +
                                        ", FarmName: " + item.getFarmName());
                }

                createOrderRequest req2 = createOrderRequest.newBuilder()
                                .setNewOrder(dtoOrder)
                                .addAllOrderItems(dtoOrderItems)
                                .setNote("Debugging CreateOrder Ver. 320")
                                .setPaymentMethod("Google Pay")
                                .build();

                generalPutResponse res2 = proofStub.createNewOrder(req2);
                System.out.println(res2.getResp());

                DtoReview dtoReview = DtoReview.newBuilder()
                                .setText("test")
                                .setStar(3.3)
                                .setFarmerId("1")
                                .setCustomerId("test")
                                .setOrderId(4)
                                .build();

                postReviewRequest requestReview = postReviewRequest.newBuilder()
                                .setReview(dtoReview)
                                .build();

                generalPutResponse response = proofStub.postReview(requestReview);
                System.out.println("Review response: " + response);

                // =========================================
                DtoComment comment = DtoComment.newBuilder()
                                .setText("I hope i can see it")
                                .setFarmerId("1")
                                .setCustomerId("test")
                                .setUsername("test")
                                .build();

                putCommentRequest requestComment = putCommentRequest.newBuilder()
                                .setComment(comment)
                                .build();

                generalPutResponse responseComment = proofStub.postComment(requestComment);
                System.out.println("Comment Response: " + responseComment);

                // ========================================================
                getAllReviewsByFarmerRequest reviewsByFarmerRequest = getAllReviewsByFarmerRequest.newBuilder()
                                .setFarmer("1")
                                .build();

                getAllReviewsByFarmerResponse getAllReviewsByFarmerResponse = proofStub
                                .getAllReviewsByFarmer(reviewsByFarmerRequest);
                System.out.println("All reviews by farmer: 1 - \n"
                                + getAllReviewsByFarmerResponse);

                // =========================================================

                // getReceiptsByCustomerRequest req = getReceiptsByCustomerRequest.newBuilder().setCustomer("0000")
                //                 .build();

                // getReceiptsByCustomerResponse res = proofStub.getCustomersReceipt(req);

                // for (DtoCustomerSendReceipt item : res.getReceiptsList()) {
                //         for (DtoReceipt item2 : item.getReceiptsList()) {
                //                 System.out.println(item2.getOrderId());

                //         }
                //         System.out.println("==========");
                // }

                // ====================================================

                // getPendingReceiptsByFarmerRequest req = getPendingReceiptsByFarmerRequest.newBuilder()
                //                 .setFarmer("12345").build();

                // getReceiptsResponse res = proofStub.getPendingFarmersReceipt(req);

                // for (DtoSendReceipt item : res.getReceiptsList()) {
                //         System.out.println("OrderID: " + item.getReceipt().getOrderId() +
                //                         ", Status: " + item.getReceipt().getStatus() +
                //                         ", FarmerID: " + item.getReceipt().getFarmerId() +
                //                         ", CustomerID: " + item.getReceipt().getCustomerId() +
                //                         ", Text: " + item.getReceipt().getText() +
                //                         ", method: " + item.getReceipt().getPaymentMethod());
                // }

                // =========================================================

                // farmersApprovalRequest req = farmersApprovalRequest.newBuilder().setOrderId(2).setApprove(false)
                //                 .build();

                // generalPutResponse res = proofStub.farmersApproval(req);

                // System.out.println(res.getResp());

                // =========================================
                // getRejectedReceiptsByFarmerRequest req = getRejectedReceiptsByFarmerRequest.newBuilder()
                //                 .setFarmer("0147").build();

                // getReceiptsResponse res = proofStub.getRejectedFarmersReceipt(req);

                // for (DtoSendReceipt item : res.getReceiptsList()) {
                //         System.out.println("OrderID: " + item.getReceipt().getOrderId() +
                //                         ", Status: " + item.getReceipt().getStatus() +
                //                         ", FarmerID: " + item.getReceipt().getFarmerId() +
                //                         ", CustomerID: " + item.getReceipt().getCustomerId() +
                //                         ", Text: " + item.getReceipt().getText() +
                //                         ", method: " + item.getReceipt().getPaymentMethod());
                // }
        }
}
