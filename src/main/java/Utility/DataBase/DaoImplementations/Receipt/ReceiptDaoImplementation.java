package Utility.DataBase.DaoImplementations.Receipt;

import Utility.DataBase.Daos.Receipt.ReceiptDao;
import sep.DtoOrderItem;
import sep.DtoReceipt;
import sep.DtoSendReceipt;

import java.sql.*;
import java.util.ArrayList;

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
                String farmerId=rs.getString(2);
                String customerId=rs.getString(3);
                boolean processed=rs.getBoolean(4);
                double price=rs.getDouble(5);
                String paymentMethod=rs.getString(6);
                String paymentDate=rs.getString(7);
                String note=rs.getString(8);

                DtoReceipt receipt=DtoReceipt.newBuilder()
                        .setOrderId(orderId)
                        .setFarmerId(farmerId)
                        .setCustomerId(customerId)
                        .setProcessed(processed)
                        .setPrice(price)
                        .setPaymentMethod(paymentMethod)
                        .setPaymentDate(paymentDate)
                        .setText(note)
                        .build();
                

                DtoSendReceipt sendReceipt=DtoSendReceipt.newBuilder()
                        .setReceipt(receipt)
                        .build();

                receipts.add(sendReceipt);
            }
            return receipts;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<DtoSendReceipt> getReceiptsByCustomer(String customer) {
        ArrayList<DtoSendReceipt> receipts=new ArrayList<>();
        ArrayList<Integer> orderGroups=new ArrayList<>();
        try (Connection connection=getConnection()){
            PreparedStatement items=connection.prepareStatement("select ordergroup from order where customerid=? group by ordergroup;");
            items.setString(1,customer);
            ResultSet rsItems=items.executeQuery();
            while (rsItems.next()){
                orderGroups.add(rsItems.getInt(1));
            }


            for (Integer item:orderGroups){
                double totalPrice=0;
                DtoReceipt receipt=null;
                PreparedStatement ps=connection.prepareStatement("select * from receipt join \"order\" o on o.orderID = receipt.orderID where Receipt.customerID=? and o.orderGroup=?;");
                ps.setString(1,customer);
                ps.setInt(1,item);
                ResultSet rs=ps.executeQuery();
                while (rs.next()){
                    int orderId=rs.getInt(1);
                    String farmerId=rs.getString(2);
                    String customerId=rs.getString(3);
                    boolean processed=rs.getBoolean(4);
                    double price=rs.getDouble(5);
                    totalPrice+=price;
                    String paymentMethod=rs.getString(6);
                    String paymentDate=rs.getString(7);
                    String note=rs.getString(8);

                    receipt=DtoReceipt.newBuilder()
                            .setOrderId(orderId)
                            .setFarmerId(farmerId)
                            .setCustomerId(customerId)
                            .setProcessed(processed)
                            .setPrice(totalPrice)
                            .setPaymentMethod(paymentMethod)
                            .setPaymentDate(paymentDate)
                            .setText(note)
                            .build();


                }
                DtoSendReceipt sendReceipt=DtoSendReceipt.newBuilder()
                        .setReceipt(receipt)
                        .build();

                receipts.add(sendReceipt);
            }
            return receipts;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String FarmersApproval(boolean approval, int orderId) {
        if (approval)
            return accept(orderId);
        else
            return deny(orderId);
    }

    private String accept(int orderId){
        try (Connection connection=getConnection()){
            PreparedStatement ps=connection.prepareStatement("update \"order\" ");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private String deny(int orderId){
        return null;
    }
}
