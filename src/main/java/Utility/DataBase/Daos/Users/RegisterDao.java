package Utility.DataBase.Daos.Users;

import sep.DtoRegisterCustomer;
import sep.DtoRegisterFarmer;

public interface RegisterDao
{
    String RegisterCustomer(DtoRegisterCustomer dtoCustomer);
    String RegisterFarmer(DtoRegisterFarmer dtoFarmer);
}
