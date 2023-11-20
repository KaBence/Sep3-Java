package Utility.DataBase.DaoImplementations.Users;

import Utility.DataBase.Daos.Users.FarmerDao;
import sep.DtoCustomer;
import sep.DtoFarmer;
import sep.DtoRegisterFarmer;

import java.sql.*;
import java.util.ArrayList;

public class FarmerDaoImplementation implements FarmerDao
{
    public FarmerDaoImplementation()
    {
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
    public ArrayList<DtoFarmer> getAllFarmers()
    {
        ArrayList<DtoFarmer> list = new ArrayList<>();
        try (Connection connection = getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM  Farmer");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                String phonenumber = rs.getString("phonenumber");
                String fistName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                String address = rs.getString("address");
                boolean pesticides = rs.getBoolean("pestecides");
                String farmName = rs.getString("farmName");
                double rating = rs.getDouble("rating");
                DtoFarmer x = DtoFarmer.newBuilder()
                        .setPhoneNumber(phonenumber)
                        .setFirstName(fistName)
                        .setLastName(lastName)
                        .setAddress(address)
                        .setPesticides(pesticides)
                        .setFarmName(farmName)
                        .setRating(rating)
                        .build();
                list.add(x);
            }
            return list;
        } catch (SQLException e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public DtoFarmer getFarmersById(String phoneNo)
    {
        ArrayList<DtoFarmer> list = getAllFarmers();
        for (int i = 0; i < list.size(); i++)
        {
            if (list.get(i).getPhoneNumber().equals(phoneNo))
                return list.get(i);
        }
        throw new RuntimeException("Error: Farmer with "+phoneNo+" not found!");
    }

    @Override
    public String editFarmer(DtoRegisterFarmer editedFarmer)
    {
        String phoneNo = editedFarmer.getPhoneNumber();
        String password = editedFarmer.getPassword();
        String repeatedPassword = editedFarmer.getPassword();
        String firstName = editedFarmer.getFirstName();
        String lastName = editedFarmer.getLastName();
        String address = editedFarmer.getAddress();
        boolean pesticides = editedFarmer.getPesticides();
        String farmName = editedFarmer.getFarmName();
        double rating = editedFarmer.getRating();

        if (phoneNo.isEmpty())
            return "Error: Phone number cannot be empty!";
        if (password.isEmpty())
            return "Error: Password cannot be empty!";
        if (repeatedPassword.isEmpty())
            return "Error: Reaped Password cannot be empty!";
        if (!password.equals(repeatedPassword))
            return "Error: Passwords mismatch!";
        if (firstName.isEmpty())
            return "Error: First name cannot be empty!";
        if (lastName.isEmpty())
            return "Error: Last name cannot be empty!";
        if (address.isEmpty())
            return "Error: Address cannot be empty!";
        if (farmName.isEmpty())
            return  "Error: Farm name cannot be empty!";
        if (phoneNo.length()>=50)
            return "Error: Phone number cannot be longer than 50 characters!";
        if (password.length()>=50)
            return "Error: Password cannot be longer than 50 characters!";
        if (firstName.length()>=50)
            return "Error: First name cannot be longer than 50 characters!";
        if (lastName.length()>=50)
            return "Error: Last name cannot be longer than 50 characters!";
        if (address.length()>=50)
            return "Error: Address cannot be longer than 50 characters!";
        if (farmName.length()>=50)
            return "Error: Farm name cannot be longer than 50 characters!";

        try (Connection connection = getConnection())
        {
            PreparedStatement psUser = connection.prepareStatement("Update \"user\" SET password = ? Where phonenumber = ?");
            psUser.setString(1, password);
            psUser.setString(2, phoneNo);
            psUser.executeUpdate();


            PreparedStatement psFarmer = connection.prepareStatement(
                    "UPDATE customer SET firstname = ?, lastname = ?, address = ?,pestecides = ?,farmName=?,rating =? WHERE phonenumber = ?");
            psFarmer.setString(1, firstName);
            psFarmer.setString(2, lastName);
            psFarmer.setString(3, address);
            psFarmer.setBoolean(4,pesticides);
            psFarmer.setString(5,farmName);
            psFarmer.setDouble(6,rating);
            psFarmer.setString(7, phoneNo);
            psFarmer.executeUpdate();
            return "Success!";
        }
        catch (SQLException e)
        {
            return "Error: Internal data base Error!\n"+e.getMessage();
        }
    }
}