package Utility.DataBase.Daos.Product;

import sep.*;

import java.util.ArrayList;

public interface ProductDao {
    String createProduct(DtoProduct dtoProduct) throws Exception;
    ArrayList<DtoProduct> getProductsByFarmer(String farmerId);
    DtoProduct getProductById(int productId);
    ArrayList<DtoProduct> getAllProducts(String type, double amount, double price);
    //ArrayList<DtoProduct> getFilteredProducts(ProductSearchParameters dto);
    String editProduct(DtoProduct dto) throws Exception;
    String deleteProduct(int id) throws Exception;
}
