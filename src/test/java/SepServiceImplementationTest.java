import Server.SepServiceImplementation;
import Utility.DataBase.DaoImplementations.Order.OrderDaoImplementation;
import Utility.DataBase.DaoImplementations.Product.ProductDaoImplementation;
import Utility.DataBase.Daos.Order.OrderDao;
import Utility.DataBase.Daos.Product.ProductDao;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import sep.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

public class SepServiceImplementationTest {

        private ManagedChannel managedChannel;
        private SepServiceGrpc.SepServiceBlockingStub stub;
        private DtoOrder dtoOrder;
        private ArrayList<DtoOrderItem> dtoOrderItems;
        private DtoProduct dtoProduct;
        protected OrderDao orderDao;
        protected ProductDao productDao;
        protected SepServiceImplementation service;

        @BeforeEach
        void setUp() {
                orderDao = Mockito.mock(OrderDaoImplementation.class);
                productDao = Mockito.mock(ProductDaoImplementation.class);
                service = new SepServiceImplementation(orderDao, productDao);
                dtoOrder = DtoOrder.newBuilder()
                                .setOrderId(1)
                                .setStatus("Pending")
                                .setCustomerId("custTest")
                                .setDate("2023-12-10")
                                .build();
                dtoOrderItems = new ArrayList<>();
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
                dtoProduct = DtoProduct.newBuilder()
                                .setAmount(10)
                                .setAvailability(true)
                                .setType("Potato")
                                .setPrice(140)
                                .setExpirationDate("2023-11-20")
                                .setPickedDate("2023-10-10")
                                .setFarmerId("Test")
                                .build();
        }

        @Test
        public void CreateOrderTest() throws Exception {
                createOrderRequest request = createOrderRequest.newBuilder()
                                .setNewOrder(dtoOrder)
                                .addAllOrderItems(dtoOrderItems)
                                .setNote("Note")
                                .setPaymentMethod("Test")
                                .build();
                List<DtoOrderItem> orderItems = new ArrayList<>(request.getOrderItemsList());
                Collections.sort(orderItems, Comparator.comparing(DtoOrderItem::getFarmName));
                Mockito.when(orderDao.createOrder(request.getNewOrder(), orderItems, request.getPaymentMethod(),
                                request.getNote()))
                                .thenReturn("Success!");

                TestServer testServer = new TestServer();
                Thread thread = new Thread(testServer);
                thread.start();
                TimeUnit.SECONDS.sleep(2);
                managedChannel = ManagedChannelBuilder
                                .forAddress("localhost", 1337)
                                .usePlaintext()
                                .build();
                stub = SepServiceGrpc.newBlockingStub(managedChannel);

                generalPutResponse res = stub.createNewOrder(request);

                assertEquals("Success!", res.getResp());
        }

        @Test
        void CreateOrderWithoutItems() throws Exception {
                createOrderRequest request = createOrderRequest.newBuilder()
                                .setNewOrder(dtoOrder)
                                .addAllOrderItems(new ArrayList<>())
                                .setNote("Note")
                                .setPaymentMethod("Test")
                                .build();
                List<DtoOrderItem> orderItems = new ArrayList<>(request.getOrderItemsList());
                Collections.sort(orderItems, Comparator.comparing(DtoOrderItem::getFarmName));
                Mockito.when(orderDao.createOrder(request.getNewOrder(), orderItems, request.getPaymentMethod(),
                                request.getNote())).thenThrow(new Exception("Error: There are no orderItems"));

                TestServer testServer = new TestServer();
                Thread thread = new Thread(testServer);
                thread.start();
                TimeUnit.SECONDS.sleep(2);
                managedChannel = ManagedChannelBuilder
                                .forAddress("localhost", 1337)
                                .usePlaintext()
                                .build();
                stub = SepServiceGrpc.newBlockingStub(managedChannel);

                generalPutResponse res = stub.createNewOrder(request);

                assertEquals("Error: There are no orderItems", res.getResp());
        }

        @Test
        void CreateOrderWithoutPayment() throws Exception {
                createOrderRequest request = createOrderRequest.newBuilder()
                                .setNewOrder(dtoOrder)
                                .addAllOrderItems(dtoOrderItems)
                                .setNote("Note")
                                .build();
                List<DtoOrderItem> orderItems = new ArrayList<>(request.getOrderItemsList());
                Collections.sort(orderItems, Comparator.comparing(DtoOrderItem::getFarmName));
                Mockito.when(orderDao.createOrder(request.getNewOrder(), orderItems, request.getPaymentMethod(),
                                request.getNote())).thenThrow(new Exception("Error: There is no payment method"));

                TestServer testServer = new TestServer();
                Thread thread = new Thread(testServer);
                thread.start();
                TimeUnit.SECONDS.sleep(2);
                managedChannel = ManagedChannelBuilder
                                .forAddress("localhost", 1337)
                                .usePlaintext()
                                .build();
                stub = SepServiceGrpc.newBlockingStub(managedChannel);

                generalPutResponse res = stub.createNewOrder(request);

                assertEquals("Error: There is no payment method", res.getResp());
        }

        @Test
        void CreateOrderWithoutOrder() throws Exception {
                createOrderRequest request = createOrderRequest.newBuilder()
                                .addAllOrderItems(dtoOrderItems)
                                .setNote("Note")
                                .setPaymentMethod("Test")
                                .build();
                List<DtoOrderItem> orderItems = new ArrayList<>(request.getOrderItemsList());
                Collections.sort(orderItems, Comparator.comparing(DtoOrderItem::getFarmName));
                Mockito.when(orderDao.createOrder(request.getNewOrder(), orderItems, request.getPaymentMethod(),
                                request.getNote())).thenThrow(new Exception("Error: There is no order"));

                TestServer testServer = new TestServer();
                Thread thread = new Thread(testServer);
                thread.start();
                TimeUnit.SECONDS.sleep(2);
                managedChannel = ManagedChannelBuilder
                                .forAddress("localhost", 1337)
                                .usePlaintext()
                                .build();
                stub = SepServiceGrpc.newBlockingStub(managedChannel);

                generalPutResponse res = stub.createNewOrder(request);

                assertEquals("Error: There is no order", res.getResp());
        }

        @Test
        void getOrderItemsByIdTest() throws Exception {

                getAllOrderItemsFromOrderRequest request = getAllOrderItemsFromOrderRequest.newBuilder()
                                .setOrderId(1)
                                .build();
                Mockito.when(orderDao.getOrderItemsById(request.getOrderId())).thenReturn(dtoOrderItems);

                TestServer testServer = new TestServer();
                Thread thread = new Thread(testServer);
                thread.start();
                TimeUnit.SECONDS.sleep(2);
                managedChannel = ManagedChannelBuilder
                                .forAddress("localhost", 1337)
                                .usePlaintext()
                                .build();
                stub = SepServiceGrpc.newBlockingStub(managedChannel);

                getAllOrderItemsFromOrderResponse res = stub.getAllOrderItemsFromOrder(request);
                assertEquals(dtoOrderItems, res.getOrderItemsList());
        }

        @Test
        void getOrderItemsByItemByIdWithoutId() throws InterruptedException {

                getAllOrderItemsFromOrderRequest request = getAllOrderItemsFromOrderRequest.newBuilder()
                                .setOrderId(0)
                                .build();
                Mockito.when(orderDao.getOrderItemsById(request.getOrderId())).thenReturn(new ArrayList<>());

                TestServer testServer = new TestServer();
                Thread thread = new Thread(testServer);
                thread.start();
                TimeUnit.SECONDS.sleep(2);
                managedChannel = ManagedChannelBuilder
                                .forAddress("localhost", 1337)
                                .usePlaintext()
                                .build();
                stub = SepServiceGrpc.newBlockingStub(managedChannel);

                getAllOrderItemsFromOrderResponse res = stub.getAllOrderItemsFromOrder(request);
                assertEquals(new ArrayList<>(), res.getOrderItemsList());
        }

        @Test
        void getOrderItemsByItemByGroup() throws InterruptedException {

                getAllOrderItemsByGroupRequest request = getAllOrderItemsByGroupRequest.newBuilder()
                                .setOrderId(1)
                                .build();
                Mockito.when(orderDao.getOrderItemsById(request.getOrderId())).thenReturn(dtoOrderItems);

                TestServer testServer = new TestServer();
                Thread thread = new Thread(testServer);
                thread.start();
                TimeUnit.SECONDS.sleep(2);
                managedChannel = ManagedChannelBuilder
                                .forAddress("localhost", 1337)
                                .usePlaintext()
                                .build();
                stub = SepServiceGrpc.newBlockingStub(managedChannel);

                getAllOrderItemsByGroupResponse res = stub.getAllOrderItemsByGroup(request);
                assertEquals(new ArrayList<>(), res.getOrderItemsList());
        }

        @Test
        void createProductTest() throws Exception {
                createProductRequest request = createProductRequest.newBuilder()
                                .setNewProduct(dtoProduct)
                                .build();

                Mockito.when(productDao.createProduct(request.getNewProduct())).thenReturn("Success!");

                TestServer testServer = new TestServer();
                Thread thread = new Thread(testServer);
                thread.start();
                TimeUnit.SECONDS.sleep(2);
                managedChannel = ManagedChannelBuilder
                                .forAddress("localhost", 1337)
                                .usePlaintext()
                                .build();
                stub = SepServiceGrpc.newBlockingStub(managedChannel);

                generalPutResponse res = stub.createProduct(request);
                assertEquals("Success!", res.getResp());
        }

        @Test
        void createProductWithoutProductTest() throws Exception {
                createProductRequest request = createProductRequest.newBuilder()
                                .build();

                Mockito.when(productDao.createProduct(request.getNewProduct()))
                                .thenThrow(new Exception("Error: There is no product"));

                TestServer testServer = new TestServer();
                Thread thread = new Thread(testServer);
                thread.start();
                TimeUnit.SECONDS.sleep(2);
                managedChannel = ManagedChannelBuilder
                                .forAddress("localhost", 1337)
                                .usePlaintext()
                                .build();
                stub = SepServiceGrpc.newBlockingStub(managedChannel);

                generalPutResponse res = stub.createProduct(request);
                assertEquals("Error: There is no product", res.getResp());
        }

        @Test
        void getProductByIdTest() throws Exception {
                DtoProduct product = DtoProduct.newBuilder()
                                .setAmount(100)
                                .setAvailability(true)
                                .setPrice(50)
                                .setId(1)
                                .setExpirationDate("2023-12-10")
                                .setPickedDate("2023-12-05")
                                .setFarmerId("Farmer")
                                .build();

                getProductByIdRequest request = getProductByIdRequest.newBuilder()
                                .setId(1)
                                .build();

                Mockito.when(productDao.getProductById(request.getId())).thenReturn(product);

                TestServer testServer = new TestServer();
                Thread thread = new Thread(testServer);
                thread.start();
                TimeUnit.SECONDS.sleep(2);
                managedChannel = ManagedChannelBuilder
                                .forAddress("localhost", 1337)
                                .usePlaintext()
                                .build();
                stub = SepServiceGrpc.newBlockingStub(managedChannel);

                getProductByIdResponse res = stub.getProductById(request);
                assertEquals(product, res.getProduct());
        }

        @Test
        void getProductWithoutIdTest() throws Exception {
                DtoProduct product = DtoProduct.newBuilder()
                                .setAmount(100)
                                .setAvailability(true)
                                .setPrice(50)
                                .setId(1)
                                .setExpirationDate("2023-12-10")
                                .setPickedDate("2023-12-05")
                                .setFarmerId("Farmer")
                                .build();

                getProductByIdRequest request = getProductByIdRequest.newBuilder()
                                .build();

                Mockito.when(productDao.getProductById(request.getId()))
                                .thenThrow(new Exception("Error: Product with " + request.getId() + " not found!"));

                TestServer testServer = new TestServer();
                Thread thread = new Thread(testServer);
                thread.start();
                TimeUnit.SECONDS.sleep(2);
                managedChannel = ManagedChannelBuilder
                                .forAddress("localhost", 1337)
                                .usePlaintext()
                                .build();
                stub = SepServiceGrpc.newBlockingStub(managedChannel);

                getProductByIdResponse res = stub.getProductById(request);
                assertEquals("Error: Product with " + request.getId() + " not found!", res.getProduct().getType());
        }

        @Test
        void EditProductTest() throws Exception {
                DtoProduct product = DtoProduct.newBuilder()
                                .setAmount(100)
                                .setAvailability(true)
                                .setPrice(50)
                                .setId(1)
                                .setExpirationDate("2023-12-10")
                                .setPickedDate("2023-12-05")
                                .setFarmerId("Farmer")
                                .build();

                updateProductRequest request = updateProductRequest.newBuilder()
                                .setProduct(product)
                                .build();

                Mockito.when(productDao.editProduct(request.getProduct())).thenReturn("Success!");

                TestServer testServer = new TestServer();
                Thread thread = new Thread(testServer);
                thread.start();
                TimeUnit.SECONDS.sleep(2);
                managedChannel = ManagedChannelBuilder
                                .forAddress("localhost", 1337)
                                .usePlaintext()
                                .build();
                stub = SepServiceGrpc.newBlockingStub(managedChannel);

                generalPutResponse res = stub.updateProduct(request);
                assertEquals("Success!", res.getResp());
        }

        @Test
        void editProductWithoutProduct() throws Exception {
                DtoProduct product = DtoProduct.newBuilder()
                                .setAmount(100)
                                .setAvailability(true)
                                .setPrice(50)
                                .setId(1)
                                .setExpirationDate("2023-12-10")
                                .setPickedDate("2023-12-05")
                                .setFarmerId("Farmer")
                                .build();

                updateProductRequest request = updateProductRequest.newBuilder()
                                .build();

                Mockito.when(productDao.editProduct(request.getProduct()))
                                .thenThrow(new Exception("Error: There is no product"));

                TestServer testServer = new TestServer();
                Thread thread = new Thread(testServer);
                thread.start();
                TimeUnit.SECONDS.sleep(2);
                managedChannel = ManagedChannelBuilder
                                .forAddress("localhost", 1337)
                                .usePlaintext()
                                .build();
                stub = SepServiceGrpc.newBlockingStub(managedChannel);

                generalPutResponse res = stub.updateProduct(request);
                assertEquals("Error: There is no product", res.getResp());
        }

        @Test
        void deleteProductTest() throws Exception {
                deleteProductRequest request = deleteProductRequest.newBuilder()
                                .setId(1)
                                .build();

                Mockito.when(productDao.deleteProduct(request.getId())).thenReturn("Success!");

                TestServer testServer = new TestServer();
                Thread thread = new Thread(testServer);
                thread.start();
                TimeUnit.SECONDS.sleep(2);
                managedChannel = ManagedChannelBuilder
                                .forAddress("localhost", 1337)
                                .usePlaintext()
                                .build();
                stub = SepServiceGrpc.newBlockingStub(managedChannel);

                generalPutResponse res = stub.deleteProduct(request);
                assertEquals("Success!", res.getResp());
        }

        @Test
        void deleteProductWithoutId() throws Exception {
                deleteProductRequest request = deleteProductRequest.newBuilder()
                                .build();

                Mockito.when(productDao.deleteProduct(request.getId())).thenThrow(new Exception("Error: id is zero"));

                TestServer testServer = new TestServer();
                Thread thread = new Thread(testServer);
                thread.start();
                TimeUnit.SECONDS.sleep(2);
                managedChannel = ManagedChannelBuilder
                                .forAddress("localhost", 1337)
                                .usePlaintext()
                                .build();
                stub = SepServiceGrpc.newBlockingStub(managedChannel);

                generalPutResponse res = stub.deleteProduct(request);
                assertEquals("Error: id is zero", res.getResp());
        }

        class TestServer implements Runnable {
                @Override
                public void run() {
                        try {
                                Server server = ServerBuilder.forPort(1337).addService(service).build();

                                server.start();
                                System.out.println("Server started...");
                                server.awaitTermination();
                        } catch (Exception e) {
                                throw new RuntimeException(e);
                        }
                }
        }

}
