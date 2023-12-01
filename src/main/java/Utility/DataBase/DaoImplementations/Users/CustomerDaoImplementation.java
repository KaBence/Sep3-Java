package Utility.DataBase.DaoImplementations.Users;

import Utility.DataBase.Daos.Users.CustomerDao;
import sep.DtoCustomer;
import sep.DtoRegisterCustomer;

import java.sql.*;
import java.util.ArrayList;

public class CustomerDaoImplementation implements CustomerDao
{

    public CustomerDaoImplementation()
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
    public ArrayList<DtoCustomer> getAllCustomers()
    {
        ArrayList<DtoCustomer> list = new ArrayList<>();
        try (Connection connection = getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM  Customer");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                String phonenumber = rs.getString("phonenumber");
                String fistName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                String address = rs.getString("address");
                DtoCustomer x = DtoCustomer.newBuilder()
                        .setPhoneNumber(phonenumber)
                        .setFirstName(fistName)
                        .setLastName(lastName)
                        .setAddress(address)

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
    public DtoCustomer getCustomerById(String phoneNo)
    {
        ArrayList<DtoCustomer> list = getAllCustomers();
        for (int i = 0; i < list.size(); i++)
        {
            if (list.get(i).getPhoneNumber().equals(phoneNo))
                return list.get(i);
        }
        throw new RuntimeException("Error: Customer with "+phoneNo+" not found!");
    }

    @Override
    public String editCustomer(DtoRegisterCustomer editedCustomer)
    {
        String phoneNo = editedCustomer.getPhoneNumber();
        String password = editedCustomer.getPassword();
        String repeatedPassword = editedCustomer.getRepeatPassword();
        String firstName = editedCustomer.getFirstName();
        String lastName = editedCustomer.getLastName();
        String address = editedCustomer.getAddress();
        System.out.println(password);
        System.out.println(repeatedPassword);

        if (phoneNo.isEmpty())
            return "Error: Username cannot be empty!";
        if (password.isEmpty())
            return "Error: Password cannot be empty!";
        if (repeatedPassword.isEmpty())
            return "Error: Repeated Password cannot be empty!";
        if (!password.equals(repeatedPassword))
            return "Error: Passwords mismatch!";
        if (firstName.isEmpty())
            return "Error: First name cannot be empty!";
        if (lastName.isEmpty())
            return "Error: Last name cannot be empty!";
        if (address.isEmpty())
            return "Error: Address cannot be empty!";
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

        try (Connection connection = getConnection())
        {
            PreparedStatement psUser = connection.prepareStatement("Update \"user\" SET password = ? Where phonenumber = ?");
            psUser.setString(1, password);
            psUser.setString(2, phoneNo);
            psUser.executeUpdate();


            PreparedStatement psCustomer = connection.prepareStatement(
                    "UPDATE customer SET firstname = ?, lastname = ?, address = ? WHERE phonenumber = ?");
            psCustomer.setString(1, firstName);
            psCustomer.setString(2, lastName);
            psCustomer.setString(3, address);
            psCustomer.setString(4, phoneNo);
            psCustomer.executeUpdate();
            return "Success!";
        }
        catch (SQLException e)
        {
            return "Error: Internal data base Error!\n"+e.getMessage();
        }
    }
}
