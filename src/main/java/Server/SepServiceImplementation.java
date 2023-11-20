package Server;

import Utility.DataBase.DaoImplementations.Users.CustomerDaoImplementation;
import Utility.DataBase.DaoImplementations.Users.FarmerDaoImplementation;
import Utility.DataBase.DaoImplementations.Users.LoginDaoImplementation;
import Utility.DataBase.DaoImplementations.Users.RegisterDaoImplementation;
import Utility.DataBase.Daos.Users.CustomerDao;
import Utility.DataBase.Daos.Users.FarmerDao;
import Utility.DataBase.Daos.Users.LoginDao;
import Utility.DataBase.Daos.Users.RegisterDao;
import io.grpc.stub.StreamObserver;
import sep.*;

import java.util.ArrayList;

public class SepServiceImplementation extends SepServiceGrpc.SepServiceImplBase
{
    private RegisterDao registerDao;
    private LoginDao loginDao;
    private CustomerDao customerDao;
    private FarmerDao farmerDao;
    public SepServiceImplementation()
    {
        this.registerDao = new RegisterDaoImplementation();
        this.loginDao = new LoginDaoImplementation();
        this.customerDao = new CustomerDaoImplementation();
        this.farmerDao = new FarmerDaoImplementation();
    }

    //----------Login----------\\
    @Override
    public void login(loginRequest request, StreamObserver<generalPutResponse> responseObserver)
    {
       String temp = loginDao.login(request.getLogin());
        System.out.println(temp);

        generalPutResponse response = generalPutResponse.newBuilder()
                .setResp(temp)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
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
    public void getAllCustomers(getAllCustomersRequest request, StreamObserver<getAllCustomersResponse> responseObserver)
    {
        ArrayList<DtoCustomer> list = customerDao.getAllCustomers();
        getAllCustomersResponse res = getAllCustomersResponse.newBuilder()
                .addAllAllCustomers(list)
                .build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

    @Override
    public void getCustomerByPhone(getCustomerByPhoneRequest request, StreamObserver<getCustomerByPhoneResponse> responseObserver)
    {
        DtoCustomer x = customerDao.getCustomerById(request.getCustomersPhone());
        getCustomerByPhoneResponse res = getCustomerByPhoneResponse.newBuilder()
                .setCustomer(x)
                .build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

    @Override
    public void editCustomer(editCustomerRequest request, StreamObserver<generalPutResponse> responseObserver) {
        String x = customerDao.editCustomer(request.getEditedCustomer());
        generalPutResponse res = generalPutResponse.newBuilder()
                .setResp(x)
                .build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

    //----------Farmer----------\\
    @Override
    public void getAllFarmers(getAllFarmersRequest request, StreamObserver<getAllFarmersResponse> responseObserver) {
        ArrayList<DtoFarmer> list = farmerDao.getAllFarmers();
        getAllFarmersResponse res = getAllFarmersResponse.newBuilder()
                .addAllAllFarmers(list)
                .build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

    @Override
    public void getFarmerByPhone(getFarmerByPhoneRequest request, StreamObserver<getFarmerByPhoneResponse> responseObserver) {
        DtoFarmer x = farmerDao.getFarmersById(request.getFarmersPhone());
        getFarmerByPhoneResponse res = getFarmerByPhoneResponse.newBuilder()
                .setFarmer(x)
                .build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

    @Override
    public void editFarmer(editFarmerRequest request, StreamObserver<generalPutResponse> responseObserver) {
        String x = farmerDao.editFarmer(request.getEditedFarmer());
        generalPutResponse res = generalPutResponse.newBuilder()
                .setResp(x)
                .build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
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
