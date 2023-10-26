package Utility.DataBase.String;
import java.sql.*;
import java.sql.DriverManager;
import java.util.ArrayList;

public class StringDataImplementation implements StringData{
 public StringDataImplementation(){
     try{
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
    public String addSOmeValueToThisTest(String text) {
        if (text.equals(" ")){
            return  DatabaseConnection.MANDATORY;
        }
        else {
            try(Connection connection= getConnection()){
                PreparedStatement ps= connection.prepareStatement("INSERT INTO testtable(string_test) VALUES(?)");
                ps.setString(1,text);
                ps.executeUpdate();
                return DatabaseConnection.SUCCESS;
            }
            catch (SQLException e)
            {
                return DatabaseConnection.ERROR;
            }
        }
    }

    @Override
    public ArrayList getString() {
        ArrayList list = new ArrayList<>();

     try(Connection connection= getConnection()) {
         PreparedStatement ps = connection.prepareStatement(
                 "SELECT * FROM testtable");
         ResultSet rs = ps.executeQuery();
         while(rs.next())
         {
             String message= rs.getString("string_test");
         list.add(message);
         }
         return list;
     }
     catch (SQLException e)
     {
         System.out.println(e.getMessage());
         return null;
     }
    }
}
