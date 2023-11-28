package Utility.DataBase.DaoImplementations.Order;

import Utility.DataBase.DaoImplementations.Product.ProductDaoImplementation;
import Utility.DataBase.Daos.Order.OrderDao;
import Utility.DataBase.Daos.Product.ProductDao;
import sep.DtoOrder;
import sep.DtoOrderItem;
import sep.DtoProduct;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
    public String createOrder(DtoOrder order, List<DtoOrderItem> orderItems,String paymentMethod,String note) throws Exception {
        if (orderItems.isEmpty())
            throw new Exception("Error: There are no orderItems");
        List<String> farmers=new ArrayList<>();
        int orderGroup=0;
        for (DtoOrderItem item: orderItems){
            DtoProduct x=productDao.getProductById(item.getProductId());
            if (!farmers.contains(x.getFarmerId())){
                farmers.add(x.getFarmerId());
            }
        }

        try (Connection connection=getConnection()){
            for (String farmer: farmers){

                PreparedStatement ps=connection.prepareStatement("insert into \"order\"(status,date,customerId) values (?,?,?)");
                ps.setString(1,order.getStatus());
                ps.setDate(2, Date.valueOf(order.getDate()));
                System.out.println("2----------------------------------------------");
                ps.setString(3,order.getCustomerId());

                ps.executeUpdate();


                ps=connection.prepareStatement("select * from \"order\" order by orderID desc limit 1");
                ResultSet rs=ps.executeQuery();
                int id=0;
                while (rs.next()){
                    id=rs.getInt(1);
                }

                if (orderGroup==0){
                    orderGroup=id;
                }

                ps=connection.prepareStatement("update \"order\" set ordergroup=? where orderid=?;");
                ps.setInt(1,orderGroup);
                ps.setInt(2,id);
                ps.executeUpdate();

                double price=0;
                double amount =0;
                for (DtoOrderItem item:orderItems){
                    DtoProduct x=productDao.getProductById(item.getProductId());
                    if (!x.getFarmerId().equals(farmer))
                        break;
                    price+=x.getPrice();
                    amount+=x.getAmount();
                }

                for(DtoOrderItem item:orderItems){
                    DtoProduct x=productDao.getProductById(item.getProductId());
                    if (!x.getFarmerId().equals(farmer)){
                        ps=connection.prepareStatement("insert into Receipt(orderId,farmerid,customerId,processed,price,paymentmethod,text,amount) values (?,?,?,?,?,?,?,?)");
                        ps.setInt(1,id);
                        ps.setString(2,farmer);
                        ps.setString(3,order.getCustomerId());
                        ps.setBoolean(4,false);
                        ps.setDouble(5,price);
                        ps.setString(6,paymentMethod);
                        ps.setString(7,note);
                        ps.setDouble(8,amount);
                        ps.executeUpdate();
                        break;
                    }
                    ps=connection.prepareStatement("insert into orderItem(orderId,ProductId,amount) values (?,?,?)");
                    ps.setInt(1,id);
                    ps.setInt(2,item.getProductId());
                    ps.setDouble(3,item.getAmount());
                    ps.executeUpdate();

                    ps=connection.prepareStatement("update product set amount=amount-? where productid=?");
                    ps.setDouble(1,item.getAmount());
                    ps.setInt(2,item.getProductId());
                    ps.executeUpdate();
                }
            }
            return "Success!";
        }
        catch (SQLException e) {
            throw new Exception("Error: Internal data base Error!\n" + e.getMessage());
        }
        catch (Exception e){
            throw new Exception("Error: "+e.getMessage());
        }
    }
}
