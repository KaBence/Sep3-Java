package Utility.DataBase.DaoImplementations.Receipt;

import Utility.DataBase.Daos.Receipt.ReceiptDao;
import sep.DtoCustomerSendReceipt;
import sep.DtoOrderItem;
import sep.DtoReceipt;
import sep.DtoSendReceipt;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class ReceiptDaoImplementation implements ReceiptDao {


    public ReceiptDaoImplementation() {
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
    public ArrayList<DtoSendReceipt> getReceiptsByFarmer(String farmer) {
        ArrayList<DtoSendReceipt> receipts=new ArrayList<>();
        try (Connection connection=getConnection()){
            PreparedStatement ps=connection.prepareStatement("select * from receipt where farmerid=?;");
            ps.setString(1,farmer);
            ResultSet rs=ps.executeQuery();
            while (rs.next()){
                int orderId=rs.getInt(1);
                boolean processed=rs.getBoolean(2);
                String status=rs.getString(3);
                double price=rs.getDouble(4);
                String paymentMethod=rs.getString(5);
                String paymentDate=rs.getString(6);
                String note=rs.getString(7);
                String farmerId=rs.getString(8);
                String customerId=rs.getString(9);

                ps=connection.prepareStatement("select \"date\" from \"order\" where orderid=?;");
                ps.setInt(1,orderId);
                ResultSet rs1=ps.executeQuery();
                String dateOfCreation="";
                while (rs1.next()){
                    dateOfCreation=rs1.getString(1);
                }

                DtoReceipt receipt=DtoReceipt.newBuilder()
                        .setOrderId(orderId)
                        .setFarmerId(farmerId)
                        .setCustomerId(customerId)
                        .setProcessed(processed)
                        .setPrice(price)
                        .setPaymentMethod(paymentMethod)
                        .setPaymentDate(paymentDate)
                        .setText(note)
                        .setStatus(status)
                        .build();
                

                DtoSendReceipt sendReceipt=DtoSendReceipt.newBuilder()
                        .setReceipt(receipt)
                        .setDateOfCreation(dateOfCreation)
                        .build();

                receipts.add(sendReceipt);
            }
            return receipts;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<DtoCustomerSendReceipt> getReceiptsByCustomer(String customer) {
        ArrayList<DtoCustomerSendReceipt> all=new ArrayList<>();
        ArrayList<Integer> orderGroups=new ArrayList<>();
        try (Connection connection=getConnection()){
            PreparedStatement items=connection.prepareStatement("select ordergroup from \"order\" where customerid=? group by ordergroup;");
            items.setString(1,customer);
            ResultSet rsItems=items.executeQuery();
            while (rsItems.next()){
                orderGroups.add(rsItems.getInt(1));
            }

            String dateOfCreation="";
            
            for (Integer item:orderGroups){
                ArrayList<DtoReceipt> receipts=new ArrayList<>();
                ArrayList<String> farmNames=new ArrayList<>();
                double totalPrice=0;
                PreparedStatement ps=connection.prepareStatement("select * from receipt join \"order\" o on o.orderID = receipt.orderID where Receipt.customerID=? and o.orderGroup=?;");
                ps.setString(1,customer);
                ps.setInt(2,item);
                ResultSet rs=ps.executeQuery();
                while (rs.next()){

                    int orderId=rs.getInt(1);
                    boolean processed=rs.getBoolean(2);
                    String status=rs.getString(3);
                    double price=rs.getDouble(4);
                    String paymentMethod=rs.getString(5);
                    String paymentDate=rs.getString(6);
                    String note=rs.getString(7);
                    String farmerId=rs.getString(8);
                    String customerId=rs.getString(9);
                    
                    ps=connection.prepareStatement("select \"date\" from \"order\" where orderid=?;");
                    ps.setInt(1,orderId);
                    ResultSet rs1=ps.executeQuery();
                    while (rs1.next()){
                        dateOfCreation=rs1.getString(1);
                    }

                    ps=connection.prepareStatement("select farmname from farmer where phonenumber=?");
                    ps.setString(1,farmerId);
                    ResultSet rs2=ps.executeQuery();
                    while (rs2.next()){
                        farmNames.add(rs2.getString(1));
                    }

                    if (paymentDate==null)
                        paymentDate="";

                    DtoReceipt receipt=DtoReceipt.newBuilder()
                            .setOrderId(orderId)
                            .setFarmerId(farmerId)
                            .setCustomerId(customerId)
                            .setProcessed(processed)
                            .setPrice(price)
                            .setPaymentMethod(paymentMethod)
                            .setPaymentDate(paymentDate)
                            .setText(note)
                            .setStatus(status)
                            .build();

                    receipts.add(receipt);
                }
                DtoCustomerSendReceipt sendReceipt=DtoCustomerSendReceipt.newBuilder()
                        .addAllReceipts(receipts)
                        .setDateOfCreation(dateOfCreation)
                        .setCombinedPrice(totalPrice)
                        .addAllFarmNames(farmNames)
                        .build();

                all.add(sendReceipt);
            }
            return all;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<DtoSendReceipt> getPendingReceiptsByFarmer(String farmer) {
        ArrayList<DtoSendReceipt> receipts=new ArrayList<>();
        try (Connection connection=getConnection()){
            PreparedStatement ps=connection.prepareStatement(pendingRequestQuery());
            ResultSet rs=ps.executeQuery();
            while (rs.next()){
                int orderId=rs.getInt(1);
                String farmerId=rs.getString(2);
                String customerId=rs.getString(3);
                boolean processed=rs.getBoolean(4);
                String status=rs.getString(5);
                double price=rs.getDouble(6);
                String paymentMethod=rs.getString(7);
                String paymentDate=rs.getString(8);
                String note=rs.getString(9);

                ps=connection.prepareStatement("select \"date\" from \"order\" where orderid=?;");
                ps.setInt(1,orderId);
                ResultSet rs1=ps.executeQuery();
                String dateOfCreation="";
                while (rs1.next()){
                    dateOfCreation=rs1.getString(1);
                }

                DtoReceipt receipt=DtoReceipt.newBuilder()
                        .setOrderId(orderId)
                        .setFarmerId(farmerId)
                        .setCustomerId(customerId)
                        .setProcessed(processed)
                        .setPrice(price)
                        .setPaymentMethod(paymentMethod)
                        .setPaymentDate(paymentDate)
                        .setText(note)
                        .setStatus(status)
                        .build();


                DtoSendReceipt sendReceipt=DtoSendReceipt.newBuilder()
                        .setReceipt(receipt)
                        .setDateOfCreation(dateOfCreation)
                        .build();

                receipts.add(sendReceipt);
            }
            return receipts;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public ArrayList<DtoSendReceipt> getApprovedReceiptsByFarmer(String farmer) {
        ArrayList<DtoSendReceipt> all=getReceiptsByFarmer(farmer);
        ArrayList<DtoSendReceipt> receipts=new ArrayList<>();

        for (DtoSendReceipt item:all){
            if (item.getReceipt().getProcessed()&&item.getReceipt().getStatus().equals("Accepted"))
                receipts.add(item);
        }
        return receipts;
    }

    @Override
    public ArrayList<DtoSendReceipt> getRejectedReceiptsByFarmer(String farmer) {
        ArrayList<DtoSendReceipt> all=getReceiptsByFarmer(farmer);
        ArrayList<DtoSendReceipt> receipts=new ArrayList<>();

        for (DtoSendReceipt item:all){
            if (item.getReceipt().getProcessed()&&item.getReceipt().getStatus().equals("Rejected"))
                receipts.add(item);
        }
        return receipts;
    }

    @Override
    public String FarmersApproval(boolean approval, int orderId) throws Exception {
        String status;
        if (approval)
            status="Accepted";
        else
            status="Rejected";
        try (Connection connection=getConnection()){
            PreparedStatement ps=connection.prepareStatement("update \"order\" set status=? where orderid=?");
            ps.setString(1,status);
            ps.setInt(2,orderId);
            ps.executeUpdate();


            ps=connection.prepareStatement("select price,paymentmethod,text,farmerid,customerid from receipt where orderid=?;");
            ps.setInt(1,orderId);
            ResultSet rs= ps.executeQuery();
            while (rs.next()){
                double price=rs.getDouble(1);
                String paymentMethod=rs.getString(2);
                String text=rs.getString(3);
                String farmerId=rs.getString(4);
                String customerId=rs.getString(5);

                ps=connection.prepareStatement("insert into receipt (orderID, processed, status, price, paymentMethod, paymentDate, text, farmerID, customerID) values (?,?,?,?,?,?,?,?,?);");
                ps.setInt(1,orderId);
                ps.setBoolean(2,true);
                ps.setString(3,status);
                ps.setDouble(4,price);
                ps.setString(5,paymentMethod);
                ps.setDate(6,Date.valueOf(LocalDate.now()));
                ps.setString(7,text);
                ps.setString(8,farmerId);
                ps.setString(9,customerId);
                ps.executeUpdate();
            }
            

            return "Success!";
        } catch (SQLException e) {
            throw new Exception("Error: "+e.getMessage());
        }
    }

    private String pendingRequestQuery(){
        return "WITH RankedReceipts AS (\n" +
                "    SELECT\n" +
                "        orderID,\n" +
                "        farmerID,\n" +
                "        customerID,\n" +
                "        processed,\n" +
                "        status,\n" +
                "        price,\n" +
                "        paymentMethod,\n" +
                "        paymentDate,\n" +
                "        text,\n" +
                "        ROW_NUMBER() OVER (PARTITION BY orderID ORDER BY processed DESC) AS row_num\n" +
                "    FROM Receipt\n" +
                ")\n" +
                "SELECT\n" +
                "    orderID,\n" +
                "    farmerID,\n" +
                "    customerID,\n" +
                "    processed,\n" +
                "    status,\n" +
                "    price,\n" +
                "    paymentMethod,\n" +
                "    paymentDate,\n" +
                "    text\n" +
                "FROM RankedReceipts\n" +
                "WHERE row_num = 1 AND processed = false;";
    }
}
