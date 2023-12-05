package Utility.DataBase.DaoImplementations.Users;

import Utility.DataBase.Daos.Users.LoginDao;
import sep.DtoLogin;

import java.sql.*;
import java.util.ArrayList;

public class LoginDaoImplementation implements LoginDao
{
    public LoginDaoImplementation()
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
    public synchronized String login(DtoLogin dto) throws Exception {
        ArrayList<DtoLogin> list = new ArrayList<>();
        if (dto.getPhoneNumber().isEmpty())
            throw new Exception("Error: Username cannot be empty!");
        if (dto.getPassword().isEmpty())
            throw new Exception("Error: Password cannot be empty!");
        else
        {
            try (Connection connection = getConnection())
            {
                PreparedStatement ps = connection.prepareStatement(
                        "SELECT * FROM  \"user\"");
                ResultSet rs = ps.executeQuery();
                while(rs.next())
                {
                    String phonenumber= rs.getString("phonenumber");
                    String password= rs.getString("password");
                    DtoLogin x = DtoLogin.newBuilder()
                            .setPhoneNumber(phonenumber)
                            .setPassword(password)
                            .build();
                    list.add(x);
                }

                for (int i = 0; i < list.size(); i++)
                {
                    if (list.get(i).getPhoneNumber().equals(dto.getPhoneNumber()))
                    {
                        if (list.get(i).getPassword().equals(dto.getPassword()))
                            return list.get(i).getPhoneNumber();
                        throw new Exception("Error: Password mismatch");
                    }
                }
                throw new Exception("Error: User doesn't exist");
            }
            catch (SQLException e)
            {
                throw new Exception("Error: Internal data base Error!\n"+e.getMessage());
            }
        }
    }
}
