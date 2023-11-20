package Utility.DataBase.DaoImplementations.Users;

import Utility.DataBase.Daos.Users.RegisterDao;
import Utility.DataBase.String.DatabaseConnection;
import sep.DtoRegisterCustomer;
import sep.DtoRegisterFarmer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterDaoImplementation implements RegisterDao
{
    public RegisterDaoImplementation()
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
    public String RegisterCustomer(DtoRegisterCustomer dtoCustomer)
    {
        if (dtoCustomer.getPhoneNumber().isEmpty())
            return "Error: Phone number cannot be empty!";
        if (dtoCustomer.getPassword().isEmpty())
            return "Error: Password cannot be empty!";
        if (dtoCustomer.getRepeatPassword().isEmpty())
            return "Error: Reaped Password cannot be empty!";
        if (!dtoCustomer.getRepeatPassword().equals(dtoCustomer.getPassword()))
            return "Error: Passwords mismatch!";
        if (dtoCustomer.getFirstName().isEmpty())
            return "Error: First name cannot be empty!";
        if (dtoCustomer.getLastName().isEmpty())
            return "Error: Last name cannot be empty!";
        if (dtoCustomer.getAddress().isEmpty())
            return "Error: Address cannot be empty!";
        if (dtoCustomer.getPhoneNumber().length()>=50)
            return "Error: Phone number cannot be longer than 50 characters!";
        if (dtoCustomer.getPassword().length()>=50)
            return "Error: Password cannot be longer than 50 characters!";
        if (dtoCustomer.getFirstName().length()>=50)
            return "Error: First name cannot be longer than 50 characters!";
        if (dtoCustomer.getLastName().length()>=50)
            return "Error: Last name cannot be longer than 50 characters!";
        if (dtoCustomer.getAddress().length()>=50)
            return "Error: Address cannot be longer than 50 characters!";
        else
        {
            try (Connection connection = getConnection())
            {
                PreparedStatement user= connection.prepareStatement("INSERT INTO  \"user\"(phonenumber,password,active) VALUES(?,?,?)");
                user.setString(1,dtoCustomer.getPhoneNumber());
                user.setString(2,dtoCustomer.getPassword());
                user.setBoolean(3,true);
                user.executeUpdate();

                PreparedStatement customer= connection.prepareStatement("INSERT INTO Customer(phonenumber,firstname,lastname,address) VALUES(?,?,?,?)");
                customer.setString(1,dtoCustomer.getPhoneNumber());
                customer.setString(2,dtoCustomer.getFirstName());
                customer.setString(3,dtoCustomer.getLastName());
                customer.setString(4,dtoCustomer.getAddress());
                customer.executeUpdate();

                return "Success!";
            }
            catch (SQLException e)
            {
                if (e.getMessage().contains("ERROR: duplicate key value"))
                {
                    return "Error: Phone number: "+dtoCustomer.getPhoneNumber()+", is already registered in the system";
                }
                return "Error: Internal data base Error!\n"+e.getMessage();
            }
        }
    }

    @Override
    public String RegisterFarmer(DtoRegisterFarmer dtoFarmer)
    {
        if (dtoFarmer.getPhoneNumber().isEmpty())
            return "Error: Phone number cannot be empty!";
        if (dtoFarmer.getPassword().isEmpty())
            return "Error: Password cannot be empty!";
        if (dtoFarmer.getRepeatPassword().isEmpty())
            return "Error: Reaped Password cannot be empty!";
        if (!dtoFarmer.getRepeatPassword().equals(dtoFarmer.getPassword()))
            return "Error: Passwords mismatch!";
        if (dtoFarmer.getFirstName().isEmpty())
            return "Error: First name cannot be empty!";
        if (dtoFarmer.getLastName().isEmpty())
            return "Error: Last name cannot be empty!";
        if (dtoFarmer.getAddress().isEmpty())
            return "Error: Address cannot be empty!";
        if (dtoFarmer.getFarmName().isEmpty())
            return  "Error: Farm name cannot be empty!";
        if (dtoFarmer.getPhoneNumber().length()>=50)
            return "Error: Phone number cannot be longer than 50 characters!";
        if (dtoFarmer.getPassword().length()>=50)
            return "Error: Password cannot be longer than 50 characters!";
        if (dtoFarmer.getFirstName().length()>=50)
            return "Error: First name cannot be longer than 50 characters!";
        if (dtoFarmer.getLastName().length()>=50)
            return "Error: Last name cannot be longer than 50 characters!";
        if (dtoFarmer.getAddress().length()>=50)
            return "Error: Address cannot be longer than 50 characters!";
        if (dtoFarmer.getFarmName().length()>=50)
            return "Error: Farm name cannot be longer than 50 characters!";
        else
        {
            try (Connection connection = getConnection())
            {
                PreparedStatement user= connection.prepareStatement("INSERT INTO  \"user\"(phonenumber,password,active) VALUES(?,?,?)");
                user.setString(1,dtoFarmer.getPhoneNumber());
                user.setString(2,dtoFarmer.getPassword());
                user.setBoolean(3,true);
                user.executeUpdate();

                PreparedStatement farmer= connection.prepareStatement("INSERT INTO Farmer(phonenumber,firstname,lastname,address,pestecides,farmName,rating) VALUES(?,?,?,?,?,?,?)");
                farmer.setString(1,dtoFarmer.getPhoneNumber());
                farmer.setString(2,dtoFarmer.getFirstName());
                farmer.setString(3,dtoFarmer.getLastName());
                farmer.setString(4,dtoFarmer.getAddress());
                farmer.setBoolean(5,dtoFarmer.getPesticides());
                farmer.setString(6,dtoFarmer.getFarmName());
                farmer.setDouble(7,dtoFarmer.getRating());
                farmer.executeUpdate();

                return "Success!";
            }
            catch (SQLException e)
            {
                if (e.getMessage().contains("ERROR: duplicate key value"))
                {
                    return "Error: Phone number: "+dtoFarmer.getPhoneNumber()+", is already registered in the system";
                }
                return "Error: Internal data base Error!\n"+e.getMessage();
            }
        }
    }
}
