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
        if (paymentMethod==null)
            throw new Exception("Error: There is no payment method");
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
                        price+=x.getPrice()*x.getAmount();
                    }
                }
                //Going through the orderitems and creating them if the next order would be for another farmer it is creating the receipt instead
                for(int j=counter;j<orderItems.size();j++){
                    DtoProduct x=productDao.getProductById(orderItems.get(j).getProductId());
                    if (!x.getFarmerId().equals(farmers.get(i))){
                        ps=connection.prepareStatement("insert into Receipt(orderId,farmerid,customerId,processed,price,paymentmethod,text) values (?,?,?,?,?,?,?)");
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
        catch (Exception e){
            throw new Exception("Error: "+e.getMessage());
        }
    }
}
