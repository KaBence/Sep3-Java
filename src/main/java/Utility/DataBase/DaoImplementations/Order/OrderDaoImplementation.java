package Utility.DataBase.DaoImplementations.Order;

import Utility.DataBase.DaoImplementations.Product.ProductDaoImplementation;
import Utility.DataBase.Daos.Order.OrderDao;
import Utility.DataBase.Daos.Product.ProductDao;
import sep.DtoOrder;
import sep.DtoOrderItem;
import sep.DtoProduct;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;


public class OrderDaoImplementation implements OrderDao {

    private ProductDao productDao;
    public OrderDaoImplementation() {
        productDao=new ProductDaoImplementation();
        try
        {
            DriverManager.registerDriver(new org.postgresql.Driver());
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
    }

    private Connection getConnection() throws SQLException
    {
        return DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/postgres?currentSchema=distributionsystem",
                "postgres", "password");
    }

    @Override
    public synchronized String createOrder(DtoOrder order, List<DtoOrderItem> orderItems,String paymentMethod,String note) throws Exception {
        if (orderItems==null||orderItems.isEmpty())
            throw new Exception("Error: There are no orderItems");
        if (paymentMethod==null)
            throw new Exception("Error: There is no payment method");
        if (order==null)
            throw new Exception("Error: There is no order");
        //initialize
        List<String> farmers=new ArrayList<>();
        int orderGroup=0;
        //counts how many farmers are there in the order
        for (DtoOrderItem item: orderItems){
            DtoProduct x=productDao.getProductById(item.getProductId());
            if (!farmers.contains(x.getFarmerId())){
                farmers.add(x.getFarmerId());
            }
        }
        //adding one more farmer so the loop at orderitem recognizes that it is the end and create the receipt
        farmers.add("--------");
        int counter=0;
        double price=0;
        PreparedStatement ps;
        int id=0;
        try (Connection connection=getConnection()){
            for (int i=0;i< farmers.size();i++){
                if (!farmers.get(i).equals("--------")){
                    //creating the orders
                    ps=connection.prepareStatement("insert into \"order\"(status,date,customerId) values (?,?,?)");
                    ps.setString(1,"Pending");
                    ps.setDate(2, Date.valueOf(LocalDate.now()));
                    ps.setString(3,order.getCustomerId());
                    ps.executeUpdate();

                    //getting the orderid for the orderItems
                    ps=connection.prepareStatement("select * from \"order\" order by orderID desc limit 1");
                    ResultSet rs=ps.executeQuery();
                    while (rs.next()){
                        id=rs.getInt(1);
                    }
                    //saving the first id for orderGroup
                    if (orderGroup==0){
                        orderGroup=id;
                    }
                    //setting the orderGroup for all orders
                    ps=connection.prepareStatement("update \"order\" set ordergroup=? where orderid=?;");
                    ps.setInt(1,orderGroup);
                    ps.setInt(2,id);
                    ps.executeUpdate();
                    //Calculating the price for the receipt

                    for (int j=counter;j<orderItems.size();j++){
                        DtoProduct x=productDao.getProductById(orderItems.get(j).getProductId());
                        if (!x.getFarmerId().equals(farmers.get(i)))
                            break;
                        price+=x.getPrice()*orderItems.get(j).getAmount();
                    }
                }
                //Going through the orderitems and creating them if the next order would be for another farmer it is creating the receipt instead
                for(int j=counter;j<orderItems.size();j++){
                    DtoProduct x=productDao.getProductById(orderItems.get(j).getProductId());
                    if (!x.getFarmerId().equals(farmers.get(i))){
                        ps=connection.prepareStatement("insert into Receipt(orderId,farmerid,customerId,processed,price," +
                                "paymentmethod,text,status) values (?,?,?,?,?,?,?,?)");
                        ps.setInt(1,id);
                        if (farmers.get(i).equals("--------")){
                            ps.setString(2, farmers.get(--i));
                            i++;
                        }
                        else
                            ps.setString(2,farmers.get(i));
                        ps.setString(3,order.getCustomerId());
                        ps.setBoolean(4,false);
                        ps.setDouble(5,price);
                        ps.setString(6,paymentMethod);
                        ps.setString(7,note);
                        ps.setString(8,"Pending");
                        ps.executeUpdate();
                        counter=j;
                        break;
                    }
                    ps=connection.prepareStatement("insert into orderItem(orderId,ProductId,amount) values (?,?,?)");
                    ps.setInt(1,id);
                    ps.setInt(2,orderItems.get(j).getProductId());
                    ps.setDouble(3,orderItems.get(j).getAmount());
                    ps.executeUpdate();

                    ps=connection.prepareStatement("update product set amount=amount-? where productid=?");
                    ps.setDouble(1,orderItems.get(j).getAmount());
                    ps.setInt(2,orderItems.get(j).getProductId());
                    ps.executeUpdate();
                }
            }
            return "Success!";
        }
        catch (SQLException e) {
            throw new Exception("Error: Internal data base Error!\n" + e.getMessage());
        }
    }

    @Override
    public synchronized ArrayList<DtoOrderItem> getOrderItemsById(int orderId) {
        ArrayList<DtoOrderItem> orderItems=new ArrayList<>();
        try (Connection connection=getConnection()){
            PreparedStatement ps=connection.prepareStatement("select orderID,p.productID,orderitem.amount,p.type,p.price,farmName from orderitem join distributionsystem.product p on orderitem.productID = p.productid join distributionsystem.product p2 on orderitem.productID = p2.productid join distributionsystem.farmer f on f.phonenumber = p.farmerid where orderID=?;");
            ps.setInt(1,orderId);
            ResultSet rs= ps.executeQuery();
            while (rs.next()){
                int productId=rs.getInt(2);
                double amount=rs.getDouble(3);
                String type=rs.getString(4);
                double price=rs.getDouble(5);
                String farmName=rs.getString(6);

                DtoOrderItem orderItem=DtoOrderItem.newBuilder()
                        .setOrderId(orderId)
                        .setProductId(productId)
                        .setType(type)
                        .setAmount(amount)
                        .setPrice(price)
                        .setFarmName(farmName)
                        .build();
                orderItems.add(orderItem);
            }
            return orderItems;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized ArrayList<DtoOrderItem> getOrderItemsByGroup(int orderId) {
        ArrayList<DtoOrderItem> orderItems=new ArrayList<>();
        int orderGroup=0;
        try (Connection connection=getConnection()){
            PreparedStatement ps=connection.prepareStatement("select ordergroup from \"order\" where orderid=?;");
            ps.setInt(1,orderId);
            ResultSet rs=ps.executeQuery();
            while (rs.next()){
                orderGroup=rs.getInt(1);
            }
            ps=connection.prepareStatement("select OrderItem.orderID,p.productID,orderitem.amount,p.type,p.price,farmName from orderitem join distributionsystem.product p on orderitem.productID = p.productid join distributionsystem.product p2 on orderitem.productID = p2.productid join distributionsystem.farmer f on f.phonenumber = p.farmerid join \"order\" o on o.orderID = orderitem.orderID where o.orderGroup=?;");
            ps.setInt(1,orderGroup);
            rs=ps.executeQuery();
            while (rs.next()){
                int productId=rs.getInt(2);
                double amount=rs.getDouble(3);
                String type=rs.getString(4);
                double price=rs.getDouble(5);
                String farmName=rs.getString(6);

                DtoOrderItem orderItem=DtoOrderItem.newBuilder()
                        .setOrderId(orderId)
                        .setProductId(productId)
                        .setType(type)
                        .setAmount(amount)
                        .setPrice(price)
                        .setFarmName(farmName)
                        .build();

                orderItems.add(orderItem);
            }
            return orderItems;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
