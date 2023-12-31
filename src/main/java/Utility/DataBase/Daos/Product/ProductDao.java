package Utility.DataBase.Daos.Product;

import sep.*;

import java.util.ArrayList;

public interface ProductDao {
    String createProduct(DtoProduct dtoProduct) throws Exception;
    ArrayList<DtoProduct> getProductsByFarmer(String farmerId,String type, double amount, double price);
    DtoProduct getProductById(int productId) throws Exception;
    ArrayList<DtoProduct> getAllProducts(String type, double amount, double price);
    String editProduct(DtoProduct dto) throws Exception;
    String deleteProduct(int id) throws Exception;
}
