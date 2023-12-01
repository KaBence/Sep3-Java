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

    //now its not possible to get only the farmers without pesticides, or allFarmers for the getFarmer method, solve that or try whith the sql statement
    @Override
    public ArrayList<DtoFarmer> getAllFarmers(int pesticides, String farmName, double rating) {
        ArrayList<DtoFarmer> list = new ArrayList<>();
        Boolean pest=null;
        if(pesticides==1){
            pest=true;
        }
        if(pesticides==2){
            pest=false;
        }
        try (Connection connection = getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM  Farmer");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String phonenumber = rs.getString("phonenumber");
                String fistName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                String address = rs.getString("address");
                boolean pesticidesOriginal = rs.getBoolean("pestecides");
                String farmNameOriginal = rs.getString("farmName");
                double ratingOriginal = rs.getDouble("rating");
               if ((  pest==null||pest == pesticidesOriginal ) &&
                        (farmName.equals(farmNameOriginal) || farmName.isEmpty()) &&
                        (rating == ratingOriginal || rating == 0.0)) {
                    DtoFarmer x = DtoFarmer.newBuilder()
                            .setPhoneNumber(phonenumber)
                            .setFirstName(fistName)
                            .setLastName(lastName)
                            .setAddress(address)
                            .setPesticides(pesticidesOriginal)
                            .setFarmName(farmNameOriginal)
                            .setRating(ratingOriginal)
                            .build();
                    list.add(x);

                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());}

        // here just try to do more ifs or combine it somehow

        }




    @Override
    public DtoFarmer getFarmersById(String phoneNo)
    {
        int pesticides= 0;
        String farmName= "";
        double rating=0.0;
        ArrayList<DtoFarmer> list = getAllFarmers(pesticides,farmName,rating);
        for (int i = 0; i < list.size(); i++)
        {
            if (list.get(i).getPhoneNumber().equals(phoneNo))
                return list.get(i);
        }
        throw new RuntimeException("Error: Farmer with "+phoneNo+" not found!");
    }

    //DtoRegisterFarmer is used for edit farmer as well
    @Override
    public String editFarmer(DtoRegisterFarmer editedFarmer)
    {
        String phoneNo = editedFarmer.getPhoneNumber();
        String password = editedFarmer.getPassword();
        String repeatedPassword = editedFarmer.getRepeatPassword();
        String firstName = editedFarmer.getFirstName();
        String lastName = editedFarmer.getLastName();
        String address = editedFarmer.getAddress();
        boolean pesticides = editedFarmer.getPesticides();
        String farmName = editedFarmer.getFarmName();

        if (phoneNo.isEmpty())
            return "Error: Username cannot be empty!";
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
            return "Error: Username cannot be longer than 50 characters!";
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
                    "UPDATE Farmer SET firstname = ?, lastname = ?, address = ?,pestecides = ?,farmName=? WHERE phonenumber = ?");
            psFarmer.setString(1, firstName);
            psFarmer.setString(2, lastName);
            psFarmer.setString(3, address);
            psFarmer.setBoolean(4,pesticides);
            psFarmer.setString(5,farmName);
            psFarmer.setString(6, phoneNo);
            psFarmer.executeUpdate();
            return "Success!";
        }
        catch (SQLException e)
        {
            return "Error: Internal data base Error!\n"+e.getMessage();
        }
    }
}
