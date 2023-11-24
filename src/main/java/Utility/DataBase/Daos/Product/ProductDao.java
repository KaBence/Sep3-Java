package Utility.DataBase.Daos.Product;

import sep.*;

import java.util.ArrayList;

public interface ProductDao {
    String createProduct(DtoProduct dtoProduct) throws Exception;
    ArrayList<DtoProduct> getProductsByFarmer(String farmerId);
    DtoProduct getProductById(int productId);
    ArrayList<DtoProduct> getAllProducts();
    ArrayList<DtoProduct> getFilteredProducts(ProductSearchParameters dto);
    String editProduct(DtoProduct dto);
    String deleteProduct(int id) throws Exception;
}
