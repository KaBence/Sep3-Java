package Server;

import Utility.DataBase.DaoImplementations.Users.RegisterDaoImplementation;
import Utility.DataBase.Daos.Users.RegisterDao;
import io.grpc.stub.StreamObserver;
import sep.*;

public class SepServiceImplementation extends SepServiceGrpc.SepServiceImplBase
{
    private RegisterDao registerDao;

    public SepServiceImplementation()
    {
        this.registerDao = new RegisterDaoImplementation();
    }

    //----------Login----------\\
    @Override
    public void login(loginRequest request, StreamObserver<generalPutResponse> responseObserver)
    {
        super.login(request, responseObserver);
    }

    //----------Register----------\\
    @Override
    public void registerCustomer(registerCustomerRequest request, StreamObserver<generalPutResponse> responseObserver)
    {
        String temp = registerDao.RegisterCustomer(request.getNewCustomer());
        System.out.println(temp);

        generalPutResponse response = generalPutResponse.newBuilder()
                .setResp(temp)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void registerFarmer(registerFarmerRequest request, StreamObserver<generalPutResponse> responseObserver)
    {
        String temp = registerDao.RegisterFarmer(request.getNewFarmer());
        System.out.println(temp);

        generalPutResponse response = generalPutResponse.newBuilder()
                .setResp(temp)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    //----------Customer----------\\
    @Override
    public void getAllCustomers(getAllCustomersRequest request, StreamObserver<getAllCustomersResponse> responseObserver) {
        super.getAllCustomers(request, responseObserver);
    }

    @Override
    public void getCustomerByPhone(getCustomerByPhoneRequest request, StreamObserver<getCustomerByPhoneResponse> responseObserver) {
        super.getCustomerByPhone(request, responseObserver);
    }

    //----------Farmer----------\\
    @Override
    public void getAllFarmers(getAllFarmersRequest request, StreamObserver<getAllFarmersResponse> responseObserver) {
        super.getAllFarmers(request, responseObserver);
    }

    @Override
    public void getFarmerByPhone(getCustomerByPhoneRequest request, StreamObserver<getCustomerByPhoneResponse> responseObserver) {
        super.getFarmerByPhone(request, responseObserver);
    }

    //----------Order----------\\
    @Override
    public void createNewOrder(createOrderRequest request, StreamObserver<generalPutResponse> responseObserver) {
        super.createNewOrder(request, responseObserver);
    }

    @Override
    public void getAllOrders(getAllOrdersRequest request, StreamObserver<getAllOrdersResponse> responseObserver) {
        super.getAllOrders(request, responseObserver);
    }

    @Override
    public void getOrderById(getOrderByIdRequest request, StreamObserver<getOrderByIdResponse> responseObserver) {
        super.getOrderById(request, responseObserver);
    }

    //----------Product----------\\
    @Override
    public void createProduct(createProductRequest request, StreamObserver<generalPutResponse> responseObserver) {
        super.createProduct(request, responseObserver);
    }

    @Override
    public void getProductsByFarmer(getAllProductsByFarmerRequest request, StreamObserver<getAllProductsByFarmerResponse> responseObserver) {
        super.getProductsByFarmer(request, responseObserver);
    }

    @Override
    public void getProductById(getProductByIdRequest request, StreamObserver<getProductByIdResponse> responseObserver) {
        super.getProductById(request, responseObserver);
    }

    //----------OrderItem----------\\
    @Override
    public void createNewOrderItem(createOrderItemRequest request, StreamObserver<generalPutResponse> responseObserver) {
        super.createNewOrderItem(request, responseObserver);
    }

    @Override
    public void getAllOrderItemsFromOrder(getAllOrderItemsFromOrderRequest request, StreamObserver<getAllOrderItemsFromOrderResponse> responseObserver) {
        super.getAllOrderItemsFromOrder(request, responseObserver);
    }

    //----------Receipt----------\\
    @Override
    public void createReceipt(createReceiptRequest request, StreamObserver<generalPutResponse> responseObserver) {
        super.createReceipt(request, responseObserver);
    }

    @Override
    public void getAllReceipts(getAllReceiptsRequest request, StreamObserver<getAllReceiptsResponse> responseObserver) {
        super.getAllReceipts(request, responseObserver);
    }

    @Override
    public void getReceiptById(getReceiptByIdRequest request, StreamObserver<getReceiptByIdResponse> responseObserver) {
        super.getReceiptById(request, responseObserver);
    }

    //----------Comment----------\\
    @Override
    public void postComment(putCommentRequest request, StreamObserver<generalPutResponse> responseObserver) {
        super.postComment(request, responseObserver);
    }

    //----------Review----------\\
    @Override
    public void postReview(postReviewRequest request, StreamObserver<generalPutResponse> responseObserver) {
        super.postReview(request, responseObserver);
    }

    @Override
    public void getAllReviewsByFarmer(getAllReviewsByFarmerRequest request, StreamObserver<getAllReviewsByFarmerResponse> responseObserver) {
        super.getAllReviewsByFarmer(request, responseObserver);
    }

    @Override
    public void getReviewBy(getReviewByIdRequest request, StreamObserver<getReviewByIdResponse> responseObserver) {
        super.getReviewBy(request, responseObserver);
    }
}
