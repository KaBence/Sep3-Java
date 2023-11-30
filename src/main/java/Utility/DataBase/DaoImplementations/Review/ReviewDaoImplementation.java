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
    public String createReview(DtoReview dto) throws Exception
    {
        if (dto.getText().isEmpty())
            throw new Exception("Error: Text cannot be empty!");
        if (dto.getStar()<1||dto.getStar()>5)
            throw new Exception("Error: Star must be from range 1-5!");
        else {
            try (Connection connection = getConnection()) {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO Review(text, star, farmerID, customerID) VALUES (?,?,?,?)");
                ps.setString(1, dto.getText());
                ps.setDouble(2, dto.getStar());
                ps.setString(3, dto.getFarmerId());
                ps.setString(4, dto.getCustomerId());
                ps.executeUpdate();
                return "Success!";

            } catch (SQLException e) {
                throw new Exception("Error: Internal data base Error!\n" + e.getMessage());
            }
        }
    }

    @Override
    public ArrayList<DtoReview> getAllReviewsByFarmer(String farmer)
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
               ArrayList<DtoComment> comments = getAllCommentsByReview(farmerId,customerId);

               DtoReview x = DtoReview.newBuilder()
                       .setText(text)
                       .setStar(star)
                       .setFarmerId(farmerId)
                       .setCustomerId(customerId)
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
    public String postComment(DtoComment comment) throws Exception {
        if (comment.getText().isEmpty()) {
            throw new Exception("Error: Text cannot be empty!");
        }
        else
        {
            try (Connection connection = getConnection())
            {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO Comment(text,farmerID,customerID) VALUES (?,?,?)");
                ps.setString(1,comment.getText());
                ps.setString(2,comment.getFarmerId());
                ps.setString(3,comment.getCustomerId());
                ps.executeUpdate();
                return "Success!";
            } catch (SQLException e) {
                throw new Exception("Error: Text cannot be empty!");
            }
        }
    }

    @Override
    public ArrayList<DtoComment> getAllCommentsByReview(String farmer, String customer)
    {
        ArrayList<DtoComment> list = new ArrayList<>();

        try (Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement("select * from Comment where farmerid = ? and customerid = ?");
            ps.setString(1,farmer);
            ps.setString(2,customer);

            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                int id = rs.getInt(1);
                String text = rs.getString(2);
                String farmerId = rs.getString(3);
                String customerId = rs.getString(4);

                DtoComment x = DtoComment.newBuilder()
                        .setCommentId(id)
                        .setText(text)
                        .setFarmerId(farmerId)
                        .setCustomerId(customerId)
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