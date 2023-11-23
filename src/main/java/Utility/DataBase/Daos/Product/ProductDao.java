package Utility.DataBase.Daos.Product;

import sep.DtoProduct;

import java.util.ArrayList;

public interface ProductDao {
    String createProduct(DtoProduct dtoProduct);
    ArrayList<DtoProduct> getProductsByFarmer(String farmerId);
    DtoProduct getProductById(int productId);
    ArrayList<DtoProduct> getAllProducts();

}
