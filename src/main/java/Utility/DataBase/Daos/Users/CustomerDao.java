package Utility.DataBase.Daos.Users;

import sep.DtoCustomer;
import sep.DtoRegisterCustomer;

import java.util.ArrayList;

public interface CustomerDao
{
   ArrayList<DtoCustomer> getAllCustomers();
   DtoCustomer getCustomerById(String phoneNo);
   String editCustomer(DtoRegisterCustomer editedCustomer);
}
