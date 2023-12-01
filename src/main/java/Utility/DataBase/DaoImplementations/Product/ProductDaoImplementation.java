package Utility.DataBase.DaoImplementations.Product;

import Utility.DataBase.Daos.Product.ProductDao;
import sep.DtoCustomer;
import sep.DtoProduct;


import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
    public String createProduct(DtoProduct dtoProduct) throws Exception {
        Date picked = Date.valueOf(dtoProduct.getPickedDate());
        Date expired = Date.valueOf(dtoProduct.getExpirationDate());

        if (dtoProduct.getType().isEmpty())
            throw new Exception("Error: Type cannot be empty");
        if (dtoProduct.getPrice() == 0)
            throw new Exception("Error: Price cannot be empty");
        if (dtoProduct.getPickedDate().equals("0001-01-01"))
            throw new Exception("Error: Picked date cannot be empty");
        if (dtoProduct.getExpirationDate().equals("0001-01-01"))
            throw new Exception("Error: Expiration date cannot be empty");
        if (dtoProduct.getPickedDate().equals("9999-12-12") || dtoProduct.getExpirationDate().equals("9999-12-12"))
            throw new Exception("Error: Invalid Date - Date Format is DD/MM/YYYY");
        if (dtoProduct.getAmount() == 0)
            throw new Exception("Error: Amount cannot be empty");
        if (expired.before(picked))
            throw new Exception("Error: Expiration date cannot be before Picked date");
        else {
            try (Connection connection = getConnection()) {
                PreparedStatement product = connection.prepareStatement("INSERT INTO Product(availability,amount, \"type\", price, pickedDate, expirationDate,farmerID) VALUES (?, ?, ?, ?, ?, ?, ?)");

                product.setBoolean(1, true);
                product.setDouble(2, dtoProduct.getAmount());
                product.setString(3, dtoProduct.getType());
                product.setDouble(4, dtoProduct.getPrice());
                product.setDate(5, picked);
                product.setDate(6, expired);
                product.setString(7, dtoProduct.getFarmerId());

                product.executeUpdate();

                return "Success!";
            } catch (SQLException e) {
                throw new Exception("Error: Internal data base Error!\n" + e.getMessage());
            }
        }
    }

    @Override
    public ArrayList<DtoProduct> getProductsByFarmer(String farmerId,String type, double amount, double price) {
        ArrayList<DtoProduct> list = getAllProducts(type,amount,price);
        ArrayList<DtoProduct> farmersProducts = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getFarmerId().equals(farmerId))
                farmersProducts.add(list.get(i));
        }
        return farmersProducts;
    }

    @Override
    public DtoProduct getProductById(int productId) {
        String type="";
        double amount=0.0;
        double price=0.0;
        ArrayList<DtoProduct> list = getAllProducts(type,amount,price);
        for (int i = 0; i < list.size(); i++)
        {
            if (list.get(i).getId() == productId) // will it work no idea
                return list.get(i);
        }
        throw new RuntimeException("Error: Product with "+ productId +" not found!");
    }

    @Override
    public ArrayList<DtoProduct> getAllProducts(String type, double amount, double price) {
        ArrayList<DtoProduct> list = new ArrayList<>();
        try (Connection connection = getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM  Product where availability=true");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                int productID = rs.getInt("productID");
                boolean availability = rs.getBoolean("availability");
                double amountOriginal = rs.getDouble("amount");
                String typeOriginal = rs.getString("type");
                double priceOriginal = rs.getDouble("price");
                String pickedDate = rs.getString("pickedDate");
                String expirationDate = rs.getString("expirationDate");
                String farmerID =rs.getString("farmerID");
                System.out.println("type " + type +" OriginalType " +typeOriginal);
                System.out.println("amount "+amount +" OriginalAmount " + amountOriginal);
                System.out.println("price " +price +" PriceOriginal " +priceOriginal);
                if ((type == "" || type.equals(typeOriginal)) &&
                        (amount <= 0.0 || amount <= amountOriginal) &&
                        (price <= 0.0 || price >= priceOriginal))
                {
                DtoProduct x = DtoProduct.newBuilder()
                        .setId(productID)
                        .setAvailability(availability)
                        .setAmount(amountOriginal)
                        .setType(typeOriginal)
                        .setPrice(priceOriginal)
                        .setPickedDate(pickedDate)
                        .setExpirationDate(expirationDate)
                        .setFarmerId(farmerID)
                        .build();

                    list.add(x);
                }
            }
            return list;
        } catch (SQLException e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }

    /*  @Override //THIS SHIT MAY NOT BE WORKING IF SO CHECK WHAT IS NULL THEN CHECK WHAT IS NOT NULL THEN COMBINE THE FILTERS
    public ArrayList<DtoProduct> getFilteredProducts(ProductSearchParameters dto) {
        ArrayList<DtoProduct> all = getAllProducts();
        ArrayList<DtoProduct> filtered = new ArrayList<>();

      if (!dto.getType().isEmpty())
        {
            for (int i = 0; i < all.size(); i++) {
               if(dto.getType().equals(all.get(i).getType()))
               {
                   filtered.add(all.get(i));
               }
            }
        }
        if (dto.getPrice()!= 0)
        {
            if(filtered.isEmpty())
            {
                for (int i = 0; i < all.size(); i++) {
                    if(dto.getPrice()<= all.get(i).getPrice())
                    {
                        filtered.add(all.get(i));
                    }
                }
            }
            else {
                for (int i = 0; i < filtered.size(); i++) {
                    if(dto.getPrice()> filtered.get(i).getPrice())
                    {
                        if (filtered.remove(i)!=null)
                        {
                            i--;
                        }
                    }
                }
            }
        }
        if(dto.getAmount()!=0)
        {
            if(filtered.isEmpty())
            {
                for (int i = 0; i < all.size(); i++) {
                    if(dto.getAmount()<= all.get(i).getAmount())
                    {
                        filtered.add(all.get(i));
                    }
                }
            }
            else {
                for (int i = 0; i < filtered.size(); i++) {
                    if(dto.getAmount()> filtered.get(i).getAmount())
                    {
                        if (filtered.remove(i)!=null)
                        {
                            i--;
                        }
                    }
                }
            }
        }
        if(filtered.isEmpty())
            return all;
        else
            return filtered;
    }*/

    @Override
    public String editProduct(DtoProduct dto) throws Exception {
        boolean avl=true;
        Date exDate=Date.valueOf(dto.getExpirationDate());
        Date pDate= Date.valueOf(dto.getPickedDate());
        try (Connection connection=getConnection()){
            if (dto.getAmount()==0)
                avl=false;
            if (dto.getAmount()<0)
                throw new Exception("Error : There isn't enough products");
            if (exDate.after(today()))
                avl=false;
            if (exDate.before(pDate))
                throw new Exception("Error : Expiration date cannot be before Picked date");
            PreparedStatement ps=connection.prepareStatement("update product set availability=?, amount=?,\"type\"=?,price=?,pickedDate=?,expirationDate=? where productid=?");
            ps.setBoolean(1,avl);
            ps.setDouble(2,dto.getAmount());
            ps.setString(3,dto.getType());
            ps.setDouble(4,dto.getPrice());
            ps.setDate(5,Date.valueOf(dto.getPickedDate()));
            ps.setDate(6,Date.valueOf(dto.getExpirationDate()));
            ps.setInt(7,dto.getId());
            ps.executeUpdate();
            return "Success!";

        } catch (SQLException e) {
            throw new Exception("Error: "+e.getMessage());
        }
    }

    @Override
    public String deleteProduct(int id) throws Exception {
        try(Connection connection = getConnection())
        {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM product WHERE productID = ?");
            ps.setInt(1,id);
            ps.executeUpdate();
            return "Success!";
        }
        catch (SQLException e)
        {
            throw new Exception("Error: Unable to remove product with id "+id);
        }
    }


    private Date today() {
        LocalDate current = LocalDate.now();
        return new Date(current.getDayOfMonth(), current.getMonthValue(), current.getYear());
    }
}
