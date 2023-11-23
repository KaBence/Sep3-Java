package Utility.DataBase.DaoImplementations.Product;

import Utility.DataBase.Daos.Product.ProductDao;
import sep.DtoCustomer;
import sep.DtoProduct;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ProductDaoImplementation implements ProductDao {

    public ProductDaoImplementation()
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
    public String createProduct(DtoProduct dtoProduct) {
        if (dtoProduct.getId() == 0)
            return "Error: Product id cannot be empty";
        //availability
        if (dtoProduct.getAmount() == 0)
            return "Error: Amount cannot be empty";
        if (dtoProduct.getType().isEmpty())
            return "Error: Type cannot be empty";
        if (dtoProduct.getPrice() == 0)
            return "Error: Price cannot be empty";
        if (dtoProduct.getPickedDate().isEmpty())
            return "Error: Picked date cannot be empty";
        if (dtoProduct.getExpirationDate().isEmpty())
            return "Error: Expiration date cannot be empty";
        if (dtoProduct.getFarmerId().isEmpty())
            return "Error: Farmer id cannot be empty";

        else {
            try (Connection connection = getConnection()) {
                PreparedStatement product = connection.prepareStatement("INSERT INTO \"Product\"(productID, availability,amount, \"type\", price, pickedDate, expirationDate,farmerID) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

                product.setInt(1, dtoProduct.getId());
                product.setBoolean(2, dtoProduct.getAvailability());
                product.setDouble(3, dtoProduct.getAmount());
                product.setString(4, dtoProduct.getType());
                product.setDouble(5, dtoProduct.getPrice());
                product.setString(6, dtoProduct.getPickedDate());
                product.setString(7, dtoProduct.getExpirationDate());
                product.setString(8, dtoProduct.getFarmerId());

                product.executeUpdate();

                return "Success!";
            } catch (SQLException e) {
                if (e.getMessage().contains("ERROR: duplicate key value")) {
                    return "Error: Product id: " + dtoProduct.getId() + ", is already used in the system";
                }
                return "Error: Internal data base Error!\n" + e.getMessage();
            }
        }
    }

    @Override
    public ArrayList<DtoProduct> getProductsByFarmer(String farmerId) {
        ArrayList<DtoProduct> list = getAllProducts();
        ArrayList<DtoProduct> farmersProducts = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getFarmerId().equals(farmerId))
                farmersProducts.add(list.get(i));
        }
        return farmersProducts;
    }

    @Override
    public DtoProduct getProductById(int productId) {
        ArrayList<DtoProduct> list = getAllProducts();
        for (int i = 0; i < list.size(); i++)
        {
            if (list.get(i).getId() == productId) // will it work no idea
                return list.get(i);
        }
        throw new RuntimeException("Error: Product with "+ productId +" not found!");
    }

    @Override
    public ArrayList<DtoProduct> getAllProducts() {
        ArrayList<DtoProduct> list = new ArrayList<>();
        try (Connection connection = getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM  Product");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                int productID = rs.getInt("productID");
                boolean availability = rs.getBoolean("availability");
                double amount = rs.getDouble("amount");
                String type = rs.getString("type");
                double price = rs.getDouble("price");
                String pickedDate = rs.getString("pickedDate");
                String expirationDate = rs.getString("expirationDate");
                String farmerID =rs.getString("farmerID");

                DtoProduct x = DtoProduct.newBuilder()
                        .setId(productID)
                        .setAvailability(availability)
                        .setAmount(amount)
                        .setType(type)
                        .setPrice(price)
                        .setPickedDate(pickedDate)
                        .setExpirationDate(expirationDate)
                        .setFarmerId(farmerID)

                        .build();

                list.add(x);

            }
            return list;
        } catch (SQLException e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }

}
