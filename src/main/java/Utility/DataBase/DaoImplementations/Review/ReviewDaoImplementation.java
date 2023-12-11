package Utility.DataBase.DaoImplementations.Review;

import Utility.DataBase.Daos.Review.ReviewDao;
import sep.DtoComment;
import sep.DtoReview;

import java.sql.*;
import java.util.ArrayList;

public class ReviewDaoImplementation implements ReviewDao
{
    public ReviewDaoImplementation() {
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
    public synchronized String createReview(DtoReview dto) throws Exception
    {
        if (dto.getText().isEmpty())
            throw new Exception("Error: Text cannot be empty!");
        if (dto.getStar()<1||dto.getStar()>5)
            throw new Exception("Error: Star must be from range 1-5!");
        else {
            try (Connection connection = getConnection()) {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO Review(text, star, farmerID, customerID,orderId) VALUES (?,?,?,?,?)");
                ps.setString(1, dto.getText());
                ps.setDouble(2, dto.getStar());
                ps.setString(3, dto.getFarmerId());
                ps.setString(4, dto.getCustomerId());
                ps.setInt(5,dto.getOrderId());
                ps.executeUpdate();
                return "Success!";

            } catch (SQLException e)
            {
                if (e.getMessage().contains("ERROR: duplicate key value violates unique constraint"))
                {
                    throw new Exception("Error: You already left the review on this order");
                }
                throw new Exception("Error: Internal data base Error!\n" + e.getMessage());
            }
        }
    }

    @Override
    public synchronized ArrayList<DtoReview> getAllReviewsByFarmer(String farmer)
    {
        ArrayList<DtoReview> list = new ArrayList<>();

        try(Connection connection = getConnection())
        {
           PreparedStatement ps = connection.prepareStatement("select * from Review Where farmerid = ?");
           ps.setString(1,farmer);

           ResultSet rs = ps.executeQuery();

           while (rs.next())
           {
               String text = rs.getString(1);
               double star = rs.getDouble(2);
               String farmerId = rs.getString(3);
               String customerId = rs.getString(4);
               int orderId = rs.getInt(5);
               ArrayList<DtoComment> comments = getAllCommentsByReview(farmerId,customerId,orderId);

               DtoReview x = DtoReview.newBuilder()
                       .setText(text)
                       .setStar(star)
                       .setFarmerId(farmerId)
                       .setCustomerId(customerId)
                       .setOrderId(orderId)
                       .addAllComments(comments)
                       .build();

               list.add(x);
           }
           return list;
        }
        catch (SQLException e)
        {
           throw new RuntimeException(e.getMessage());
       }
    }

    @Override
    public synchronized String postComment(DtoComment comment) throws Exception {
        if (comment.getText().isEmpty()) {
            throw new Exception("Error: Text cannot be empty!");
        }
        if (comment.getFarmerId().equals(""))
            throw new Exception("Error: Please select a farmer");
        else
        {
            try (Connection connection = getConnection())
            {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO Comment(text,farmerID,customerID,orderId,username) VALUES (?,?,?,?,?)");
                ps.setString(1,comment.getText());
                ps.setString(2,comment.getFarmerId());
                ps.setString(3,comment.getCustomerId());
                ps.setInt(4,comment.getOrderId());
                ps.setString(5,comment.getUsername());
                ps.executeUpdate();
                return "Success!";
            } catch (SQLException e) {
                throw new Exception("Error: Internal data base Error!\n" + e.getMessage());
            }
        }
    }

    @Override
    public synchronized ArrayList<DtoComment> getAllCommentsByReview(String farmer, String customer,int order)
    {
        ArrayList<DtoComment> list = new ArrayList<>();

        try (Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement("select * from Comment where farmerid = ? and customerid = ? and orderid = ?");
            ps.setString(1,farmer);
            ps.setString(2,customer);
            ps.setInt(3,order);

            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                int id = rs.getInt(1);
                String text = rs.getString(2);
                String farmerId = rs.getString(3);
                String customerId = rs.getString(4);
                int orderId = rs.getInt(5);
                String username = rs.getString(6);

                DtoComment x = DtoComment.newBuilder()
                        .setCommentId(id)
                        .setText(text)
                        .setFarmerId(farmerId)
                        .setCustomerId(customerId)
                        .setOrderId(orderId)
                        .setUsername(username)
                        .build();

                list.add(x);
            }
            return list;

        }
        catch (SQLException e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }
}
