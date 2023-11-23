package Utility.DataBase.Daos.Users;

import sep.DtoLogin;

public interface LoginDao
{
    String login(DtoLogin dto) throws Exception;
}
