package Utility.DataBase.Daos.Users;

import sep.DtoFarmer;
import sep.DtoRegisterFarmer;

import java.util.ArrayList;

public interface FarmerDao
{
    ArrayList<DtoFarmer> getAllFarmers();
    DtoFarmer getFarmersById(String phoneNo);
    String editFarmer(DtoRegisterFarmer editedFarmer);
}
