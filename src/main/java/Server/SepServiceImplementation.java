package Server;

import Utility.DataBase.DaoImplementations.Order.OrderDaoImplementation;
import Utility.DataBase.DaoImplementations.Product.ProductDaoImplementation;
import Utility.DataBase.DaoImplementations.Receipt.ReceiptDaoImplementation;
import Utility.DataBase.DaoImplementations.Review.ReviewDaoImplementation;
import Utility.DataBase.DaoImplementations.Users.CustomerDaoImplementation;
import Utility.DataBase.DaoImplementations.Users.FarmerDaoImplementation;
import Utility.DataBase.DaoImplementations.Users.LoginDaoImplementation;
import Utility.DataBase.DaoImplementations.Users.RegisterDaoImplementation;
import Utility.DataBase.Daos.Order.OrderDao;
import Utility.DataBase.Daos.Product.ProductDao;
import Utility.DataBase.Daos.Receipt.ReceiptDao;
import Utility.DataBase.Daos.Review.ReviewDao;
import Utility.DataBase.Daos.Users.CustomerDao;
import Utility.DataBase.Daos.Users.FarmerDao;
import Utility.DataBase.Daos.Users.LoginDao;
import Utility.DataBase.Daos.Users.RegisterDao;
import io.grpc.stub.StreamObserver;
import sep.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SepServiceImplementation extends SepServiceGrpc.SepServiceImplBase {
    private final RegisterDao registerDao;
    private final LoginDao loginDao;
    private final CustomerDao customerDao;
    private final FarmerDao farmerDao;
    private final ProductDao productDao;
    private final OrderDao orderDao;
    private final ReceiptDao receiptDao;

    private final ReviewDao reviewDao;

    public SepServiceImplementation() {
        registerDao = new RegisterDaoImplementation();
        loginDao = new LoginDaoImplementation();
        customerDao = new CustomerDaoImplementation();
        farmerDao = new FarmerDaoImplementation();
        productDao = new ProductDaoImplementation();
        orderDao=new OrderDaoImplementation();
        receiptDao=new ReceiptDaoImplementation();
        reviewDao = new ReviewDaoImplementation();
    }

    //----------Login----------\\
    @Override
    public void login(loginRequest request, StreamObserver<loginResponse> responseObserver) {
        String temp = null;
        int pesticides= 0;
        String farmName= "";
        double rating=0.0;
        try {
            temp = loginDao.login(request.getLogin());
        } catch (Exception e) {
            temp = e.getMessage();
        }
        String out = "Not Found";
        ArrayList<DtoFarmer> farmers = farmerDao.getAllFarmers(pesticides, farmName, rating);
        ArrayList<DtoCustomer> customers = customerDao.getAllCustomers();
        for (int i = 0; i < farmers.size(); i++)
        {
            if (farmers.get(i).getPhoneNumber().equals(temp))
            {
                out = "Farmer";
                i = farmers.size() + 1;
            }
        }
        if (out.equals("Not Found"))
        {
            for (int i = 0; i < customers.size(); i++)
            {
                if (customers.get(i).getPhoneNumber().equals(temp))
                {
                    out = "Customer";
                    i = customers.size() + 1;
                }
            }
        }
        System.out.println("Login - "+out+": "+temp);

        loginResponse response = loginResponse.newBuilder()
                .setPhoneNumber(temp)
                .setInstanceOf(out)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    //----------Register----------\\
    @Override
    public void registerCustomer(registerCustomerRequest request, StreamObserver<generalPutResponse> responseObserver) {
        String temp = registerDao.RegisterCustomer(request.getNewCustomer());
        System.out.println("Register Customer - "+temp);

        generalPutResponse response = generalPutResponse.newBuilder()
                .setResp(temp)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void registerFarmer(registerFarmerRequest request, StreamObserver<generalPutResponse> responseObserver) {
        String temp = registerDao.RegisterFarmer(request.getNewFarmer());
        System.out.println("Register Farmer - "+temp);

        generalPutResponse response = generalPutResponse.newBuilder()
                .setResp(temp)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    //----------Customer----------\\
    @Override
    public void getAllCustomers(getAllCustomersRequest request, StreamObserver<getAllCustomersResponse> responseObserver) {
        ArrayList<DtoCustomer> list = customerDao.getAllCustomers();
        System.out.println("Get all Customers - size: "+list.size());
        getAllCustomersResponse res = getAllCustomersResponse.newBuilder()
                .addAllAllCustomers(list)
                .build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

    @Override
    public void getCustomerByPhone(getCustomerByPhoneRequest request, StreamObserver<getCustomerByPhoneResponse> responseObserver) {
        DtoCustomer x = customerDao.getCustomerById(request.getCustomersPhone());
        System.out.println("Get Customer by Phone number - "+x.getPhoneNumber());
        getCustomerByPhoneResponse res = getCustomerByPhoneResponse.newBuilder()
                .setCustomer(x)
                .build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

    @Override
    public void editCustomer(editCustomerRequest request, StreamObserver<generalPutResponse> responseObserver) {
        String x = customerDao.editCustomer(request.getEditedCustomer());
        System.out.println("Edit Customer - "+x);
        generalPutResponse res = generalPutResponse.newBuilder()
                .setResp(x)
                .build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

    //----------Farmer----------\\
    @Override
    public void getAllFarmers(getAllFarmersRequest request, StreamObserver<getAllFarmersResponse> responseObserver) {
        ArrayList<DtoFarmer> list = farmerDao.getAllFarmers(request.getPesticides(), request.getFarmName(), request.getRating());
        System.out.println("Get all Farmers - size: "+list.size());
        getAllFarmersResponse res = getAllFarmersResponse.newBuilder()
                .addAllAllFarmers(list)
                .build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

    @Override
    public void getFarmerByPhone(getFarmerByPhoneRequest request, StreamObserver<getFarmerByPhoneResponse> responseObserver) {
        DtoFarmer x = farmerDao.getFarmersById(request.getFarmersPhone());
        System.out.println("Get Product by PhoneNumber - "+x.getPhoneNumber());
        getFarmerByPhoneResponse res = getFarmerByPhoneResponse.newBuilder()
                .setFarmer(x)
                .build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

    @Override
    public void editFarmer(editFarmerRequest request, StreamObserver<generalPutResponse> responseObserver) {
        String x = farmerDao.editFarmer(request.getEditedFarmer());
        System.out.println("Edit Farmer - "+x);
        generalPutResponse res = generalPutResponse.newBuilder()
                .setResp(x)
                .build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

    //----------Order----------\\
    @Override
    public void createNewOrder(createOrderRequest request, StreamObserver<generalPutResponse> responseObserver) {
        List<DtoOrderItem> orderItems = new ArrayList<>(request.getOrderItemsList());
        Collections.sort(orderItems, Comparator.comparing(DtoOrderItem::getFarmName));

        String x;
        try {
            x=orderDao.createOrder(request.getNewOrder(),orderItems,request.getPaymentMethod(),request.getNote());
            System.out.println("Good "+x);
        }
        catch (Exception e){
            x=e.getMessage();
            System.out.println("Bad "+x);
        }

        generalPutResponse res=generalPutResponse.newBuilder()
                .setResp(x)
                .build();

        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }



    //----------Product----------\\
    @Override
    public void createProduct(createProductRequest request, StreamObserver<generalPutResponse> responseObserver) {
        String temp;
        try {
            temp = productDao.createProduct(request.getNewProduct());
        } catch (Exception e) {
            temp = e.getMessage();
        }
        System.out.println("Create Product - "+temp);

        generalPutResponse response = generalPutResponse.newBuilder()
                .setResp(temp)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getProductsByFarmer(getAllProductsByFarmerRequest request, StreamObserver<getAllProductsByFarmerResponse> responseObserver) {
        ArrayList<DtoProduct> list = productDao.getProductsByFarmer(request.getFarmer(),request.getType(), request.getAmount(), request.getPrice());
        System.out.println("Get Product by Farmer - size: "+list.size());
        getAllProductsByFarmerResponse res = getAllProductsByFarmerResponse.newBuilder()
                .addAllAllProducts(list)
                .build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

    @Override
    public void getProductById(getProductByIdRequest request, StreamObserver<getProductByIdResponse> responseObserver) {
        DtoProduct x = productDao.getProductById(request.getId());
        System.out.println("Get Product by id - "+x.getId());
        getProductByIdResponse res = getProductByIdResponse.newBuilder()
                .setProduct(x)
                .build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }


    @Override
    public void getAllProducts(getAllProductsRequest request, StreamObserver<getAllProductsResponse> responseObserver)
    {
        ArrayList<DtoProduct> list =productDao.getAllProducts(request.getType(), request.getAmount(), request.getPrice());
        getAllProductsResponse res = getAllProductsResponse.newBuilder()
                .addAllAllProducts(list)
                .build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

    @Override
    public void updateProduct(updateProductRequest request, StreamObserver<generalPutResponse> responseObserver) {
        String x;
        try {
            x = productDao.editProduct(request.getProduct());
        } catch (Exception e) {
            x = e.getMessage();
        }

        System.out.println("Edit Product - "+x);
        generalPutResponse res = generalPutResponse.newBuilder()
                .setResp(x)
                .build();
        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteProduct(deleteProductRequest request, StreamObserver<generalPutResponse> responseObserver) {
        String temp;
        try {
            temp = productDao.deleteProduct(request.getId());
        } catch (Exception e) {
            temp = e.getMessage();
        }
        System.out.println("Delete Product - "+temp);

        generalPutResponse response = generalPutResponse.newBuilder()
                .setResp(temp)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    //----------OrderItem----------\\


    @Override
    public void getAllOrderItemsFromOrder(getAllOrderItemsFromOrderRequest request, StreamObserver<getAllOrderItemsFromOrderResponse> responseObserver) {
        ArrayList<DtoOrderItem> orderItems=orderDao.getOrderItemsById(request.getOrderId());

        getAllOrderItemsFromOrderResponse res=getAllOrderItemsFromOrderResponse.newBuilder()
                        .addAllOrderItems(orderItems)
                        .build();

        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

    @Override
    public void getAllOrderItemsByGroup(getAllOrderItemsByGroupRequest request, StreamObserver<getAllOrderItemsByGroupResponse> responseObserver) {
        ArrayList<DtoOrderItem> orderItems=orderDao.getOrderItemsByGroup(request.getOrderId());

        getAllOrderItemsByGroupResponse res=getAllOrderItemsByGroupResponse.newBuilder()
                .addAllOrderItems(orderItems)
                .build();

        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

    //----------Receipt----------\\


    @Override
    public void getPendingFarmersReceipt(getPendingReceiptsByFarmerRequest request, StreamObserver<getReceiptsResponse> responseObserver) {
        ArrayList<DtoSendReceipt> receipts=receiptDao.getPendingReceiptsByFarmer(request.getFarmer());

        getReceiptsResponse res=getReceiptsResponse.newBuilder()
                .addAllReceipts(receipts)
                .build();

        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

    @Override
    public void getApprovedFarmersReceipt(getApprovedReceiptsByFarmerRequest request, StreamObserver<getReceiptsResponse> responseObserver) {
        ArrayList<DtoSendReceipt> receipts=receiptDao.getApprovedReceiptsByFarmer(request.getFarmer());

        getReceiptsResponse res=getReceiptsResponse.newBuilder()
                .addAllReceipts(receipts)
                .build();

        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

    @Override
    public void getRejectedFarmersReceipt(getRejectedReceiptsByFarmerRequest request, StreamObserver<getReceiptsResponse> responseObserver) {
        ArrayList<DtoSendReceipt> receipts=receiptDao.getRejectedReceiptsByFarmer(request.getFarmer());

        getReceiptsResponse res=getReceiptsResponse.newBuilder()
                .addAllReceipts(receipts)
                .build();

        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

    @Override
    public void getCustomersReceipt(getReceiptsByCustomerRequest request, StreamObserver<getReceiptsByCustomerResponse> responseObserver) {
        ArrayList<DtoCustomerSendReceipt> receipts=receiptDao.getReceiptsByCustomer(request.getCustomer());

        getReceiptsByCustomerResponse res=getReceiptsByCustomerResponse.newBuilder()
                .addAllReceipts(receipts)
                .build();

        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

    @Override
    public void farmersApproval(farmersApprovalRequest request, StreamObserver<generalPutResponse> responseObserver) {
        String x;
        try {
            x = receiptDao.FarmersApproval(request.getApprove(),request.getOrderId());
        } catch (Exception e) {
            x = e.getMessage();
        }

        generalPutResponse response = generalPutResponse.newBuilder()
                .setResp(x)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    //----------Comment----------\\
    @Override
    public void postComment(putCommentRequest request, StreamObserver<generalPutResponse> responseObserver) {
        String x;
        try
        {
            x = reviewDao.postComment(request.getComment());
        }
        catch (Exception e)
        {
            x = e.getMessage();
        }

        generalPutResponse response = generalPutResponse.newBuilder()
                .setResp(x)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    //----------Review----------\\
    @Override
    public void postReview(postReviewRequest request, StreamObserver<generalPutResponse> responseObserver)
    {
        String x;
        try {
            x = reviewDao.createReview(request.getReview());
        } catch (Exception e) {
            x = e.getMessage();
        }

        generalPutResponse response = generalPutResponse.newBuilder()
                .setResp(x)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getAllReviewsByFarmer(getAllReviewsByFarmerRequest request, StreamObserver<getAllReviewsByFarmerResponse> responseObserver)
    {
        ArrayList<DtoReview> list = reviewDao.getAllReviewsByFarmer(request.getFarmer());
        getAllReviewsByFarmerResponse response = getAllReviewsByFarmerResponse.newBuilder()
                .addAllAllReviews(list)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
